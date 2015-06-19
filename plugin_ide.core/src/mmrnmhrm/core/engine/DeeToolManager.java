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
package mmrnmhrm.core.engine;

import java.nio.file.Path;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import melnorme.lang.ide.core.ILangOperationsListener;
import melnorme.lang.ide.core.LangCore;
import melnorme.lang.ide.core.operations.AbstractToolsManager;
import melnorme.lang.ide.core.operations.IToolOperation;
import melnorme.lang.ide.core.operations.OperationInfo;
import melnorme.lang.ide.core.operations.ProcessStartInfo;
import melnorme.lang.ide.core.utils.CoreTaskAgent;
import melnorme.lang.ide.core.utils.EclipseUtils;
import melnorme.lang.ide.core.utils.process.AbstractRunProcessTask;
import melnorme.lang.ide.core.utils.process.EclipseCancelMonitor;
import melnorme.lang.ide.core.utils.process.IRunProcessTask;
import melnorme.utilbox.concurrency.ICancelMonitor;
import melnorme.utilbox.concurrency.ITaskAgent;
import melnorme.utilbox.concurrency.OperationCancellation;
import melnorme.utilbox.core.CommonException;
import melnorme.utilbox.core.ExceptionAdapter;
import melnorme.utilbox.process.ExternalProcessHelper.ExternalProcessResult;
import melnorme.utilbox.process.ExternalProcessNotifyingHelper;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Manages launching D tools.
 * Has an executor agent to run external DUB commands.
 */
public class DeeToolManager extends AbstractToolsManager {
	
	protected final ITaskAgent dubProcessAgent = new CoreTaskAgent(getClass().getSimpleName());
	
	public DeeToolManager() {
	}
	
	public void shutdownNow() {
		dubProcessAgent.shutdownNow();
	}
	
	/* ----------------------------------- */
	
	public Future<?> submitDubCommand(IRunProcessTask task) {
		return dubProcessAgent.submit(task);
	}
	
	public ExternalProcessResult submitDubCommandAndWait(IRunProcessTask task) 
			throws CoreException, OperationCancellation  {
		return submitAndGetTask(task);
	}
	
	public ExternalProcessResult submitAndGetTask(IRunProcessTask task) 
			throws CoreException, OperationCancellation {
		Future<ExternalProcessResult> future = dubProcessAgent.submit(task);
		try {
			return future.get();
		} catch (InterruptedException e) {
			future.cancel(true);
			LangCore.logError("Unexpected interruption", e);
			throw new OperationCancellation();
		} catch (ExecutionException e) {
			Throwable cause = e.getCause();
			if(cause instanceof OperationCancellation) {
				throw (OperationCancellation) cause;
			}
			if(cause instanceof CoreException) {
				throw (CoreException) cause;
			}
			throw ExceptionAdapter.unchecked(cause); // Should not happen
		}
	}
	
	public RunDubProcessOperation newDubOperation(String operationName, IProject project, 
			String[] commands, IProgressMonitor monitor) {
		ProcessBuilder pb = createProcessBuilder(project, commands);
		return new RunDubProcessOperation(operationName, pb, project, new EclipseCancelMonitor(monitor));
	}
	
	public static ProcessBuilder createProcessBuilder(IProject project, String... commands) {
		Path workingDir = project != null ?
			project.getLocation().toFile().toPath() :
			EclipseUtils.getWorkspaceRoot().getLocation().toFile().toPath();
		return new ProcessBuilder(commands).directory(workingDir.toFile());
	}
	
	/* FIXME: review following code: */
	
	public class RunDubProcessOperation extends AbstractRunProcessTask implements IToolOperation {
		
		protected final String operationName;
		protected final IProject project;
		protected final OperationInfo opInfo = new OperationInfo();
		
		protected RunDubProcessOperation(String operationName, ProcessBuilder pb, IProject project,
				ICancelMonitor cancelMonitor) {
			super(pb, cancelMonitor);
			
			this.operationName = operationName;
			this.project = project;
		}
		
		@Override
		protected ExternalProcessNotifyingHelper startProcess(ICancelMonitor cm) throws CommonException {
			notifyOperationStarted(this, opInfo);
			return super.startProcess(cm);
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
			for(ILangOperationsListener processListener : getListeners()) {
				processListener.handleProcessStart(
					new ProcessStartInfo(pb, project, "> ", false, processHelper, ce), opInfo);
			}
		}
		
	}
	
	public class DubCompositeOperation implements IToolOperation {
		
		protected final String operationName;
		protected final IProject project;
		public final OperationInfo opInfo = new OperationInfo();
		
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
		
	}
	
	public AbstractRunProcessTask newDubProcessTask(IProject project, String[] commands, IProgressMonitor pm
			, OperationInfo opInfo) {
		ProcessBuilder pb = createProcessBuilder(project, commands);
		return new AbstractRunProcessTask(pb, new EclipseCancelMonitor(pm)) {
			
			@Override
			protected void handleProcessStartResult(ExternalProcessNotifyingHelper processHelper, CommonException ce) {
				for(ILangOperationsListener processListener : getListeners()) {
					processListener.handleProcessStart(newStartProcessInfo(processHelper, ce), opInfo);
				}
			}
			
			protected ProcessStartInfo newStartProcessInfo(ExternalProcessNotifyingHelper processHelper,
					CommonException ce) {
				return new ProcessStartInfo(pb, project, "> ", false, processHelper, ce);
			}
		};
	}
	
}