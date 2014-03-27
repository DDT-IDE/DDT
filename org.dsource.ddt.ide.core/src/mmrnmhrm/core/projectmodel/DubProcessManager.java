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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.nio.file.Path;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import melnorme.lang.ide.core.utils.process.IExternalProcessListener;
import melnorme.lang.ide.core.utils.process.RunExternalProcessTask;
import melnorme.utilbox.concurrency.ITaskAgent;
import melnorme.utilbox.core.ExceptionAdapter;
import melnorme.utilbox.core.fntypes.ICallable;
import melnorme.utilbox.misc.ListenerListHelper;
import melnorme.utilbox.process.ExternalProcessNotifyingHelper;
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
	
	protected final ListenerListHelper<IExternalProcessListener> processListenersHelper = new ListenerListHelper<>();
	
	public void addDubProcessListener(IExternalProcessListener dubProcessListener) {
		processListenersHelper.addListener(dubProcessListener);
	}
	
	public void removeDubProcessListener(IExternalProcessListener dubProcessListener) {
		processListenersHelper.removeListener(dubProcessListener);
	}
	
	/** Marker interface for listener callbacks that runs in the Dub agent. 
	 * Used for documentation purposes only, has no effect in code. */
	@Target(ElementType.METHOD)
	@Retention(RetentionPolicy.SOURCE)
	public static @interface RunsInDubProcessAgent { }
	
	/* ----------------------------------- */
	
	public Future<ExternalProcessNotifyingHelper> submitDubCommand(RunExternalProcessTask task) {
		return dubProcessAgent.submit(task);
	}
	
	public ExternalProcessNotifyingHelper submitDubCommandAndWait(IProject project, IProgressMonitor monitor, 
			String... commands) throws InterruptedException, CoreException {
		return submitDubCommandAndWait(monitor, project, commands);
	}
	
	public ExternalProcessNotifyingHelper submitDubCommandAndWait(IProgressMonitor monitor, IProject project,
			String... commands) throws InterruptedException, CoreException {
		return submitDubCommandAndWait(newExternalProcessTask(monitor, project, commands));
	}
	
	public <T> T submitDubCommandAndWait(ICallable<T, CoreException> task) 
			throws InterruptedException, CoreException {
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
	
	public RunExternalProcessTask newExternalProcessTask(IProgressMonitor monitor, IProject project,
			String... commands) {
		Path workingDir = project != null ?
				project.getLocation().toFile().toPath() :
				DeeCore.getWorkspaceRoot().getLocation().toFile().toPath();
		ProcessBuilder pb = new ProcessBuilder(commands).directory(workingDir.toFile());
		return new RunExternalProcessTask(pb, project, monitor, processListenersHelper);
	}
	
}