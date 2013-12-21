/*******************************************************************************
 * Copyright (c) 2013, 2013 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package melnorme.utilbox.concurrency;

import java.util.concurrent.CountDownLatch;

/**
 * A latchRunnable provides a two way (entry and exit) concurrency barrier.
 * It is useful mostly for automated tests related to concurrent code.
 */
public class LatchRunnable implements Runnable, AutoCloseable {
	
	public final CountDownLatch entryLatch = new CountDownLatch(1);
	public final CountDownLatch exitLatch = new CountDownLatch(1);
	
	public LatchRunnable() {
	}
	
	@Override
	public void run() {
		entryLatch.countDown();
		while(true) {
			try {
				exitLatch.await();
				return;
			} catch (InterruptedException e) {
				continue;
			}
		}
	}
	
	public void awaitEntry() throws InterruptedException {
		entryLatch.await();
	}
	
	public void releaseAll() {
		entryLatch.countDown();
		exitLatch.countDown();
	}
	
	@Override
	public void close() {
		releaseAll(); // Ensure whatever was holding on the latch is released, even for error cleanup
	}
	
}