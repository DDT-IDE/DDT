/*******************************************************************************
 * Copyright (c) 2014, 2014 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package dtool.engine;

import static dtool.engine.DToolServer.TIMESTAMP_FORMAT;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.CoreUtil.areEqual;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import melnorme.lang.tooling.context.BundleModules;
import melnorme.lang.tooling.context.ModuleSourceException;
import melnorme.utilbox.collections.ArrayList2;
import melnorme.utilbox.concurrency.ITaskAgent;
import dtool.dub.BundlePath;
import dtool.dub.DubBundleDescription;
import dtool.dub.DubBundleDescription.DubDescribeAnalysis;
import dtool.dub.DubHelper.RunDubDescribeCallable;
import dtool.dub.ResolvedManifest;
import dtool.engine.StandardLibraryResolution.MissingStandardLibraryResolution;
import dtool.engine.compiler_installs.CompilerInstall;
import dtool.engine.compiler_installs.CompilerInstallDetector;
import dtool.engine.compiler_installs.SearchCompilersOnPathOperation;
import dtool.engine.modules.BundleModulesVisitor;
import dtool.engine.util.CachingRegistry;
import dtool.engine.util.FileCachingEntry;
import dtool.parser.DeeParserResult.ParsedModule;

/**
 * A caching hierarchical registry.
 * This class could be generalized, but it would be a bit of a mess to handle all the parameterized types.
 */
abstract class AbstractSemanticManager {
	
	public AbstractSemanticManager() {
	}
	
	protected final CachingRegistry<BundleKey, BundleInfo> infos = new CachingRegistry<BundleKey, BundleInfo>() {
		@Override
		protected BundleInfo createEntry(BundleKey bundleKey) {
			return new BundleInfo(bundleKey);
		}
	};
	
	protected final Object entriesLock = new Object();
	
	/* -----------------  ----------------- */
	
	protected class BundleInfo {
		
		protected final BundleKey bundleKey;
		
		protected final FileCachingEntry<ResolvedManifest> manifestEntry;
		protected volatile BundleResolution bundleResolution;
		
		public BundleInfo(BundleKey bundleKey) {
			this.bundleKey = bundleKey;
			this.manifestEntry = new FileCachingEntry<>(bundleKey.getPath());
		}
		
		public ResolvedManifest getManifest() {
			return manifestEntry.getValue();
		}
		
		public BundleResolution getSemanticResolution() {
			return bundleResolution;
		}
		
		public boolean checkIsManifestStale() {
			if(manifestEntry.isStale()) {
				return true;
			}
			
			synchronized(entriesLock) {
				
				for(ResolvedManifest depBundle : getManifest().getBundleDeps()) {
					BundleInfo depBundleInfo = getInfo(depBundle.getBundleKey());
					if(depBundle != depBundleInfo.getManifest()) {
						// The manifest of the dependency is not stale, 
						// but it has changed since the parent was created, therefore parent is stale.
						return true;
					}
					if(depBundleInfo.checkIsManifestStale()) {
						return true;
					}
				}
				return false;
			}
		}
		
		public boolean checkIsResolutionStale() {
			return
					getSemanticResolution() == null ||
					checkIsManifestStale() ||					
					getSemanticResolution().checkIsStale();
		}
		
	}
	
	protected BundleInfo getInfo(BundleKey bundleKey) {
		return infos.getEntry(bundleKey);
	}
	
	public ResolvedManifest getStoredManifest(BundleKey bundleKey) {
		BundleInfo info = infos.getEntryOrNull(bundleKey);
		return info != null ? info.getManifest() : null;
	}
	
	public boolean checkIsManifestStale(BundleKey bundleKey) {
		BundleInfo info = infos.getEntryOrNull(bundleKey);
		return info == null ? true : info.checkIsManifestStale();
	}
	
