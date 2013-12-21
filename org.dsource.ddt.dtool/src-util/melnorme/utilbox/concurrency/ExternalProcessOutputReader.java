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

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.io.IOException;

import melnorme.utilbox.misc.ByteArrayOutputStreamExt;

/**
 * Helper class designed to safely read (with regards to concurrency) the output of a sub-process.
 */
public class ExternalProcessOutputReader extends ExternalProcessHelper {
	
	protected ReadAllBytesTask mainReader;
	protected ReadAllBytesTask stderrReader;
	
	public static ExternalProcessOutputReader startProcess(ProcessBuilder pb, boolean redirectStdErr) 
			throws IOException {
		pb.redirectErrorStream(redirectStdErr);
		return new ExternalProcessOutputReader(pb);
	}
	
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
	
	public ByteArrayOutputStreamExt getStdOutBytes() throws IOException {
		return mainReader.getResult();
	}
	
	public ByteArrayOutputStreamExt getStdErrBytes() throws IOException {
		assertTrue(redirectErrorStream == false);
		return stderrReader.getResult();
	}
	
}