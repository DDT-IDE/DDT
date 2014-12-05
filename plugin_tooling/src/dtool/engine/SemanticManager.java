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
import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
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

class BundleInfo {
	
	protected final BundleKey bundleKey;
	protected volatile BundleResolution bundleResolution;
	
	public BundleInfo(BundleKey bundleKey) {
		this.bundleKey = bundleKey;
	}
	
	public BundleResolution getSemanticResolution() {
		return bundleResolution;
	}
	
}

/**
 * Maintains a registry of parsed bundle manifests, indexed by bundle path.
 */
public class SemanticManager {
	
	protected final DToolServer dtoolServer;
	protected final ITaskAgent dubProcessAgent;
	protected final ModuleParseCache parseCache;
	
	protected final AbstractManifestManager manifestManager = new AbstractManifestManager();
	protected final ResolutionsManager resolutionsManager = new ResolutionsManager();

	
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
	
	public final ResolvedManifest getStoredManifest(BundleKey bundleKey) {
		return manifestManager.getEntryManifest(bundleKey);
	}
	public final boolean checkIsManifestStale(BundleKey bundleKey) {
		return manifestManager.checkIsEntryStale(bundleKey);
	}
	public ResolvedManifest getUpdatedManifest(BundleKey bundleKey) throws ExecutionException {
		return manifestManager.getUpdatedManifest(bundleKey);
	}
	