	public ResolvedManifest getUpdatedManifest(BundleKey bundleKey) throws ExecutionException {
		BundleInfo info = getInfo(bundleKey);
		if(info.checkIsManifestStale()) {
			return updateManifestEntry(bundleKey);
		}
		return info.getManifest();
	}
	
	
	public final ResolvedManifest getStoredManifest(BundlePath bundlePath) {
		return getStoredManifest(new BundleKey(bundlePath));
	}
	public final boolean checkIsManifestStale(BundlePath bundlePath) {
		return checkIsManifestStale(new BundleKey(bundlePath));
	}
	public ResolvedManifest getUpdatedManifest(BundlePath bundlePath) throws ExecutionException {
		return getUpdatedManifest(new BundleKey(bundlePath));
	}
	
	protected final Object updateOperationLock = new Object();
	
	protected abstract ResolvedManifest updateManifestEntry(BundleKey bundleKey) throws ExecutionException;
	
}

/**
 * Maintains a registry of parsed bundle manifests, indexed by bundle path.
 */
public class SemanticManager extends AbstractSemanticManager {
	
	protected final DToolServer dtoolServer;
	protected final ITaskAgent dubProcessAgent;
	protected final ModuleParseCache parseCache;
	
	public SemanticManager(DToolServer dtoolServer) {
		this.dtoolServer = dtoolServer;
		this.parseCache = new ModuleParseCache(dtoolServer);
		this.dubProcessAgent = dtoolServer.new DToolTaskAgent("DToolServer.DubProcessAgent");
	}
	
	public void shutdown() {
		dubProcessAgent.shutdownNow();
	}
	
	public ModuleParseCache getParseCache() {
		return parseCache;
	}
	
	@Override
	protected ResolvedManifest updateManifestEntry(BundleKey bundleKey) throws ExecutionException {
		synchronized(updateOperationLock) {
			// Recheck stale status after acquiring lock, it might have been updated in the meanwhile.
			// Otherwise unnecessary updates might occur after one other.
			BundleInfo info = getInfo(bundleKey);
			if(info.checkIsManifestStale() == false)
				return info.getManifest();
			
			doUpdateManifestEntry(bundleKey);
			return info.getManifest();
		}
	}
	
	protected void doUpdateManifestEntry(BundleKey bundleKey) throws ExecutionException {
		RunDubDescribeCallable dubDescribeTask = new RunDubDescribeCallable(bundleKey.bundlePath, false);
		DubBundleDescription bundleDesc = dubDescribeTask.submitAndGet(dubProcessAgent);
		
		FileTime dubStartTimeStamp = dubDescribeTask.getStartTimeStamp();
		DubDescribeAnalysis dubDescribeAnalyzer = new DubDescribeAnalysis(bundleDesc);
		
		setNewManifestEntry(dubStartTimeStamp, dubDescribeAnalyzer);
	}
	
	protected void setNewManifestEntry(FileTime dubStartTimeStamp, DubDescribeAnalysis dubDescribeAnalyzer) {
		synchronized(entriesLock) {
			
			StringBuilder sb = new StringBuilder();
			
			sb.append(" Completed `dub describe`, resolved new manifests");
			sb.append(" (timestamp: " + TIMESTAMP_FORMAT.format(new Date(dubStartTimeStamp.toMillis())) + ")");
			sb.append(" : \n");
			
			for(ResolvedManifest newManifestValue : dubDescribeAnalyzer.getAllManifests()) {
				sb.append(" Bundle:  " + String.format("%-25s", newManifestValue.getBundleName())  
					+ "  @ " + newManifestValue.bundlePath + "\n");
				
				BundleKey bundleKey = newManifestValue.getBundleKey();
				// We cap the maximum timestamp because DUB describe is only guaranteed to have read the 
				// manifest files up to dubStartTimeStamp
				getInfo(bundleKey).manifestEntry.updateValue(newManifestValue, dubStartTimeStamp);
			}
			sb.append("---");
			dtoolServer.logMessage(sb.toString());
		}
	}
	
