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

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.io.IOException;
import java.io.InputStream;

import melnorme.utilbox.misc.IByteSequence;
import melnorme.utilbox.misc.StreamUtil;

/**
 * External Process Helper that reads all process output into a byte array (and another for stderr)
 */
public class ExternalProcessOutputReader extends ExternalProcessHelper {
	
	public static ExternalProcessOutputReader startProcess(ProcessBuilder pb, boolean redirectStdErr) 
			throws IOException {
		pb.redirectErrorStream(redirectStdErr);
		return new ExternalProcessOutputReader(pb);
	}
	
	protected ReadAllBytesTask mainReader;
	protected ReadAllBytesTask stderrReader;
	
	public ExternalProcessOutputReader(ProcessBuilder pb) throws IOException {
		super(pb);
	}
	
	@Override
	protected ReadAllBytesTask createMainReaderTask() {
		return mainReader = new ReadAllBytesTask(process.getInputStream());
	}
	
	@Override
	protected ReadAllBytesTask createStdErrReaderTask() {
		return stderrReader = new ReadAllBytesTask(process.getErrorStream());
	}
	
	@Override
	protected boolean isCanceled() {
		return false;
	}
	
	protected static class ReadAllBytesTask implements Runnable {
		
		protected final InputStream is;
		protected IByteSequence bytes;
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
		
		public synchronized IByteSequence getResult() throws IOException {
			if(exception != null) {
				throw exception;
			}
			return assertNotNull(bytes);
		}
	}
	
	public IByteSequence getStdOutBytes() throws IOException {
		assertTrue(isFullyTerminated());
		return mainReader.getResult();
	}
	
	public IByteSequence getStdErrBytes() throws IOException {
		assertTrue(isFullyTerminated());
		assertTrue(redirectErrorStream == false);
		return stderrReader.getResult();
	}
	
}