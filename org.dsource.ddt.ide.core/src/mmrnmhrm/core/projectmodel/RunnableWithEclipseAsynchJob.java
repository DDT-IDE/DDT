/*******************************************************************************
 * Copyright (c) 2014, 2014 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package mmrnmhrm.core.projectmodel;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;

import java.util.concurrent.CountDownLatch;

import mmrnmhrm.core.DeeCore;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.Job;

/**
 * A {@link RunnableWithEclipseAsynchJob} encompasses some task that should run associated with an Eclipse Job
 * (thus possibly visible to the UI, and cancellable),  but whose actual running code must execute 
 * in a thread other than the job thread.
 */
public abstract class RunnableWithEclipseAsynchJob implements Runnable {
	
	protected final CountDownLatch jobStartLatch = new CountDownLatch(1);
	
	protected IProgressMonitor monitor;
	
	@Override
	public void run() {
		Job job = new Job(getNameForJob()) {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				RunnableWithEclipseAsynchJob.this.monitor = monitor;
				jobStartLatch.countDown();
				return ASYNC_FINISH;
			}
		};
		job.schedule();
		
		try {
			try {
				jobStartLatch.await();
				assertNotNull(monitor);
			} catch (InterruptedException e) {
				return;
			}
			
			job.setThread(Thread.currentThread());
			runWithMonitor(monitor);
		} finally {
			job.done(DeeCore.createStatus(null)); 
		}
	}
	
	protected abstract String getNameForJob();
	
	/** The main code to run */
	protected abstract void runWithMonitor(IProgressMonitor monitor);
	
}