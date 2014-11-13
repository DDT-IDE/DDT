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

import static melnorme.utilbox.core.CoreUtil.areEqual;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutionException;

import melnorme.lang.tooling.bundles.ModuleFullName;
import melnorme.lang.tooling.bundles.ModuleSourceException;
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
	
	protected final CachingRegistry<BundlePath, BundleInfo> infos = new CachingRegistry<BundlePath, BundleInfo>() {
		@Override
		protected BundleInfo createEntry(BundlePath bundlePath) {
			return new BundleInfo(bundlePath);
		}
	};
	
	protected final Object entriesLock = new Object();
	
	/* -----------------  ----------------- */
	
	protected class BundleInfo {
		
		protected final BundlePath bundlePath;
		
		protected final FileCachingEntry<ResolvedManifest> manifestEntry;
		protected volatile BundleResolution bundleResolution;
		
		public BundleInfo(BundlePath bundlePath) {
			this.bundlePath = bundlePath;
			this.manifestEntry = new FileCachingEntry<>(bundlePath.path);
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
					BundleInfo depBundleInfo = getInfo(depBundle.getBundlePath());
					if(depBundle != depBundleInfo.getManifest()) {
						// Dep manifest is not stale, but was updated in the meanwhile. 
						// Therefore parent is stale.
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
	
	protected BundleInfo getInfo(BundlePath bundlePath) {
		return infos.getEntry(bundlePath);
	}
	
	public ResolvedManifest getStoredManifest(BundlePath bundlePath) {
		BundleInfo info = infos.getEntryOrNull(bundlePath);
		return info != null ? info.getManifest() : null;
	}
	
	public boolean checkIsManifestStale(BundlePath bundlePath) {
		BundleInfo info = infos.getEntryOrNull(bundlePath);
		return info == null ? true : info.checkIsManifestStale();
	}
	
	public ResolvedManifest getUpdatedManifest(BundlePath bundlePath) throws ExecutionException {
		BundleInfo info = getInfo(bundlePath);
		if(info.checkIsManifestStale()) {
			return updateManifestEntry(bundlePath);
		}
		return info.getManifest();
	}
	
	protected final Object updateOperationLock = new Object();
	
	protected abstract ResolvedManifest updateManifestEntry(BundlePath bundlePath) throws ExecutionException;
	
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
	protected ResolvedManifest updateManifestEntry(BundlePath bundlePath) throws ExecutionException {
		synchronized(updateOperationLock) {
			// Recheck stale status after acquiring lock, it might have been updated in the meanwhile.
			// Otherwise unnecessary updates might occur after one other.
			BundleInfo info = getInfo(bundlePath);
			if(info.checkIsManifestStale() == false)
				return info.getManifest();
			
			doUpdateManifestEntry(bundlePath);
			return info.getManifest();
		}
	}
	
	protected void doUpdateManifestEntry(BundlePath bundlePath) throws ExecutionException {
		RunDubDescribeCallable dubDescribeTask = new RunDubDescribeCallable(bundlePath, false);
		DubBundleDescription bundleDesc = dubDescribeTask.submitAndGet(dubProcessAgent);
		
		FileTime dubStartTimeStamp = dubDescribeTask.getStartTimeStamp();
		DubDescribeAnalysis dubDescribeAnalyzer = new DubDescribeAnalysis(bundleDesc);
		
		setNewManifestEntry(dubStartTimeStamp, dubDescribeAnalyzer);
	}
	
	protected void setNewManifestEntry(FileTime dubStartTimeStamp, DubDescribeAnalysis dubDescribeAnalyzer) {
		synchronized(entriesLock) {
			for(ResolvedManifest newManifestValue : dubDescribeAnalyzer.getAllManifests()) {
				dtoolServer.logMessage("Resolved new manifest for: " + newManifestValue.bundlePath + 
					"\n  timestamp: " + dubStartTimeStamp.toString());
				
				// We cap the maximum timestamp because DUB describe is only guaranteed to have read the 
				// manifest files up to dubStartTimeStamp
				getInfo(newManifestValue.bundlePath).manifestEntry.updateValue(newManifestValue, dubStartTimeStamp);
			}
		}
	}
	
	/* ----------------- Semantic Resolution and module list ----------------- */
	
	public BundleResolution getStoredResolution(BundlePath bundlePath) {
		BundleInfo info = infos.getEntryOrNull(bundlePath);
		return info != null ? info.getSemanticResolution() : null;
	}
	
	public boolean checkIsResolutionStale(BundlePath bundlePath) {
		BundleInfo info = infos.getEntryOrNull(bundlePath);
		return info == null ? true : info.checkIsResolutionStale();
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
	
	public BundleResolution getUpdatedResolution(BundlePath bundlePath) throws ExecutionException {
		return getUpdatedResolution(bundlePath, null);
	}
	
	public BundleResolution getUpdatedResolution(BundlePath bundlePath, Path compilerPath) throws ExecutionException {
		BundleInfo info = getInfo(bundlePath);
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
			
			BundlePath bundlePath = staleInfo.bundlePath;
			ResolvedManifest manifest = getUpdatedManifest(bundlePath);
			StandardLibraryResolution stdLibResolution = getUpdatedStdLibResolution(compilerPath);
			
			BundleResolution bundleRes = new BundleResolution(this, manifest, stdLibResolution);
			
			setNewBundleResolutionEntry(bundleRes);
			return staleInfo.getSemanticResolution();
		}
	}
	
	protected BundleInfo setNewBundleResolutionEntry(BundleResolution bundleRes) {
		synchronized(entriesLock) {
			for(BundleResolution newDepBundleRes : bundleRes.getDirectDependencies()) {
				setNewBundleResolutionEntry(newDepBundleRes);
			}
			BundleInfo newInfo = getInfo(bundleRes.getBundlePath());
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
		// Keep this enabled for now.
//		if(!filePath.isAbsolute()) {
//			throw new ExecutionException(new Exception("Invalid module path"));
//		}
		BundlePath bundlePath = BundlePath.findBundleForPath(filePath);
		
		try {
			AbstractBundleResolution bundleRes;
			if(bundlePath == null) {
				StandardLibraryResolution stdLibResolution = getUpdatedStdLibResolution(compilerPath);
				bundleRes = new SyntheticBundleResolution(this, BundleModules.createEmpty(), stdLibResolution);
			} else {
				bundleRes = getUpdatedResolution(bundlePath, compilerPath);
			}
			return bundleRes.getBundleResolvedModule(filePath);
		} catch (ModuleSourceException e) {
			throw new ExecutionException(e);
		}
	}
	
	protected class SyntheticBundleResolution extends AbstractBundleResolution {
		
		protected final StandardLibraryResolution stdLibResolution;

		public SyntheticBundleResolution(SemanticManager manager, BundleModules bundleModules, 
				StandardLibraryResolution stdLibResolution) {
			super(manager, bundleModules);
			this.stdLibResolution = stdLibResolution;
		}
		
		@Override
		protected void findModules(String fullNamePrefix, HashSet<String> matchedModules) {
			stdLibResolution.findModules(fullNamePrefix, matchedModules);
		}
		
		@Override
		public ResolvedModule findResolvedModule(ModuleFullName moduleFullName) throws ModuleSourceException {
			return stdLibResolution.findResolvedModule(moduleFullName);
		}
		
	}
	
}