	/* ----------------- Semantic Resolution and module list ----------------- */
	
	public BundleResolution getStoredResolution(BundlePath bundlePath) {
		return getStoredResolution(new BundleKey(bundlePath));
	}
	public boolean checkIsResolutionStale(BundlePath bundlePath) {
		return checkIsResolutionStale(new BundleKey(bundlePath));
	}
	public BundleResolution getUpdatedResolution(BundlePath bundlePath) throws ExecutionException {
		return getUpdatedResolution(new BundleKey(bundlePath));
	}	
	
	public BundleResolution getStoredResolution(BundleKey bundleKey) {
		BundleInfo info = infos.getEntryOrNull(bundleKey);
		return info != null ? info.getSemanticResolution() : null;
	}
	
	public boolean checkIsResolutionStale(BundleKey bundleKey) {
		BundleInfo info = infos.getEntryOrNull(bundleKey);
		return info == null ? true : info.checkIsResolutionStale();
	}
	
	public BundleModules createBundleModules(List<Path> importFolders) {
		return new SM_BundleModulesVisitor(importFolders).toBundleModules();
	}
	
	protected class SM_BundleModulesVisitor extends BundleModulesVisitor {
		public SM_BundleModulesVisitor(List<Path> importFolders) {
			super(importFolders);
		}
		
		@Override
		protected FileVisitResult handleFileVisitException(Path file, IOException exc) {
			dtoolServer.logError("Error visiting directory/file path: " + file, exc);
			return FileVisitResult.CONTINUE;
		}
	}
	
	public BundleResolution getUpdatedResolution(BundleKey bundleKey) throws ExecutionException {
		return getUpdatedResolution(bundleKey, null);
	}
	
	public BundleResolution getUpdatedResolution(BundleKey bundleKey, Path compilerPath) throws ExecutionException {
		BundleInfo info = getInfo(bundleKey);
		BundleResolution semanticResolution = info.getSemanticResolution();
		if(info.checkIsResolutionStale() || 
			(compilerPath != null && !areEqual(semanticResolution.getCompilerPath(), compilerPath))) {
			/*FIXME: BUG here*/
			return updateSemanticResolutionEntry(info, compilerPath);
		}
		return semanticResolution;
	}
	
	protected BundleResolution updateSemanticResolutionEntry(BundleInfo staleInfo, Path compilerPath) 
			throws ExecutionException {
		synchronized(updateOperationLock) {
			// Recheck stale status after acquiring lock, it might have been updated in the meanwhile.
			// Otherwise unnecessary update operatons might occur if two threads tried to update at the same time.
			if(staleInfo.checkIsResolutionStale() == false)
				return staleInfo.getSemanticResolution();
			
			BundleKey bundleKey = staleInfo.bundleKey;
			ResolvedManifest manifest = getUpdatedManifest(bundleKey);
			StandardLibraryResolution stdLibResolution = getUpdatedStdLibResolution(compilerPath);
			
			BundleResolution bundleRes = new DubBundleResolution(this, manifest, stdLibResolution);
			
			setNewBundleResolutionEntry(bundleRes);
			return staleInfo.getSemanticResolution();
		}
	}
	
	protected BundleInfo setNewBundleResolutionEntry(BundleResolution bundleRes) {
		synchronized(entriesLock) {
			for(BundleResolution newDepBundleRes : bundleRes.getDirectDependencies()) {
				setNewBundleResolutionEntry(newDepBundleRes);
			}
			BundleInfo newInfo = getInfo(bundleRes.getBundleKey());
			newInfo.bundleResolution = bundleRes;
			return newInfo;
		}
	}
	
	/* ----------------- StdLib resolution ----------------- */
	
	protected class SM_SearchCompilersOnPath extends SearchCompilersOnPathOperation {
		@Override
		protected void handleWarning(String message) {
			dtoolServer.logMessage(message);
		}
	}
	
