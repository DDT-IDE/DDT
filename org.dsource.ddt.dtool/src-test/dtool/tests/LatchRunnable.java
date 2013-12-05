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
package dtool.tests;

import java.util.concurrent.CountDownLatch;

public class LatchRunnable implements Runnable, AutoCloseable {
	
	protected final CountDownLatch latch;
	
	public LatchRunnable() {
		this.latch = new CountDownLatch(1);
	}
	
	@Override
	public void run() {
		try {
			latch.await();
		} catch (InterruptedException e) {
			throw melnorme.utilbox.core.ExceptionAdapter.unchecked(e);
		}
	}
	
	public void release() {
		latch.countDown();
	}
	
	@Override
	public void close() {
		release(); // Ensure whatever was holding on the latch is release, even for error cleanup
	}
	
}