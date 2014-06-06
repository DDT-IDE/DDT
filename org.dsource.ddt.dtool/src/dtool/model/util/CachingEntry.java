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
package dtool.model.util;

import java.util.concurrent.ExecutionException;

/**
 * An entry caching some value. Value can be created on demand, and invalid manually.
 */
public abstract class CachingEntry<VALUE> {
	
	protected volatile VALUE value;
	protected volatile long latestAvailableStamp = 0;
	protected volatile long creationStamp = -1;
	
	public CachingEntry() {
	}
	
	public final boolean isInternallyStale() {
		return latestAvailableStamp > creationStamp;
	}
	
	public void makeStale() {
		latestAvailableStamp = System.nanoTime();
	}
	
	public long getLatestAvailableStamp() {
		return latestAvailableStamp;
	}
	
	public long getCreationStamp() {
		return creationStamp;
	}
	
	protected boolean checkIsStale() {
		return isInternallyStale();
	}
	
	/** Get existing value, even if it's stale. */
	public VALUE getExistingValue() {
		return value;
	}
	
	public synchronized VALUE getValue() throws ExecutionException {
		if(checkIsStale()) {
			creationStamp = System.nanoTime();
			try {
				this.value = doCreateNewValue();
			} catch (InterruptedException e) {
				throw new ExecutionException(e);
			}
		}
		return value;
	}
	
	protected abstract VALUE doCreateNewValue() throws ExecutionException, InterruptedException;
	
}