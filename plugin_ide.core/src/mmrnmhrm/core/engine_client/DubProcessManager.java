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
package mmrnmhrm.core.engine_client;

import java.nio.file.Path;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import melnorme.lang.ide.core.LangCore;
import melnorme.lang.ide.core.utils.CoreTaskAgent;
import melnorme.lang.ide.core.utils.EclipseUtils;
import melnorme.lang.ide.core.utils.process.AbstractRunProcessTask;
import melnorme.lang.ide.core.utils.process.IRunProcessTask;
import melnorme.lang.ide.core.utils.process.IStartProcessListener;
import melnorme.lang.ide.core.utils.process.RunExternalProcessTask;
import melnorme.utilbox.concurrency.ITaskAgent;
import melnorme.utilbox.core.CommonException;
import melnorme.utilbox.core.ExceptionAdapter;
import melnorme.utilbox.core.fntypes.ICallable;
import melnorme.utilbox.misc.ListenerListHelper;
import melnorme.utilbox.misc.ListenersHelper;
import melnorme.utilbox.process.ExternalProcessHelper.ExternalProcessResult;
import melnorme.utilbox.process.ExternalProcessNotifyingHelper;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Manages an executor agent to run external DUB commands
 */
public class DubProcessManager extends ListenerListHelper<IDubProcessListener> {
	
	protected final ITaskAgent dubProcessAgent = new CoreTaskAgent(getClass().getSimpleName());
	
	public DubProcessManager() {
	}
	
	public void shutdownNow() {
		dubProcessAgent.shutdownNow();
	}
	
	/* ----------------- listeners ----------------- */
	
	public static interface IDubOperation {
		public IProject getProject();
		public String getOperationName();
		
		public void addExternalProcessListener(IStartProcessListener processListener);
		
	}
	
	public void notifyOperationStarted(IDubOperation dubOperation) {
		for(IDubProcessListener processListener : getListeners()) {
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
	
	public RunDubProcessOperation newDubOperation(String operationName, IProject project, 
			String[] commands, IProgressMonitor monitor) {
		ProcessBuilder pb = createProcessBuilder(project, commands);
		return new RunDubProcessOperation(operationName, pb, project, monitor);
	}
	
	public static ProcessBuilder createProcessBuilder(IProject project, String... commands) {
		Path workingDir = project != null ?
			project.getLocation().toFile().toPath() :
			EclipseUtils.getWorkspaceRoot().getLocation().toFile().toPath();
		return new ProcessBuilder(commands).directory(workingDir.toFile());
	}
	
	public class RunDubProcessOperation extends AbstractRunProcessTask implements IDubOperation{
		
		protected final String operationName;
		protected final IProject project;
		protected ListenersHelper<IStartProcessListener> listenersHelper = new ListenersHelper<>();
		
		protected RunDubProcessOperation(String operationName, ProcessBuilder pb, IProject project,
				IProgressMonitor cancelMonitor) {
			super(pb, cancelMonitor);
			
			this.operationName = operationName;
			this.project = project;
		}
		
		@Override
		protected ExternalProcessNotifyingHelper startProcess(IProgressMonitor pm) throws CommonException {
			notifyOperationStarted(this);
			return super.startProcess(pm);
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
		protected void handleProcessStartResult(ExternalProcessNotifyingHelper processHelper, CommonException ce) {
			for(IStartProcessListener processListener : listenersHelper.getListeners()) {
				processListener.handleProcessStartResult(pb, project, processHelper, ce);
			}
		}
		
		@Override
		public void addExternalProcessListener(IStartProcessListener processListener) {
			listenersHelper.addListener(processListener);
		}

	}
	
	public static class DubCompositeOperation implements IDubOperation {
		
		protected final String operationName;
		protected final IProject project;
		protected final ListenerListHelper<IStartProcessListener> listenerListHelper = new ListenerListHelper<>();
		
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
		public void addExternalProcessListener(IStartProcessListener processListener) {
			listenerListHelper.addListener(processListener);
		}
		
		public ListenerListHelper<IStartProcessListener> getListenersList() {
			return listenerListHelper;
		}
		
		public RunExternalProcessTask newDubProcessTask(IProject project, String[] commands, IProgressMonitor monitor) {
			ProcessBuilder pb = createProcessBuilder(project, commands);
			return new RunExternalProcessTask(pb, project, monitor, listenerListHelper);
		}
		
	}
	
}