	public class AbstractManifestManager 
		extends AbstractCachingManager<BundleKey, FileCachingEntry<ResolvedManifest>> {
		
		@Override
		protected FileCachingEntry<ResolvedManifest> doCreateEntry(BundleKey bundleKey) {
			return new FileCachingEntry<>(bundleKey.getPath());
		}
		
		public ResolvedManifest getEntryManifest(BundleKey bundleKey) {
			FileCachingEntry<ResolvedManifest> entry = infos.getEntryOrNull(bundleKey);
			return getManifestFromEntry(entry);
		}
		
		protected ResolvedManifest getManifestFromEntry(FileCachingEntry<ResolvedManifest> entry) {
			return entry != null ? entry.getValue() : null;
		}
		
		public ResolvedManifest getUpdatedManifest(BundleKey bundleKey) throws ExecutionException {
			return getUpdatedEntry(bundleKey).getValue();
		}

		@Override
		public boolean doCheckIsEntryStale(FileCachingEntry<ResolvedManifest> entry) {
			if(entry.isStale()) {
				return true;
			}
			
			synchronized(entriesLock) {
				
				for(ResolvedManifest depBundle : entry.getValue().getBundleDeps()) {
					FileCachingEntry<ResolvedManifest> depBundleEntry = getEntry(depBundle.getBundleKey());
					if(depBundle != depBundleEntry.getValue()) {
						// The manifest of the dependency is not stale, 
						// but it has changed since the parent was created, therefore parent is stale.
						return true;
					}
					if(doCheckIsEntryStale(depBundleEntry)) {
						return true;
					}
				}
				return false;
			}
		}
		
		@Override
		protected FileCachingEntry<ResolvedManifest> updateManifestEntry(BundleKey key) 
				throws ExecutionException {
			synchronized(updateOperationLock) {
				// Recheck stale status after acquiring lock, it might have been updated in the meanwhile.
				// Otherwise unnecessary updates might occur after one other.
				FileCachingEntry<ResolvedManifest> info = getEntry(key);
				if(doCheckIsEntryStale(info) == false)
					return info;
				
				doUpdateManifestEntry(key);
				return info;
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
					getEntry(bundleKey).updateValue(newManifestValue, dubStartTimeStamp);
				}
				sb.append("---");
				dtoolServer.logMessage(sb.toString());
			}
		}
	
	};
	
	/* ----------------- Semantic Resolution and module list ----------------- */
	
	protected BundleKey bundleKey(BundlePath bundlePath) {
//		CompilerInstall compilerInstall = getCompilerInstallForPath(null);
		return new BundleKey(bundlePath);
	}
	
	public BundleResolution getStoredResolution(BundlePath bundlePath) {
		return getStoredResolution(bundleKey(bundlePath));
	}
	public boolean checkIsResolutionStale(BundlePath bundlePath) {
		return checkIsResolutionStale(bundleKey(bundlePath));
	}
	public BundleResolution getUpdatedResolution(BundlePath bundlePath) throws ExecutionException {
		return getUpdatedResolution(bundleKey(bundlePath));
	}	
	
	public BundleResolution getStoredResolution(BundleKey bundleKey) {
		BundleInfo info = resolutionsManager.getEntry(bundleKey);
		return info != null ? info.getSemanticResolution() : null;
	}
	public boolean checkIsResolutionStale(BundleKey bundleKey) {
		return resolutionsManager.checkIsEntryStale(bundleKey);
	}
	public BundleResolution getUpdatedResolution(BundleKey bundleKey) throws ExecutionException {
		return getUpdatedResolution(bundleKey, null);
	}
	
	public class ResolutionsManager extends AbstractCachingManager<BundleKey, BundleInfo> {
		@Override
		protected BundleInfo doCreateEntry(BundleKey key) {
			return new BundleInfo(key);
		}
		
		@Override
		public boolean doCheckIsEntryStale(BundleInfo bundleInfo) {
			return
					bundleInfo.getSemanticResolution() == null ||
					checkIsManifestStale(bundleInfo.bundleKey) ||
					bundleInfo.getSemanticResolution().checkIsStale();
		};
		
		@Override
		protected BundleInfo updateManifestEntry(BundleKey key) throws ExecutionException {
			throw assertFail(); // TODO
		}
		
	}
	
	public BundleResolution getUpdatedResolution(BundleKey bundleKey, Path compilerPath) throws ExecutionException {
		BundleInfo info = resolutionsManager.getEntry(bundleKey);
		BundleResolution semanticResolution = info.getSemanticResolution();
		if(resolutionsManager.doCheckIsEntryStale(info) || 
			(compilerPath != null && !areEqual(semanticResolution.getCompilerPath(), compilerPath))) {
			/*FIXME: BUG here*/
			StandardLibraryResolution stdLibResolution = getUpdatedStdLibResolution(compilerPath);
			return updateSemanticResolutionEntry(info, stdLibResolution);
		}
		return semanticResolution;
	}
	
	protected BundleResolution updateSemanticResolutionEntry(BundleInfo staleInfo, 
			StandardLibraryResolution stdLibResolution) 
			throws ExecutionException {
		synchronized(resolutionsManager.updateOperationLock) {
			// Recheck stale status after acquiring lock, it might have been updated in the meanwhile.
			// Otherwise unnecessary update operations might occur if two threads tried to update at the same time.
			if(resolutionsManager.doCheckIsEntryStale(staleInfo) == false)
				return staleInfo.getSemanticResolution();
			
			BundleKey bundleKey = staleInfo.bundleKey;
			ResolvedManifest manifest = manifestManager.getUpdatedManifest(bundleKey);
			
			BundleResolution bundleRes = new DubBundleResolution(this, manifest, stdLibResolution);
			
			setNewBundleResolutionEntry(bundleRes);
			return staleInfo.getSemanticResolution();
		}
	}
	
	protected BundleInfo setNewBundleResolutionEntry(BundleResolution bundleRes) {
		synchronized(resolutionsManager.entriesLock) {
			for(BundleResolution newDepBundleRes : bundleRes.getDirectDependencies()) {
				setNewBundleResolutionEntry(newDepBundleRes);
			}
			BundleInfo newInfo = resolutionsManager.getEntry(bundleRes.getBundleKey());
			newInfo.bundleResolution = bundleRes;
			return newInfo;
		}
	}
	
	/* ----------------- helper ----------------- */
	
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
	
	/* ----------------- StdLib resolution ----------------- */
	
	protected class SM_SearchCompilersOnPath extends SearchCompilersOnPathOperation {
		@Override
		protected void handleWarning(String message) {
			dtoolServer.logMessage(message);
		}
	}
	
	protected final StandardLibraryResolution getUpdatedStdLibResolution(Path compilerPath) {
		CompilerInstall foundInstall = getCompilerInstallForPath(compilerPath);
		return getUpdatedStdLibResolution(foundInstall);
	}
	
	public StandardLibraryResolution getUpdatedStdLibResolution(CompilerInstall foundInstall) {
		 // found install can be null /* FIXME: make non-null*/
		return stdLibResolutions.getEntry(foundInstall);
	}
	
	protected CompilerInstall getCompilerInstallForPath(Path compilerPath) {
		// FIXME: /*FIXME: BUG here*/ non null
		if(compilerPath != null) {
			return new CompilerInstallDetector().detectInstallFromCompilerCommandPath(compilerPath);
		} else {
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
		if(!filePath.isAbsolute()) {
			dtoolServer.logMessage("> getUpdatedResolvedModule for non-absolute path: " + filePath);
		}
		BundlePath bundlePath = BundlePath.findBundleForPath(filePath);
		CompilerInstall compilerInstall = getCompilerInstallForPath(compilerPath);
		
		try {
			ResolvedModule resolvedModule;
			StandardLibraryResolution stdLibResolution;
			if(bundlePath == null) {
				stdLibResolution = getUpdatedStdLibResolution(compilerInstall);
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