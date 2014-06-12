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

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.ExecutionException;

import melnorme.utilbox.concurrency.ITaskAgent;
import dtool.dub.BundlePath;
import dtool.dub.DubBundle;
import dtool.dub.DubBundle.DubDependecyRef;
import dtool.dub.DubBundleDescription;
import dtool.dub.DubHelper.RunDubDescribeCallable;
import dtool.model.util.CachingEntry;
import dtool.model.util.CachingRegistry;
import dtool.project.DeeNamingRules;

/**
 * A caching hierarchical registry.
 * This class could be generalized, but it would be a bit of a mess to handle all the parameterized types.
 */
abstract class AbstractSemanticManager 
	extends CachingRegistry<BundlePath, AbstractSemanticManager.SemanticResolutionEntry> {
	
	public AbstractSemanticManager() {
		super();
	}
	
	/* -----------------  ----------------- */
	
	@Override
	protected SemanticResolutionEntry createEntry(BundlePath bundlePath) {
		return new SemanticResolutionEntry(bundlePath);
	}
	
	protected static class SemanticResolutionEntry extends CachingEntry<BundleSemanticResolution> {
		
		protected final BundlePath bundlePath;
		
		public SemanticResolutionEntry(BundlePath bundlePath) {
			this.bundlePath = bundlePath;
		}
		
	}

	/* -----------------  ----------------- */
	
	protected final Object entriesLock = new Object();
	
	public BundleSemanticResolution getStoredResolution(BundlePath bundlePath) throws ExecutionException {
		SemanticResolutionEntry mapEntry = getMapEntry(bundlePath);
		return mapEntry != null ? mapEntry.getValue() : null;
	}
	
	public BundleSemanticResolution getUpdatedResolution(BundlePath bundlePath) throws ExecutionException {
		SemanticResolutionEntry entry = getEntry(bundlePath);
		if(isResolutionStale(bundlePath)) {
			updateEntry(bundlePath);
		}
		return entry.getValue();
	}
	
	protected boolean isManifestInfoStale(BundlePath bundlePath) {
		return getEntry(bundlePath).isStale();
	}
	
	public boolean isResolutionStale(BundlePath bundlePath) {
		SemanticResolutionEntry entry = getEntry(bundlePath);
		if(entry.isStale()) {
			return true;
		}
		
		synchronized(entriesLock) {
			long valueTimeStamp = entry.getValueTimeStamp();
			return hasBeenModifiedSince(entry, valueTimeStamp);
		}
	}
	
	/** Checks if given entry, or any child entry that it refers to, 
	 * has had any modifications since given timeStamp. */
	protected boolean hasBeenModifiedSince(SemanticResolutionEntry entry, long timeStamp) {
		if(entry.isStale() || entry.getValueTimeStamp() > timeStamp) {
			return true;
		}
		
		for(BundlePath depBundlePath : entry.getValue().getBundleDeps()) {
			SemanticResolutionEntry depEntry = getEntry(depBundlePath);
			if(hasBeenModifiedSince(depEntry, timeStamp)) {
				return true;
			}
		}
		return false;
	}
	
	public void invalidateBundleManifest(BundlePath bundlePath) {
		getEntry(bundlePath).markStale();
	}
	
	protected final Object updateOperationLock = new Object();
	
	protected void updateEntry(BundlePath bundlePath) throws ExecutionException {
		synchronized(updateOperationLock) {
			// Recheck stale status after acquiring lock, it might have been updated in the meanwhile.
			if(isResolutionStale(bundlePath) == false)
				return;
			
			UpdateEntryResult updateResult = determineNewEntryValues(bundlePath);
			
			synchronized(entriesLock) {
				long newTimeStamp = updateResult.newTimeStamp; 
				for(BundleSemanticResolution newEntryValue : updateResult.newValues) {
					getEntry(newEntryValue.bundlePath).updateValue(newEntryValue, newTimeStamp);
				}
			}
		}
	}
	
	protected abstract UpdateEntryResult determineNewEntryValues(BundlePath bundlePath) throws ExecutionException;
	
	protected class UpdateEntryResult {
		
		protected long newTimeStamp;
		protected Collection<BundleSemanticResolution> newValues;
		
		public UpdateEntryResult(long newTimeStamp, Collection<BundleSemanticResolution> newValues) {
			this.newTimeStamp = newTimeStamp;
			this.newValues = newValues;
		}
		
	}
	
}

/**
 * Maintains a registry of parsed bundle manifests, indexed by bundle path.
 */
public class SemanticManager extends AbstractSemanticManager {
	
	protected final DToolServer dtoolServer;
	protected final ITaskAgent dubProcessAgent;
	
	protected final ModuleParseCache parseCache = new ModuleParseCache();
	
	public SemanticManager(DToolServer dtoolServer) {
		this.dtoolServer = dtoolServer;
		this.dubProcessAgent = dtoolServer.new DToolTaskAgent("DSE.DubProcessAgent");
	}
	
	public void shutdown() {
		dubProcessAgent.shutdownNow();
	}
	
