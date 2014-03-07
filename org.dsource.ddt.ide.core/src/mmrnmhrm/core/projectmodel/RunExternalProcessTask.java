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
import java.util.concurrent.TimeoutException;

import melnorme.lang.ide.core.LangCore;
import melnorme.utilbox.concurrency.ExternalProcessOutputHelper;
import melnorme.utilbox.core.fntypes.ICallable;
import melnorme.utilbox.misc.ListenerListHelper;
import mmrnmhrm.core.DeeCore;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

public class RunExternalProcessTask implements ICallable<ExternalProcessOutputHelper, CoreException> {
	
	protected final ProcessBuilder pb;
	protected final IProject project;
	protected final IProgressMonitor cancelMonitor;
	protected final ListenerListHelper<IExternalProcessListener> listenersHelper;
	
	protected RunExternalProcessTask(ProcessBuilder pb, IProject project, IProgressMonitor cancelMonitor, 
			ListenerListHelper<IExternalProcessListener> listenersHelper) {
		this.pb = pb;
		this.project = project;
		this.cancelMonitor = cancelMonitor;
		this.listenersHelper = listenersHelper;
	}
	
	@Override
	public ExternalProcessOutputHelper call() throws CoreException {
		return startProcessAndAwait();
	}
	
	public void notifyProcessStarted(ExternalProcessOutputHelper processHelper) {
		for (IExternalProcessListener dubProcessListener : listenersHelper.getListeners()) {
			dubProcessListener.handleProcessStarted(pb, project.getName(), processHelper);
		}
	}
	
	public void notifyProcessFailedToStart(IOException e) {
		for (IExternalProcessListener dubProcessListener : listenersHelper.getListeners()) {
			dubProcessListener.handleProcessFailedToStarted(pb, project.getName(), e);
		}
	}
	
	protected ExternalProcessEclipseHelper startProcessAndAwait() throws CoreException {
		ExternalProcessEclipseHelper processHelper;
		try {
			processHelper = new ExternalProcessEclipseHelper(pb, false, cancelMonitor);
		} catch (IOException e) {
			notifyProcessFailedToStart(e);
			throw createDubProcessException("Could not start process: ",  e);
		}
		
		notifyProcessStarted(processHelper);		
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
			LangCore.logError("Internal error notifying listener", e);
		}
		
	}
	
}