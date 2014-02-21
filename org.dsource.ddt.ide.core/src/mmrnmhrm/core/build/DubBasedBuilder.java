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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import melnorme.utilbox.misc.ArrayUtil;
import melnorme.utilbox.misc.CollectionUtil;
import mmrnmhrm.core.DeeCore;
import mmrnmhrm.core.DeeCorePreferences;
import mmrnmhrm.core.projectmodel.DubModelManager;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;


public class DubBasedBuilder extends IncrementalProjectBuilder {
	
	public final static String BUILDER_ID = DeeCore.PLUGIN_ID + ".DubBuilder";
	
	@Override
	protected void startupOnInitialize() {
		assertTrue(getProject() != null);
	}
	
	protected void submitAndAwaitDubCommand(IProgressMonitor monitor, String... commands) throws CoreException {
		Future<Void> future = DubModelManager.getDefault().submitDubCommand(getProject(), monitor, commands);
		
		try {
			future.get();
		} catch (InterruptedException e) {
			future.cancel(true);
		} catch (ExecutionException e) {
			Throwable cause = e.getCause();
			if(cause instanceof CoreException) {
				throw (CoreException) cause;
			}
			// this shouldn't happen
			throw new CoreException(DeeCore.createErrorStatus("Internal error running dub build", cause));
		}
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
		String dubPath = DeeCorePreferences.getDubPath();
		
		ArrayList<String> commands = new ArrayList<String>();
		commands.add(dubPath);
		commands.add("build");
		
		if(kind == FULL_BUILD) {
			commands.add("--force");
		}
		String[] extraCommands = getExtraCommands();
		commands.addAll(CollectionUtil.createArrayList(extraCommands));
		
		submitAndAwaitDubCommand(monitor, ArrayUtil.createFrom(commands, String.class));
		
		return null;
	}
	
	protected String[] getExtraCommands() {
		String extraOptionsStr = DeeCorePreferences.getDubBuildOptions(getProject());
		extraOptionsStr = extraOptionsStr.trim();
		String[] extraCommands = extraOptionsStr.split(" *");
		return extraCommands;
	}
	
}