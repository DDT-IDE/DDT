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
package mmrnmhrm.core.build;

import java.io.IOException;

import melnorme.utilbox.misc.ExternalProcessHelper;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;

/**
 * External Process handler that notifies a listener every time a line of output is read.
 * Also, can kill external process reading if given monitor is canceled. 
 */
public class ExternalProcessLineNotifyHandler extends ExternalProcessHelper {
	
	protected final IProgressMonitor monitor;
	
	public ExternalProcessLineNotifyHandler(ProcessBuilder pb, IProgressMonitor monitor) throws IOException {
		super(pb);
		this.monitor = monitor;
	}
	
	@Override
	public int awaitTermination(int timeoutMs) throws InterruptedException {
		try {
			return super.awaitTermination(timeoutMs);
		} catch (InterruptedException e) {
			if(isCanceled()) {
				throw new OperationCanceledException();
			}
			throw e;
		}
	}
	
	@Override
	protected boolean isCanceled() {
		return monitor.isCanceled();
	}
	
	@Override
	protected Runnable createMainReaderTask() {
		return new ReadLineNotifyTask(process.getInputStream()) {
			@Override
			protected void handleReadLine(String line) {
				ExternalProcessLineNotifyHandler.this.handleReadLine(line);
			}
		};
	}
	
	@Override
	protected Runnable createStdErrReaderTask() {
		return new ReadLineNotifyTask(process.getErrorStream()) {
			@Override
			protected void handleReadLine(String line) {
				ExternalProcessLineNotifyHandler.this.handleReadLine(line);
			}
		};
	}
	
	@SuppressWarnings("unused")
	protected void handleReadLine(String line) {
		// Default implementation: do nothing
	}
	
}