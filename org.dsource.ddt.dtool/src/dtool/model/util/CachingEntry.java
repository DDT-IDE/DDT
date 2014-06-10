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

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

/**
 * An entry caching some value. 
 * The value has an associated timestamp, which can be marked as out of date.
 */
public abstract class CachingEntry<VALUE> {
	
	private volatile VALUE value;
	private volatile long valueTimeStamp = -1;
	private volatile long invalidationTimeStamp = 0;
	
	public CachingEntry() {
	}
	
	public VALUE getValue() {
		return value;
	}
	
	public long getValueTimeStamp() {
		return valueTimeStamp;
	}
	
	
	public synchronized boolean isStale() {
		return invalidationTimeStamp > valueTimeStamp;
	}
	
	public synchronized void updateValue(VALUE value, long newTimeStamp) {
		this.value = value;
		assertTrue(newTimeStamp >= valueTimeStamp);
		valueTimeStamp = newTimeStamp;
	}
	
	public synchronized void makeStale() {
		do {
			long currentTime = System.nanoTime();
			invalidationTimeStamp = currentTime;
			// Extremely unlikely, but if makeStale is called soon after updateTimeStamp, 
			// then nanoTime may return the same value. If so, loop until currentTime increases. 
		} while(invalidationTimeStamp <= valueTimeStamp);
		assertTrue(isStale());
	}
	
}