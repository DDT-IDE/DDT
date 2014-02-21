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

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.Callable;
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
public class DubManager {
	
	protected final IExecutorAgent executorAgent = new CoreExecutorAgent(DubModelManager.class.getSimpleName());
	
	/* ----------------------------------- */
	
	protected final ListenerListHelper<IDubProcessListener> dubProcessListenersHelper = new ListenerListHelper<>();
	
	public void addDubProcessListener(IDubProcessListener dubProcessListener) {
		dubProcessListenersHelper.addListener(dubProcessListener);
	}
	
	public void removeDubProcessListener(IDubProcessListener dubProcessListener) {
		dubProcessListenersHelper.removeListener(dubProcessListener);
	}
	
	public Future<Void> submitDubCommand(final IProject project, final IProgressMonitor monitor, 
		final String... commands) {
		final Path location = project.getLocation().toFile().toPath();
		Future<Void> dubBuildFuture = executorAgent.submit(new Callable<Void>() {
			@Override
			public Void call() throws CoreException {
				ProcessBuilder pb = new ProcessBuilder(commands).directory(location.toFile());
				runDubProcess(pb, project, monitor);
				return null;
			}
		});
		
		return dubBuildFuture;
	}
	
	protected ExternalProcessOutputHelper runDubProcess(ProcessBuilder pb, IProject project, 
			IProgressMonitor cancelMonitor) 
			throws CoreException {
		ExternalProcessOutputHelper processHelper;
		try {
			processHelper = new DubExternalProcessHelper(pb.start(), cancelMonitor, false);
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
	
	protected IProgressMonitor monitor;
	
	public DubExternalProcessHelper(Process process, IProgressMonitor monitor, boolean startReaders) {
		super(process, true, startReaders);
		this.monitor = monitor;
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