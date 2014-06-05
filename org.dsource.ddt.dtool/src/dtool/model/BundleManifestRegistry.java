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

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import melnorme.utilbox.concurrency.ITaskAgent;
import melnorme.utilbox.core.fntypes.ICallable;
import dtool.dub.BundlePath;
import dtool.dub.DubBundleDescription;
import dtool.dub.DubHelper;
import dtool.model.util.CachingEntry;
import dtool.model.util.CachingRegistry;

/**
 * Maintains a registry of parsed bundle manifests, index by bundle path.
 */
public class BundleManifestRegistry extends CachingRegistry<BundlePath, BundleManifestRegistry.DubManifestEntry> {
	
	protected final ITaskAgent processAgent;
	
	public BundleManifestRegistry(DToolServer dtoolServer) {
		this.processAgent = dtoolServer.dubProcessAgent;
	}
	
	/* -----------------  ----------------- */
	
	public DubBundleDescription getManifest(BundlePath bundlePath) throws ExecutionException {
		return getEntry(bundlePath).getValue();
	}
	
	@Override
	protected DubManifestEntry createEntry(BundlePath bundlePath) {
		return new DubManifestEntry(bundlePath);
	}
	
	protected class DubManifestEntry extends CachingEntry<DubBundleDescription>{
		
		protected final BundlePath bundlePath;
		
		public DubManifestEntry(BundlePath bundlePath) {
			this.bundlePath = bundlePath;
		}
		
		@Override
		protected DubBundleDescription doCreateNewValue() throws ExecutionException, InterruptedException {
			return processAgent.submit(new CalculateEntryOperation()).get();
		}
		
		protected class CalculateEntryOperation implements ICallable<DubBundleDescription, Exception> {
			
			@Override
			public DubBundleDescription call() throws IOException, InterruptedException {
				return DubHelper.runDubDescribe(bundlePath.path);
			}
			
		}
		
	}
	
}