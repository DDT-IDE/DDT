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

import java.nio.file.Path;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import melnorme.lang.ide.core.LangCore;
import melnorme.lang.ide.core.utils.process.RunExternalProcessTask;
import melnorme.lang.ide.core.utils.process.EclipseExternalProcessHelper;
import melnorme.lang.ide.core.utils.process.IExternalProcessListener;
import melnorme.lang.ide.core.utils.process.IRunProcessTask;
import melnorme.utilbox.concurrency.ITaskAgent;
import melnorme.utilbox.core.ExceptionAdapter;
import melnorme.utilbox.core.fntypes.ICallable;
import melnorme.utilbox.misc.ListenerListHelper;
import melnorme.utilbox.process.ExternalProcessHelper.ExternalProcessResult;
import mmrnmhrm.core.CoreTaskAgent;
import mmrnmhrm.core.DeeCore;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Manages an executor agent to run external DUB commands
 */
public class DubProcessManager {
	
	protected final ITaskAgent dubProcessAgent = new CoreTaskAgent(getClass().getSimpleName());
	
	public void shutdownNow() {
		dubProcessAgent.shutdownNow();
	}
	
	/* ----------------- listeners ----------------- */
	
	public static interface IDubProcessListener {
		
		void handleDubOperationStarted(IDubOperation dubOperation);
		
	}
	
	protected final ListenerListHelper<IDubProcessListener> processListenersHelper = new ListenerListHelper<>();
	
	public void addDubProcessListener(IDubProcessListener dubProcessListener) {
		processListenersHelper.addListener(dubProcessListener);
	}
	
	public void removeDubProcessListener(IDubProcessListener dubProcessListener) {
		processListenersHelper.removeListener(dubProcessListener);
	}
	
	public static interface IDubOperation {
		public IProject getProject();
		public String getOperationName();
		
		public void addExternalProcessListener(IExternalProcessListener processListener);
		
	}
	
	public void notifyOperationStarted(IDubOperation dubOperation) {
		for(IDubProcessListener processListener : processListenersHelper.getListeners()) {
			processListener.handleDubOperationStarted(dubOperation);
		}
	}
	
	/* ----------------------------------- */
	
	public Future<?> submitDubCommand(IRunProcessTask task) {
		return dubProcessAgent.submit(task);
	}
	
	public ExternalProcessResult submitDubCommandAndWait(IRunProcessTask task) throws CoreException {
		try {
			return submitAndGetTask(task);
		} catch (InterruptedException e) {
			throw LangCore.createCoreException("Unexpected interruption", e);
		}
	}
	
	public <T> T submitAndGetTask(ICallable<T, CoreException> task) throws InterruptedException, CoreException {
		Future<T> future = dubProcessAgent.submit(task);
		try {
			return future.get();
		} catch (InterruptedException e) {
			future.cancel(true);
			throw e;
		} catch (ExecutionException e) {
			Throwable cause = e.getCause();
			if(cause instanceof CoreException) {
				throw (CoreException) cause;
			}
			throw ExceptionAdapter.unchecked(cause); // Should not happen
		}
	}
	
	public IRunProcessTask newDubOperation(String operationName, IProject project, 
			String[] commands, IProgressMonitor monitor) {
		ProcessBuilder pb = createProcessBuilder(project, commands);
		return new RunDubProcessOperation(operationName, pb, project, monitor);
	}
	
	public static ProcessBuilder createProcessBuilder(IProject project, String... commands) {
		Path workingDir = project != null ?
			project.getLocation().toFile().toPath() :
			DeeCore.getWorkspaceRoot().getLocation().toFile().toPath();
		return new ProcessBuilder(commands).directory(workingDir.toFile());
	}
	
	public class RunDubProcessOperation extends RunExternalProcessTask<IExternalProcessListener> implements IDubOperation {
		
		protected final String operationName;
		
		protected RunDubProcessOperation(String operationName, ProcessBuilder pb, IProject project,
				IProgressMonitor cancelMonitor) {
			super(pb, project, cancelMonitor, new ListenerListHelper<IExternalProcessListener>());
			this.operationName = operationName;
		}
		
		@Override
		public EclipseExternalProcessHelper startProcess() throws CoreException {
			notifyOperationStarted(this);
			return super.startProcess();
		}
		
		@Override
		public IProject getProject() {
			return project;
		}
		
		@Override
		public String getOperationName() {
			return operationName;
		}
		
		@Override
		public void addExternalProcessListener(IExternalProcessListener processListener) {
			listenersList.addListener(processListener);
		}
		
	}
	
	public static class DubCompositeOperation implements IDubOperation {
		
		protected final String operationName;
		protected final IProject project;
		protected final ListenerListHelper<IExternalProcessListener> listenerListHelper = new ListenerListHelper<>();
		
		public DubCompositeOperation(String operationName, IProject project) {
			this.project = project;
			this.operationName = operationName;
		}
		
		@Override
		public IProject getProject() {
			return project;
		}
		
		@Override
		public String getOperationName() {
			return operationName;
		}
		
		@Override
		public void addExternalProcessListener(IExternalProcessListener processListener) {
			listenerListHelper.addListener(processListener);
		}
		
		public ListenerListHelper<IExternalProcessListener> getListenersList() {
			return listenerListHelper;
		}
		
		public IRunProcessTask newDubProcessTask(IProject project, String[] commands, IProgressMonitor monitor) {
			ProcessBuilder pb = createProcessBuilder(project, commands);
			return new RunDubProcessTask(pb, project, monitor, listenerListHelper);
		}
		
	}
	
	public static class RunDubProcessTask extends RunExternalProcessTask<IExternalProcessListener> {
		protected RunDubProcessTask(ProcessBuilder pb, IProject project, IProgressMonitor cancelMonitor,
				ListenerListHelper<IExternalProcessListener> listenersList) {
			super(pb, project, cancelMonitor, listenersList);
		}
	}
	
}