	protected StandardLibraryResolution getUpdatedStdLibResolution(Path compilerPath) {
		CompilerInstall foundInstall = getCompilerInstallForNewResolution(compilerPath);
		return stdLibResolutions.getEntry(foundInstall); // found install can be null
	}
	
	protected CompilerInstall getCompilerInstallForNewResolution(Path compilerPath) {
		if(compilerPath != null) {
			return new CompilerInstallDetector().detectInstallFromCompilerCommandPath(compilerPath);
		} else {
			// TODO: determine a better match according to compiler type.
			return new SM_SearchCompilersOnPath().searchForCompilersInDefaultPathEnvVars().getPreferredInstall();
		}
	}
	
	protected final StdLibResolutionsCache stdLibResolutions = new StdLibResolutionsCache();
	
	protected class StdLibResolutionsCache extends CachingRegistry<CompilerInstall, StandardLibraryResolution> {
		@Override
		public synchronized StandardLibraryResolution getEntry(CompilerInstall key) {
			if(key == null) {
				key = MissingStandardLibraryResolution.NULL_COMPILER_INSTALL;
			}
			StandardLibraryResolution entry = map.get(key);
			if(entry == null || entry.checkIsStale()) {
				entry = createEntry(key);
				map.put(key, entry);
			}
			return entry;
		}
		
		@Override
		protected StandardLibraryResolution createEntry(CompilerInstall compilerInstall) {
			if(compilerInstall == MissingStandardLibraryResolution.NULL_COMPILER_INSTALL) {
				return new MissingStandardLibraryResolution(SemanticManager.this);
			}
			return new StandardLibraryResolution(SemanticManager.this, compilerInstall);
		}
	}
	
	/* ----------------- Working Copy and module resolution ----------------- */
	
	public ParsedModule setWorkingCopyAndParse(Path filePath, String source) {
		return parseCache.setWorkingCopyAndGetParsedModule(filePath, source);
	}
	
	public void discardWorkingCopy(Path filePath) {
		parseCache.discardWorkingCopy(filePath);
	}
	
	public ResolvedModule getUpdatedResolvedModule(Path filePath) throws ExecutionException {
		return getUpdatedResolvedModule(filePath, null);
	}
	
	public ResolvedModule getUpdatedResolvedModule(Path filePath, Path compilerPath) throws ExecutionException {
		/* FIXME: issue of absolute paths */
		// Keep this enabled for now.
//		if(!filePath.isAbsolute()) {
//			throw new ExecutionException(new Exception("Invalid module path"));
//		}
		BundlePath bundlePath = BundlePath.findBundleForPath(filePath);
		
		try {
			ResolvedModule resolvedModule;
			StandardLibraryResolution stdLibResolution;
			if(bundlePath == null) {
				stdLibResolution = getUpdatedStdLibResolution(compilerPath);
				resolvedModule = stdLibResolution.getBundleResolvedModule(filePath);
			} else {
				BundleResolution bundleRes = getUpdatedResolution(new BundleKey(bundlePath), compilerPath);
				stdLibResolution = bundleRes.getStdLibResolution();
				resolvedModule = bundleRes.getBundleResolvedModule(filePath);
			}
			
			if(resolvedModule != null) {
				return resolvedModule;
			}
			return createSyntheticBundle(filePath, stdLibResolution);
		} catch (ModuleSourceException e) {
			throw new ExecutionException(e);
		}
	}
	
	protected ResolvedModule createSyntheticBundle(Path filePath, StandardLibraryResolution stdLibResolution) 
			throws ModuleSourceException {
		BundleModules bundleModules = BundleModules.createSyntheticBundleModules(filePath);
		BundleResolution syntheticBR = new BundleResolution(this, null, bundleModules, stdLibResolution,
			new ArrayList2<BundleResolution>());
		ResolvedModule resolvedModule = syntheticBR.getBundleResolvedModule(filePath);
		return assertNotNull(resolvedModule);
	}
	
}