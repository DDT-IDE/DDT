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

import java.util.concurrent.ExecutionException;

import dtool.engine.util.CachingRegistry;


public abstract class AbstractCachingManager<KEY, VALUE> {
	
	public AbstractCachingManager() {
	}
	
	protected final CachingRegistry<KEY, VALUE> infos = new CachingRegistry<KEY, VALUE>() {
		
		@Override
		protected VALUE createEntry(KEY key) {
			return doCreateEntry(key);
		}
	};
	
	protected abstract VALUE doCreateEntry(KEY key);
	
	protected VALUE getEntry(KEY key) {
		return infos.getEntry(key);
	}
	
	public boolean checkIsEntryStale(KEY bundleKey) {
		VALUE entry = infos.getEntryOrNull(bundleKey);
		return entry == null ? true : doCheckIsEntryStale(entry);
	}
	
	public abstract boolean doCheckIsEntryStale(VALUE entry);
	
	public VALUE getUpdatedEntry(KEY key) throws ExecutionException {
		VALUE entry = getEntry(key);
		if(doCheckIsEntryStale(entry)) {
			return updateManifestEntry(key);
		}
		return entry;
	}
	
	protected abstract VALUE updateManifestEntry(KEY key) throws ExecutionException;
	
	/* -----------------  TODO: Need to review the usage of these locks ----------------- */
	
	protected final Object entriesLock = new Object();
	protected final Object updateOperationLock = new Object();
	
}