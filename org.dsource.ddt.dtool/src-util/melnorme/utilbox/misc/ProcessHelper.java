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
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import melnorme.utilbox.misc.ByteArrayOutputStreamExt;
import melnorme.utilbox.misc.StreamUtil;

/**
 * Helper class designed to safely read (with regards to concurrency) the output of a sub-process.
 */
public class ProcessHelper {
	
	protected Process process;
	
	protected CountDownLatch latch;
	protected ReaderThread stdoutReader;
	protected ReaderThread stderrReader;

	public ProcessHelper() {
	}
	
	public void startProcess(ProcessBuilder pb) throws IOException {
		pb.redirectErrorStream(false);
		process = pb.start();
		
		latch = new CountDownLatch(2);
		
		stdoutReader = new ReaderThread(latch, process.getInputStream());
		stderrReader = new ReaderThread(latch, process.getErrorStream());
		stdoutReader.start();
		stderrReader.start();
	}
	
	public int awaitTermination(int timeout) throws InterruptedException {
		try {
			boolean success = latch.await(timeout, TimeUnit.MILLISECONDS);
			if(!success) {
				throw new InterruptedException();
			}
			// if success, then reader threads are about to terminate (if not terminated already). 
			stdoutReader.join();
			stderrReader.join();
			return process.waitFor();
		} catch(InterruptedException e) {
			stdoutReader.interrupt();
			stderrReader.interrupt();
			throw e;
		}
	}
	
	public ByteArrayOutputStreamExt getStdOutBytes() throws IOException {
		return stdoutReader.getResult();
	}
	
	public ByteArrayOutputStreamExt getStdErrBytes() throws IOException {
		return stderrReader.getResult();
	}
	
	protected static class ReaderThread extends Thread {
		
		protected CountDownLatch latch;
		protected InputStream is;
		protected ByteArrayOutputStreamExt bytes;
		protected IOException exception;
		
		public ReaderThread(CountDownLatch latch, InputStream is) {
			this.latch = latch;
			this.is = is;
		}
		
		@Override
		public void run() {
			try {
				bytes = StreamUtil.readAllBytesFromStream(is);
			} catch (IOException e) {
				this.exception = e;
			} finally {
				latch.countDown();
			}
		}
		
		public synchronized ByteArrayOutputStreamExt getResult() throws IOException {
			assertTrue(isAlive() == false);
			if(isInterrupted()) {
				throw new IOException(new InterruptedException());
			}
			if(exception != null) {
				throw exception;
			}
			return assertNotNull(bytes);
		}
		
	}
	
}