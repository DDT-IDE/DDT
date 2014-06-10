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
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import melnorme.utilbox.concurrency.ITaskAgent;
import dtool.dub.BundlePath;
import dtool.dub.DubBundle;
import dtool.dub.DubBundle.DubDependecyRef;
import dtool.dub.DubBundleDescription;
import dtool.dub.DubHelper.RunDubDescribeCallable;
import dtool.dub.ResolvedManifest;
import dtool.model.util.CachingEntry;
import dtool.model.util.CachingRegistry;

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
	
	public ResolvedManifest getStoredResolution(BundlePath bundlePath) throws ExecutionException {
		SemanticResolutionEntry mapEntry = getMapEntry(bundlePath);
		return mapEntry != null ? mapEntry.getValue() : null;
	}
	
	public BundleSemanticResolution getUpdatedResolution(BundlePath bundlePath) throws ExecutionException {
		SemanticResolutionEntry entry = getEntry(bundlePath);
		if(!isResolutionUpdated(bundlePath)) {
			updateEntry(bundlePath);
		}
		return entry.getValue();
	}
	
	protected boolean isInternallyUpdated(BundlePath bundlePath) {
		return !getEntry(bundlePath).isStale();
	}
	
	public boolean isResolutionUpdated(BundlePath bundlePath) {
		SemanticResolutionEntry entry = getEntry(bundlePath);
		if(entry.isStale()) {
			return false;
		}
		
		synchronized(entriesLock) {
			long valueTimeStamp = entry.getValueTimeStamp();
			return !hasBeenModifiedSince(entry, valueTimeStamp);
		}
	}
	
	/** Checks if given entry, or any child entry that it refers to, 
	 * has had any modifications since given timeStamp. */
	protected boolean hasBeenModifiedSince(SemanticResolutionEntry entry, long timeStamp) {
		ResolvedManifest bundle = entry.getValue();
		
		if(entry.isStale() || entry.getValueTimeStamp() > timeStamp) {
			return true;
		}
		
		for(BundlePath depBundlePath : bundle.getBundleDeps()) {
			SemanticResolutionEntry depEntry = getEntry(depBundlePath);
			if(hasBeenModifiedSince(depEntry, timeStamp)) {
				return true;
			}
		}
		return false;
	}
	
	public void invalidateCurrentManifest(BundlePath bundlePath) {
		getEntry(bundlePath).makeStale();
	}
	
	protected final Object updateOperationLock = new Object();
	
	protected void updateEntry(BundlePath bundlePath) throws ExecutionException {
		synchronized(updateOperationLock) {
			// Recheck udpate status after acquiring lock.
			if(isResolutionUpdated(bundlePath))
				return;
			
			UpdateEntryResult updateResult = determineNewEntryValues(bundlePath);
			
			synchronized(entriesLock) {
				long newTimeStamp = updateResult.newTimeStamp; 
				for(BundleSemanticResolution resolvedBundle : updateResult.newValues) {
					getEntry(resolvedBundle.bundlePath).updateValue(resolvedBundle, newTimeStamp);
				}
			}
		}
	}
	
	protected abstract UpdateEntryResult determineNewEntryValues(BundlePath bundlePath) throws ExecutionException;
	
	protected class UpdateEntryResult {
		
		protected long newTimeStamp;
		protected List<BundleSemanticResolution> newValues;
		
		public UpdateEntryResult(long newTimeStamp, List<BundleSemanticResolution> newValues) {
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
	protected final ITaskAgent processAgent;
	
	protected final ModuleParseCache parseCache = ModuleParseCache.getDefault();
	
	public SemanticManager(DToolServer dtoolServer) {
		this.dtoolServer = dtoolServer;
		this.processAgent = dtoolServer.dubProcessAgent;
	}
	
	@Override
	protected UpdateEntryResult determineNewEntryValues(BundlePath bundlePath) throws ExecutionException {
		RunDubDescribeCallable dubDescribeTask = new RunDubDescribeCallable(bundlePath);
		DubBundleDescription bundleDesc = dubDescribeTask.submitAndGet(processAgent);
		
		long newTimeStamp = dubDescribeTask.getStartTimeStamp();
		ArrayList<BundleSemanticResolution> bundleSRs = createFromDescribe(bundleDesc);
		return new UpdateEntryResult(newTimeStamp, bundleSRs);
	}
	
	/* ----------------- Resolved manifest calculation ----------------- */
	
	public ArrayList<BundleSemanticResolution> createFromDescribe(DubBundleDescription bundleDesc) {
		DubBundle[] bundleDeps = bundleDesc.getBundleDependencies();
		HashMap<String, BundlePath> bundleNameToPathMap = bundleDesc.getDepBundleNameToPathMapping();
		
		ArrayList<BundleSemanticResolution> resolvedManifests = new ArrayList<>(bundleDeps.length + 1);
		
		addResolvedManifest(resolvedManifests, bundleDesc.getMainBundle(), bundleNameToPathMap);
		for (DubBundle dubBundle : bundleDeps) {
			addResolvedManifest(resolvedManifests, dubBundle, bundleNameToPathMap);
		}
		
		return resolvedManifests;
	}
	
	public void addResolvedManifest(ArrayList<BundleSemanticResolution> manifests, DubBundle bundle, 
			HashMap<String, BundlePath> bundleNameToPathMap) {
		BundlePath bundlePath = bundle.getBundlePath();
		if(bundlePath == null) {
			dtoolServer.logError("DUB describe: invalid bundle path for : " + bundle.getBundleName(), null);
			return;
		}
		
		ArrayList<BundlePath> directDependencies = getDirectDependencies(bundle, bundleNameToPathMap);
		
		BundleModulesHelper sm = new BundleModulesHelper(dtoolServer); /*BUG here fix this*/
		HashMap<ModuleFullName, Path> bundleModules = sm.calculateBundleModules(bundle);
		
		manifests.add(new BundleSemanticResolution(this, bundle, directDependencies, bundleModules));
	}
	
	protected ArrayList<BundlePath> getDirectDependencies(DubBundle bundle, 
		HashMap<String, BundlePath> bundleNameToPath) {
		
		ArrayList<BundlePath> directDeps = new ArrayList<>(bundle.getDependencyRefs().length);
		
		for (DubDependecyRef directDependencyRef : bundle.getDependencyRefs()) {
			String depName = directDependencyRef.getBundleName();
			BundlePath depBundlePath = bundleNameToPath.get(depName);
			if(depBundlePath == null) {
				dtoolServer.logError("DUB describe: dependency path is missing for: " + depName, null);
			} else {
				directDeps.add(depBundlePath);
			}
		}
		return directDeps;
	}
	
}