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
package dtool.model;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.locks.ReentrantLock;

import melnorme.utilbox.misc.MiscUtil;
import dtool.dub.BundlePath;
import dtool.dub.DubBundle;
import dtool.dub.DubBundle.BundleFile;
import dtool.dub.DubBundle.DubDependecyRef;
import dtool.dub.DubBundleDescription;
import dtool.model.util.CachingEntry;
import dtool.model.util.CachingRegistry;
import dtool.project.DeeNamingRules;

public class SemanticManager {
	
	protected final DToolServer dtoolServer;
	
	protected final SemanticManagerRegistry semanticResolutions = new SemanticManagerRegistry();
	protected final ReentrantLock srEntriesLock = new ReentrantLock();
	
	
	protected final ModuleParseCache parseCache = ModuleParseCache.getDefault();
	
	
	public SemanticManager(DToolServer dtoolServer) {
		this.dtoolServer = assertNotNull(dtoolServer);
	}
	
	protected BundleManifestRegistry getBundleManifestCache() {
		return dtoolServer.getBundleManifestRegistry();
	}
	
	protected void logError(String message) {
		dtoolServer.logError(message, null);
	}
	
	protected void logWarning(String message) {
		dtoolServer.logMessage(message);
	}
	
	/* -----------------  ----------------- */
	
	public SemanticResolution getSemanticResolution(BundlePath bundlePath) {
		try {
			return semanticResolutions.getEntry(bundlePath).getValue();
		} catch (ExecutionException e) {
			/*BUG here store error in SR*/
			throw melnorme.utilbox.core.ExceptionAdapter.unchecked(e);
		}
	}
	
	public SemanticResolutionEntry getEntry(BundlePath bundlePath) {
		return semanticResolutions.getEntry(bundlePath);
	}
	
	protected final class SemanticManagerRegistry extends CachingRegistry<BundlePath, SemanticResolutionEntry> {
		@Override
		protected SemanticResolutionEntry createEntry(BundlePath bundlePath) {
			return new SemanticResolutionEntry(bundlePath);
		}
	}
	
	protected class SemanticResolutionEntry extends CachingEntry<SemanticResolution>{
		
		protected final BundlePath bundlePath;
		
		public SemanticResolutionEntry(BundlePath bundlePath) {
			this.bundlePath = bundlePath;
		}
		
		@Override
		public void makeStale() {
			// Note: don't lock on SemanticResolutionEntry lock
			getBundleManifestCache().getEntry(bundlePath).makeStale();
			super.makeStale();
		}
		
		@Override
		protected synchronized boolean checkIsStale() {
			
			srEntriesLock.lock();
			try {
				if(isInternallyStale()) {
					return true;
				}
				SemanticResolution existingSR = getExistingValue();
				BundlePath[] bundleDeps = existingSR.getBundleDeps();
				
				for(BundlePath depBundlePath : bundleDeps) {
					SemanticResolutionEntry depSR = semanticResolutions.getEntry(depBundlePath);
					
					// Note: depSR can be internally stale, thats ok because we have our own copy of depSR.
					// We just want to know if a newer on is available or not.
					if(depSR.getLatestAvailableStamp() > getCreationStamp()) {
						return true;
					}
				}
				return false;
			} finally {
				srEntriesLock.unlock();
			}
		}
		
		@Override
		protected SemanticResolution doCreateNewValue() throws ExecutionException, InterruptedException {
			// Note: we are under SemanticResolutionEntry lock
			
			/* BUG here must store the created depSRs */
			
			DubBundleDescription bundleDesc = getBundleManifestCache().getManifest(bundlePath);
			
			HashMap<String, BundlePath> depBundleToPathMapping = bundleDesc.getDepBundleToPathMapping();
			
			DubBundle mainBundle = bundleDesc.getMainBundle();
			SemanticResolution mainSR = createSemanticResolution(mainBundle, depBundleToPathMapping);
			
			DubBundle[] bundleDeps = bundleDesc.getBundleDependencies();
			SemanticResolution[] depSRs = new SemanticResolution[bundleDeps.length]; 
			for (int i = 0; i < bundleDeps.length; i++) {
				depSRs[i] = createSemanticResolution(bundleDeps[i], depBundleToPathMapping);
			}
			
			return mainSR;
		}
		
	}
	
	public void notifyManifestFileChanged(BundlePath bundlePath) {
		semanticResolutions.getEntry(bundlePath).makeStale();
	}
	
	
	/* ----------------- module model calculation ----------------- */
	
	public SemanticResolution createSemanticResolution(DubBundle bundle, 
			HashMap<String, BundlePath> depBundleToPathMap) {
		BundlePath[] depBundles = getDependenciesBundlePath(depBundleToPathMap, bundle);
		HashMap<ModuleFullName, Path> bundleModules = calculateBundleModules(bundle);
		
		return new SemanticResolution(SemanticManager.this, bundle, depBundles, bundleModules);
	}
	
	protected BundlePath[] getDependenciesBundlePath(HashMap<String, BundlePath> bundleToPathMap, DubBundle bundle) {
		DubDependecyRef[] depRefs = bundle.getDependencyRefs();
		BundlePath[] directDepsPath = new BundlePath[depRefs.length];
		for (int i = 0; i < depRefs.length; i++) {
			directDepsPath[i] = bundleToPathMap.get(depRefs[i].getBundleNameRef());
			if(directDepsPath[i] == null) {
				dtoolServer.logError("DUB describe: dependency path is missing or invalid.", null);
			}
		}
		return directDepsPath;
	}
	
	protected HashMap<ModuleFullName, Path> calculateBundleModules(DubBundle bundle) {
		HashMap<ModuleFullName, Path> hashMap = new HashMap<>();
		
		for (BundleFile bundleFiles : bundle.bundleFiles) {
			Path filePath = MiscUtil.createValidPath(bundleFiles.filePath);
			if(filePath == null) {
				logError("Invalid filesystem path: " + bundleFiles.filePath);
				continue; // ignore
			}
			
			Path[] importFolders = bundle.getEffectiveImportPathFolders();
			for (Path importFolder : importFolders) {
				if(filePath.startsWith(importFolder)) {
					Path relPath = importFolder.relativize(filePath);
					if(relPath.getNameCount() == 0) {
						logError("File has same path as import folder: " + filePath);
						continue;
					}
					
					ModuleFullName moduleFullName = DeeNamingRules.getModuleFullName(relPath);
					if(!moduleFullName.isValid()) {
						logWarning("Invalid path for a D module: " + relPath);
						continue;
					}
					hashMap.put(moduleFullName, filePath);
					
					// continue looking, the same file can be present in multiple import paths, if nested
					// it's not an elegant scenario, but it's probably ok to support.
				}
			}
		}
		return hashMap;
	}
	
	public static class SemanticResolution extends SemanticContext {
		
		public SemanticResolution(SemanticManager manager, DubBundle bundle, BundlePath[] depBundlePaths, 
				Map<ModuleFullName, Path> bundleModules) {
			super(manager, bundle, depBundlePaths, bundleModules);
		}
		
	}
	
}