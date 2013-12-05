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
package melnorme.utilbox.misc;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Helper to start an external process and read its output concurrently,
 * while support cancellation and timeouts. 
 */
public abstract class ExternalProcessHelper {
	
	protected final boolean redirectErrorStream;
	protected final Process process;
	
	protected final CountDownLatch threadTerminationLatch;
	
	protected final Thread mainReaderThread;
	protected final Thread stderrReaderThread; // Can be null
	
	public ExternalProcessHelper(ProcessBuilder pb) throws IOException {
		redirectErrorStream = pb.redirectErrorStream();
		process = pb.start();
		
		threadTerminationLatch = new CountDownLatch(2);
		
		mainReaderThread = createAndStartMainReaderThread();
		
		if(!redirectErrorStream) {
			stderrReaderThread = createAndStartStdErrReaderThread();
		} else {
			threadTerminationLatch.countDown(); // dont start stderr thread, so update latch
			stderrReaderThread = null;
		}
	}
	
	/** Create and start main reader thread. 
	 * It is essential that the thread runs {@link Process#waitFor()} before terminating. */
	protected LatchCountdownThread createAndStartMainReaderThread() {
		Runnable mainReader = new RunAndWaitForProcessTask(createMainReaderTask());
		LatchCountdownThread mainReaderThread = new LatchCountdownThread(mainReader);
		mainReaderThread.start();
		return mainReaderThread;
	}
	
	/** Create and start StdErr reader thread. */
	protected LatchCountdownThread createAndStartStdErrReaderThread() {
		Runnable stderrReader = createStdErrReaderTask();
		LatchCountdownThread stderrReaderThread = new LatchCountdownThread(stderrReader);
		stderrReaderThread.start();
		return stderrReaderThread;
	}
	
	public boolean isRedirectingErrorStream() {
		return redirectErrorStream;
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
			boolean success = awaitLatchOrCancel(timeoutMs);
			if(!success) {
				throw new InterruptedException();
			}
		} catch(InterruptedException e) {
			process.destroy(); // This should ensure reader threads will terminate soon after
			throw e;
		}
		
		mainReaderThread.join();
		if(stderrReaderThread != null) {
			stderrReaderThread.join();
		}
		return process.exitValue();
	}
	
	protected boolean awaitLatchOrCancel(int timeoutMs) throws InterruptedException {
		int waitedTime = 0;
		
		while(true) {
			int cancelPollPeriod = getCancelPollingPeriod();
			boolean success = threadTerminationLatch.await(cancelPollPeriod, TimeUnit.MILLISECONDS);
			if(success) {
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
	
	protected class RunAndWaitForProcessTask implements Runnable {
		
		protected final Runnable task;
		
		public RunAndWaitForProcessTask(Runnable task) {
			this.task = task;
		}
		
		@Override
		public void run() {
			task.run();
			try {
				process.waitFor();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
	}
	
	protected class LatchCountdownThread extends Thread {
		
		protected final Runnable runnable;
		
		public LatchCountdownThread(Runnable runnable) {
			this.runnable = runnable;
			setDaemon(true);
		}
		
		@Override
		public void run() {
			try { 
				runnable.run();
			} finally {
				threadTerminationLatch.countDown();
			}
		}
		
	}
	
	protected static class ReadAllBytesTask implements Runnable {
		
		protected final InputStream is;
		protected ByteArrayOutputStreamExt bytes;
		protected IOException exception;
		
		public ReadAllBytesTask(InputStream is) {
			this.is = is;
		}
		
		@Override
		public void run() {
			try {
				bytes = StreamUtil.readAllBytesFromStream(is);
			} catch (IOException e) {
				this.exception = e;
			}
		}
		
		public synchronized ByteArrayOutputStreamExt getResult() throws IOException {
			if(exception != null) {
				throw exception;
			}
			return assertNotNull(bytes);
		}
	}
	
	protected static abstract class ReadLineNotifyTask implements Runnable {
		
		protected final InputStream inputStream;
		protected IOException exception;
		
		public ReadLineNotifyTask(InputStream inputStream) {
			this.inputStream = inputStream;
		}
		
		@Override
		public void run() {
			try(BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
				String line;
				while ((line = reader.readLine()) != null) {
					handleReadLine(line);
				}
			} catch (IOException ioe) {
				this.exception = ioe;
			}
		}
		
		protected abstract void handleReadLine(String line);
	}
}