	@Override
	protected UpdateEntryResult determineNewEntryValues(BundlePath bundlePath) throws ExecutionException {
		RunDubDescribeCallable dubDescribeTask = new RunDubDescribeCallable(bundlePath, false);
		DubBundleDescription bundleDesc = dubDescribeTask.submitAndGet(dubProcessAgent);
		
		long newTimeStamp = dubDescribeTask.getStartTimeStamp();
		Collection<BundleSemanticResolution> bundleSRs = new DubDescribeAnalyzer(bundleDesc).getAll();
		return new UpdateEntryResult(newTimeStamp, bundleSRs);
	}
	
	/* ----------------- Resolved manifest calculation ----------------- */
	
	public class DubDescribeAnalyzer extends BundleModulesHelper {
		
		protected final HashMap<String, DubBundle> bundlesMap = new HashMap<>();
		protected final HashMap<String, BundleSemanticResolution> bundleSRs = new HashMap<>();
		protected final HashSet<String> bundlesBeingCalculated = new HashSet<>();
		
		public DubDescribeAnalyzer(DubBundleDescription bundleDesc) {
			super(SemanticManager.this.dtoolServer);
			
			DubBundle[] bundleDeps = bundleDesc.getBundleDependencies();
			for (DubBundle depBundle : bundleDeps) {
				bundlesMap.put(depBundle.getBundleName(), depBundle);
			}
			
			for (DubBundle dubBundle : bundleDeps) {
				calculateSemanticResolution(dubBundle);
			}
			calculateSemanticResolution(bundleDesc.getMainBundle());
		}
		
		public Collection<BundleSemanticResolution> getAll() {
			return bundleSRs.values();
		}
		
		public BundleSemanticResolution calculateSemanticResolution(DubBundle bundle) {
			BundlePath bundlePath = bundle.getBundlePath();
			if(bundlePath == null) {
				dtoolServer.logError("DUB describe: invalid bundle path: " + bundlePath);
				return null;
			}
			
			final String bundleName = bundle.getBundleName();
			BundleSemanticResolution bundleSR = bundleSRs.get(bundleName);
			if(bundleSR != null) {
				return bundleSR;
			}
			
			if(bundlesBeingCalculated.contains(bundleName)) {
				// Error cycle in DUB describe
				dtoolServer.logError("DUB describe: bundle dependencies cycle detected!");
				return null;
			}
			bundlesBeingCalculated.add(bundleName); // Mark as SR being created, for cycle checking
			
			ArrayList<BundleSemanticResolution> directDepSRs = calculateDirectDependencies(bundle);
			HashMap<ModuleFullName, Path> bundleModules = calculateBundleModules(bundle);
			
			bundleSR = new BundleSemanticResolution(SemanticManager.this, bundle, directDepSRs, bundleModules);
			bundleSRs.put(bundleName, bundleSR);
			return bundleSR;
		}
		
		protected ArrayList<BundleSemanticResolution> calculateDirectDependencies(DubBundle bundle) {
			ArrayList<BundleSemanticResolution> directDeps = new ArrayList<>(bundle.getDependencyRefs().length);
			
			for (DubDependecyRef directDependencyRef : bundle.getDependencyRefs()) {
				String depName = directDependencyRef.getBundleName();
				DubBundle depBundle = bundlesMap.get(depName);
				if(depBundle == null) {
					dtoolServer.logError("DUB describe: missing dependency: " + depName, null);
					continue;
				}
				BundleSemanticResolution sr = calculateSemanticResolution(depBundle);
				if(sr == null) {
					dtoolServer.logError("DUB describe: invalid dependency: " + depName, null);
					continue;
				}
				directDeps.add(sr);
			}
			return directDeps;
		}
		
	}
	
	/* ----------------- file updates handling ----------------- */
	
	public void reportFileChange(Path file) {
		file = file.toAbsolutePath().normalize();
		BundlePath bundlePath = findBundleForPath(file);
		SemanticResolutionEntry entry = getEntry(bundlePath);
		
		if(isManifestInfoStale(bundlePath)) {
			return;
		}
		DubBundle bundleInfo = getBundleInfo(bundlePath);
		Path pathInImportFolder = getPathInImportFolder(file, bundleInfo);
		if(pathInImportFolder == null) {
			return; // Then the file is not contained in an import folder, so it is of no importance then.
		}
		
		ModuleFullName moduleFullName = DeeNamingRules.getValidModuleFullName(pathInImportFolder);
		if(moduleFullName != null) {
			entry.markStale();
		}
	}
	
	protected Path getPathInImportFolder(Path file, DubBundle bundleInfo) {
		ArrayList<Path> sourceFolders = bundleInfo.getEffectiveImportPathFolders_AbsolutePath();
		for(Path path : sourceFolders) {
			if(file.startsWith(path)) {
				return path.relativize(file);
			}
		}
		return null;
	}
	
	protected DubBundle getBundleInfo(BundlePath bundlePath) {
		return getMapEntry(bundlePath).getValue().bundle;
	}
	
	protected BundlePath findBundleForPath(Path dir) {
		if(dir == null) {
			return null;
		}
		BundlePath bundlePath = BundlePath.create(dir);
		if(bundlePath != null && bundlePath.getManifestFilePath().toFile().exists()) {
			return bundlePath;
		}
		return findBundleForPath(dir.getParent());
	}
	
//	public boolean isModuleListStale(BundlePath bundlePath) {
//		return getEntry(bundlePath).isStale(); /*BUG here*/
//	}
	
}