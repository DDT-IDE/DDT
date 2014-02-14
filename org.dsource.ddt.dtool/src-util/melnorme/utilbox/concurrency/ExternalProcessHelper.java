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

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Helper to start an external process and read its output concurrently,
 * using one or two reader threads (for stdout and stderr).
 * It also supports waiting for process termination with timeouts. 
 */
public abstract class ExternalProcessHelper {
	
	protected final boolean redirectErrorStream;
	protected final Process process;
	
	protected final CountDownLatch fullTerminationLatch;
	
	protected final Thread mainReaderThread;
	protected final Thread stderrReaderThread; // Can be null
	
	public ExternalProcessHelper(ProcessBuilder pb) throws IOException {
		redirectErrorStream = pb.redirectErrorStream();
		process = pb.start();
		
		fullTerminationLatch = new CountDownLatch(2);
		
		mainReaderThread = new ProcessHelperMainThread(createMainReaderTask());
		mainReaderThread.start();
		
		if(!redirectErrorStream) {
			stderrReaderThread = new ProcessHelperStderrThread(createStdErrReaderTask());
			stderrReaderThread.start();
		} else {
			fullTerminationLatch.countDown(); // dont start stderr thread, so update latch
			stderrReaderThread = null;
		}
	}
	
	public Process getProcess() {
		return process;
	}
	
	public boolean isRedirectingErrorStream() {
		return redirectErrorStream;
	}
	
	public boolean isFullyTerminated() {
		return fullTerminationLatch.getCount() == 0;
	}
	
	protected abstract Runnable createMainReaderTask();
	
	protected abstract Runnable createStdErrReaderTask();
	
	/** {@link #awaitTermination(int)} with no timeout */ 
	public int awaitTermination() throws InterruptedException {
		return awaitTermination(-1);
	}
	
	/**
	 * Await termination of underlying process.
	 * If method returns sucessfully, process has terminated and all reader threads have finished processing.
	 * @param timeoutMs timeout in milliseconds to wait for. -1 for no timeout (waiting indefinitely).
	 * @throws InterruptedException if timeout occurs, or cancel requested.   
	 */
	public int awaitTermination(int timeoutMs) throws InterruptedException {
		try {
			boolean success = awaitFullTerminationOrCancel(timeoutMs);
			if(!success) {
				throw new InterruptedException();
			}
		} catch(InterruptedException e) {
			process.destroy(); // This should ensure reader threads will terminate soon after
			throw e;
		}
		
		return process.exitValue();
	}
	
	protected boolean awaitFullTerminationOrCancel(int timeoutMs) throws InterruptedException {
		int waitedTime = 0;
		
		while(true) {
			int cancelPollPeriod = getCancelPollingPeriod();
			boolean latchSuccess = fullTerminationLatch.await(cancelPollPeriod, TimeUnit.MILLISECONDS);
			if(latchSuccess) {
				return true;
			}
			if(isCanceled()) {
				return false;
			}
			if(timeoutMs >= 0 && waitedTime >= timeoutMs) {
				return false;
			}
			waitedTime += cancelPollPeriod;
		}
	}
	
	protected int getCancelPollingPeriod() {
		return 200;
	}
	
	protected abstract boolean isCanceled();
	
	protected class ProcessHelperMainThread extends Thread {
		
		public ProcessHelperMainThread(Runnable runnable) {
			super(runnable);
			setDaemon(true);
		}
		
		@Override
		public void run() {
			try {
				super.run();
			} finally {
				waitForProcessIndefinitely();
				fullTerminationLatch.countDown();
			}
		}
		
		protected void waitForProcessIndefinitely() {
			while(true) {
				try {
					process.waitFor();
					return;
				} catch (InterruptedException e) {
					// retry waitfor, we must ensure process is terminated.
				}
			}
		}
		
	}
	
	protected class ProcessHelperStderrThread extends Thread {
		
		public ProcessHelperStderrThread(Runnable runnable) {
			super(runnable);
			setDaemon(true);
		}
		
		@Override
		public void run() {
			try {
				super.run();
			} finally {
				fullTerminationLatch.countDown();
			}
		}
		
	}
	
}