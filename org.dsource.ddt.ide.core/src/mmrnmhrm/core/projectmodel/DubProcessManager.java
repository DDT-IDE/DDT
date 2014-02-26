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

import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import melnorme.lang.ide.core.LangCore;
import melnorme.utilbox.concurrency.ExternalProcessOutputHelper;
import melnorme.utilbox.concurrency.IExecutorAgent;
import melnorme.utilbox.misc.ListenerListHelper;
import mmrnmhrm.core.CoreExecutorAgent;
import mmrnmhrm.core.DeeCore;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Manages an executor agent to run external DUB commands
 */
public class DubProcessManager {
	
	protected final IExecutorAgent dubProcessExecutor = new CoreExecutorAgent(getClass().getSimpleName());
	
	/* ----------------------------------- */
	
	protected final ListenerListHelper<IDubProcessListener> dubProcessListenersHelper = new ListenerListHelper<>();
	
	public void addDubProcessListener(IDubProcessListener dubProcessListener) {
		dubProcessListenersHelper.addListener(dubProcessListener);
	}
	
	public void removeDubProcessListener(IDubProcessListener dubProcessListener) {
		dubProcessListenersHelper.removeListener(dubProcessListener);
	}
	
	public Future<DubExternalProcessHelper> submitDubCommand(DubExternalProcessTask task) {
		return dubProcessExecutor.submit(task);
	}
	
	public DubExternalProcessHelper submitDubCommandAndWait(IProject project, IProgressMonitor monitor, 
			String... commands) throws InterruptedException, CoreException {
		return submitDubCommandAndWait(newDubExternalProcessTask(project, monitor, commands));
	}
	
	public DubExternalProcessTask newDubExternalProcessTask(IProject project, IProgressMonitor monitor,
			String... commands) {
		final Path location = project.getLocation().toFile().toPath();
		final ProcessBuilder pb = new ProcessBuilder(commands).directory(location.toFile());
		return new DubExternalProcessTask(monitor, project, pb);
	}
	
	public DubExternalProcessHelper submitDubCommandAndWait(DubExternalProcessTask task) 
			throws InterruptedException, CoreException {
		Future<DubExternalProcessHelper> future = dubProcessExecutor.submit(task);
		
		try {
			return future.get();
		} catch (InterruptedException e) {
			/// XXX: review this code
			future.cancel(true);
			throw e;
		} catch (ExecutionException e) {
			Throwable cause = e.getCause();
			if(cause instanceof CoreException) {
				throw (CoreException) cause;
			}
			if(cause instanceof RuntimeException) {
				throw (RuntimeException) cause;
			}
			throw assertFail();
		}
	}
	
	public class DubExternalProcessTask implements Callable<DubExternalProcessHelper> {
		protected final IProgressMonitor monitor;
		protected final IProject project;
		protected final ProcessBuilder pb;
		
		public DubExternalProcessTask(IProgressMonitor monitor, IProject project, ProcessBuilder pb) {
			this.monitor = monitor;
			this.project = project;
			this.pb = pb;
		}
		
		@Override
		public DubExternalProcessHelper call() throws CoreException {
			return runDubProcess(pb, project, monitor);
		}
	}
	
	protected DubExternalProcessHelper runDubProcess(ProcessBuilder pb, IProject project, 
			IProgressMonitor cancelMonitor) 
			throws CoreException {
		DubExternalProcessHelper processHelper;
		try {
			processHelper = new DubExternalProcessHelper(pb, false, cancelMonitor);
		} catch (IOException e) {
			throw createDubProcessException("Could not start dub process: ",  e);
		}
		
		notifyDubProcessStarted(processHelper, pb, project);		
		processHelper.startReaderThreads();
		
		try {
			processHelper.awaitTerminationStrict_destroyOnException();
		} catch (InterruptedException e) {
			if(cancelMonitor.isCanceled()) {
				throw createDubProcessException("Cancelled dub process.", null);
			}
			throw createDubProcessException("Interrupted running dub process.", null);
		}
		
		return processHelper;
	}
	
	protected static CoreException createDubProcessException(String message, IOException e) {
		return new CoreException(DeeCore.createErrorStatus(message, e));
	}
	
	public void notifyDubProcessStarted(ExternalProcessOutputHelper processHelper, ProcessBuilder pb, 
			IProject project) {
		List<IDubProcessListener> listeners = dubProcessListenersHelper.getListeners();
		for (IDubProcessListener dubProcessListener : listeners) {
			dubProcessListener.handleProcessStarted(processHelper, pb, project);
		}
	}
	
}

class DubExternalProcessHelper extends ExternalProcessOutputHelper {
	
	protected final IProgressMonitor monitor;
	protected final ProcessBuilder pb;
	
	public DubExternalProcessHelper(ProcessBuilder pb, boolean startReaders, IProgressMonitor monitor) 
			throws IOException {
		super(pb.start(), true, startReaders);
		this.pb = pb;
		this.monitor = monitor;
	}
	
	public Path getLocation() {
		return pb.directory().toPath();
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