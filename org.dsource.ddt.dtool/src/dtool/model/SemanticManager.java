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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.locks.ReentrantLock;

import dtool.dub.BundlePath;
import dtool.dub.DubBundle;
import dtool.dub.DubBundleDescription;
import dtool.model.util.CachingEntry;
import dtool.model.util.CachingRegistry;

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
	
	/* -----------------  ----------------- */
	
	public SemanticResolution getSemanticResolution(Path path) throws ExecutionException {
		BundlePath bundlePath = new BundlePath(path);
		return getSemanticResolution(bundlePath);
	}
	
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
		protected synchronized boolean checkIsStale() {
			
			srEntriesLock.lock();
			try {
				if(isInternallyStale()) {
					return true;
				}
				SemanticResolution existingSR = getExistingValue();
				DubBundle[] bundleDeps = existingSR.getBundleDeps();
				
				for (DubBundle bundle : bundleDeps) {
					BundlePath depBundlePath = BundlePath.createUnchecked(bundle.getLocation());
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
		public void makeStale() {
			// Note: don't lock on SemanticResolutionEntry lock
			getBundleManifestCache().getEntry(bundlePath).makeStale();
			super.makeStale();
		}
		
		
		@Override
		protected SemanticResolution doCreateNewValue() throws ExecutionException, InterruptedException {
			DubBundleDescription bundleDesc = getBundleManifestCache().getManifest(bundlePath);
			
			return new SemanticResolution(SemanticManager.this, bundleDesc);
		}
		
	}
	
	public void notifyManifestFileChanged(BundlePath bundlePath) {
		semanticResolutions.getEntry(bundlePath).makeStale();
	}
	
	
	public static class SemanticResolution extends SemanticContext {
		
		public SemanticResolution(SemanticManager manager, DubBundleDescription bundleDesc) {
			super(manager, bundleDesc);
		}
		
		protected DubBundle[] getBundleDeps() {
			return bundleDesc.getBundleDependencies();
		}
		
	}
	
}