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
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;

import melnorme.lang.ide.core.LangCore;
import melnorme.utilbox.concurrency.ExternalProcessOutputHelper;
import melnorme.utilbox.core.fntypes.ICallable;
import mmrnmhrm.core.DeeCore;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

public class RunExternalProcessTask implements ICallable<ExternalProcessOutputHelper, CoreException> {
	protected final IProgressMonitor monitor;
	protected final ProcessBuilder pb;
	protected final List<IExternalProcessListener> listeners;
	
	protected RunExternalProcessTask(IProgressMonitor monitor, ProcessBuilder pb, 
			List<IExternalProcessListener> listeners) {
		this.monitor = monitor;
		this.pb = pb;
		this.listeners = listeners;
	}
	
	@Override
	public ExternalProcessOutputHelper call() throws CoreException {
		return runDubProcess(pb, monitor);
	}
	
	protected ExternalProcessEclipseHelper runDubProcess(ProcessBuilder pb, IProgressMonitor cancelMonitor) 
			throws CoreException {
		ExternalProcessEclipseHelper processHelper;
		try {
			processHelper = new ExternalProcessEclipseHelper(pb, false, cancelMonitor);
		} catch (IOException e) {
			throw createDubProcessException("Could not start process: ",  e);
		}
		
		notifyDubProcessStarted(processHelper, pb);		
		processHelper.startReaderThreads();
		
		try {
			processHelper.awaitTerminationStrict_destroyOnException();
		} catch (InterruptedException e) {
			throw createDubProcessException("Interrupted awaiting process termination.", null);
		} catch (TimeoutException e) {
			assertTrue(cancelMonitor.isCanceled());
			throw createDubProcessException("Task cancelled, process forcibly terminated.", null);
		}
		
		return processHelper;
	}
	
	protected CoreException createDubProcessException(String message, IOException e) {
		return new CoreException(DeeCore.createErrorStatus(message, e));
	}
	
	public void notifyDubProcessStarted(ExternalProcessOutputHelper processHelper, ProcessBuilder pb) {
		for (IExternalProcessListener dubProcessListener : listeners) {
			dubProcessListener.handleProcessStarted(processHelper, pb);
		}
	}
	
	public static class ExternalProcessEclipseHelper extends ExternalProcessOutputHelper {
		
		protected final IProgressMonitor monitor;
		
		public ExternalProcessEclipseHelper(ProcessBuilder pb, boolean startReaders, IProgressMonitor monitor) 
				throws IOException {
			super(pb.start(), true, startReaders);
			this.monitor = assertNotNull(monitor);
		}
		
		@Override
		protected boolean isCanceled() {
			return monitor.isCanceled();
		}
		
		@Override
		protected void handleListenerException(RuntimeException e) {
			LangCore.logError(e, "Internal error notifying listener");
		}
		
	}
	
}