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
package mmrnmhrm.core.build;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import melnorme.utilbox.misc.ArrayUtil;
import melnorme.utilbox.misc.CollectionUtil;
import melnorme.utilbox.process.ExternalProcessNotifyingHelper;
import mmrnmhrm.core.DeeCore;
import mmrnmhrm.core.DeeCoreMessages;
import mmrnmhrm.core.DeeCorePreferences;
import mmrnmhrm.core.projectmodel.DubModelManager;
import mmrnmhrm.core.projectmodel.DubProcessManager;
import mmrnmhrm.core.projectmodel.DubProcessManager.IDubTask;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.debug.core.DebugPlugin;


public class DubProjectBuilder extends IncrementalProjectBuilder {
	
	public final static String BUILDER_ID = DeeCore.PLUGIN_ID + ".DubBuilder";
	
	@Override
	protected void startupOnInitialize() {
		assertTrue(getProject() != null);
	}
	
	@Override
	protected void clean(IProgressMonitor monitor) throws CoreException {
		IFolder dubCacheFolder = getProject().getFolder(".dub");
		if(dubCacheFolder.exists()) {
			dubCacheFolder.delete(true, monitor);
		}
	}
	
	@Override
	protected IProject[] build(int kind, Map<String, String> args, IProgressMonitor monitor) throws CoreException {
		assertTrue(kind != CLEAN_BUILD);
		
		String dubPath = DeeCorePreferences.getDubPath();
		
		ArrayList<String> commands = new ArrayList<String>();
		commands.add(dubPath);
		commands.add("build");
		
		if(kind == FULL_BUILD) {
			commands.add("--force");
		}
		
		String[] extraCommands = getExtraCommands();
		commands.addAll(CollectionUtil.createArrayList(extraCommands));
		
		try {
			ExternalProcessNotifyingHelper processHelper = submitAndAwaitDubCommand(monitor, 
				ArrayUtil.createFrom(commands, String.class));
			if(processHelper.getProcess().exitValue() != 0) {
				forgetLastBuiltState();
			}
		} catch (CoreException ce) {
			if(ce.getCause() instanceof TimeoutException && monitor.isCanceled()) {
				throw new OperationCanceledException();
			}
			DeeCore.logStatus(ce.getStatus());
			// Don't rethrow, just forget
			forgetLastBuiltState();
		}
		
		getProject().refreshLocal(IResource.DEPTH_INFINITE, monitor);
		
		return null;
	}
	
	protected String[] getExtraCommands() {
		String extraOptionsString = DeeCorePreferences.getDubBuildOptions(getProject());
		return DebugPlugin.parseArguments(extraOptionsString);
	}
	
	protected ExternalProcessNotifyingHelper submitAndAwaitDubCommand(IProgressMonitor monitor, String... commands) 
			throws CoreException {
		DubProcessManager dubProcessManager = DubModelManager.getDefault().getProcessManager();
		
		IDubTask runDubProcessOperation = dubProcessManager.newDubOperation(
			DeeCoreMessages.RunningDubBuild, getProject(), commands, monitor);
		return dubProcessManager.submitDubCommandAndWait(runDubProcessOperation);
	}
	
}