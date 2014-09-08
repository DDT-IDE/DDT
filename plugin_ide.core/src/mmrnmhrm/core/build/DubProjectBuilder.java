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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import melnorme.lang.ide.core.utils.process.IRunProcessTask;
import melnorme.utilbox.misc.ArrayUtil;
import melnorme.utilbox.misc.CollectionUtil;
import melnorme.utilbox.misc.StringUtil;
import melnorme.utilbox.process.ExternalProcessHelper.ExternalProcessResult;
import mmrnmhrm.core.DeeCore;
import mmrnmhrm.core.DeeCoreMessages;
import mmrnmhrm.core.DeeCorePreferences;
import mmrnmhrm.core.engine_client.DubProcessManager;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;


public class DubProjectBuilder extends IncrementalProjectBuilder {
	
	public static final String BUILDER_ID = DeeCore.PLUGIN_ID + ".DubBuilder";
	
	public static final String DUB_BUILD_PROBLEM_ID = DeeCore.PLUGIN_ID + ".DubBuildProblem";
	public static final String DEE_PROBLEM_ID = DUB_BUILD_PROBLEM_ID + "_DeeSourceProblem";
	
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
		deleteDubMarkers();
	}
	
	protected void deleteDubMarkers() {
		try {
			IMarker[] markers = getProject().findMarkers(DUB_BUILD_PROBLEM_ID, true, IResource.DEPTH_INFINITE);
			for (IMarker marker : markers) {
				marker.delete();
			}
		} catch (CoreException ce) {
			DeeCore.logError(ce);
		}
	}
	
	@Override
	protected IProject[] build(int kind, Map<String, String> args, IProgressMonitor monitor) throws CoreException {
		assertTrue(kind != CLEAN_BUILD);
		
		deleteDubMarkers();
		
		String dubPath = DeeCorePreferences.getEffectiveDubPath();
		
		ArrayList<String> commands = new ArrayList<String>();
		commands.add(dubPath);
		commands.add("build");
		
		if(kind == FULL_BUILD) {
			commands.add("--force");
		}
		
		String[] extraCommands = DeeCorePreferences.DUB_BUILD_OPTIONS.getParsedArguments(getProject());
		commands.addAll(CollectionUtil.createArrayList(extraCommands));
		
		ExternalProcessResult processResult;
		try {
			processResult = submitAndAwaitDubCommand(monitor, ArrayUtil.createFrom(commands, String.class));
			
		} catch (CoreException ce) {
			if(ce.getCause() instanceof TimeoutException && monitor.isCanceled()) {
				throw new OperationCanceledException();
			}
			DeeCore.logStatus(ce.getStatus());
			// Don't rethrow, just forget build state
			forgetLastBuiltState();
			return null;
		} finally {
			getProject().refreshLocal(IResource.DEPTH_INFINITE, monitor);
		}
		
		processBuildOutput(processResult);
		
		return null;
	}
	
	protected ExternalProcessResult submitAndAwaitDubCommand(IProgressMonitor monitor, String... commands) 
			throws CoreException {
		DubProcessManager dubProcessManager = DeeCore.getDubProcessManager();
		
		IRunProcessTask runDubProcessOperation = dubProcessManager.newDubOperation(
			DeeCoreMessages.RunningDubBuild, getProject(), commands, monitor);
		return dubProcessManager.submitDubCommandAndWait(runDubProcessOperation);
	}
	
	protected void processBuildOutput(ExternalProcessResult processResult) throws CoreException {
		int buildExitValue = processResult.exitValue;
		
		String stderr = processResult.stderr.toString(StringUtil.UTF8);
		
		if(buildExitValue != 0) {
			String dubErrorLine = getDubError(stderr);
			if(dubErrorLine == null) {
				dubErrorLine = DeeCoreMessages.RunningDubBuild_Error;
			}
			
			IMarker dubMarker = getProject().createMarker(DUB_BUILD_PROBLEM_ID);
			dubMarker.setAttribute(IMarker.MESSAGE, dubErrorLine);
			dubMarker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
		}
		
		processCompilerErrors(stderr);
	}
	
	protected String getDubError(String stderr) {
		Matcher matcher = Pattern.compile("^(Error executing command.*)$", Pattern.MULTILINE).
				matcher(stderr);
		if(matcher.find()) {
			return matcher.group(1);
		}
		return null;
	}
	
	protected static final String ERROR_REGEX = "^([^():\\n]*)"+"\\(([^:)\\n]*)\\):"+"\\sError:\\s(.*)$";
	protected static final Pattern ERROR_MATCHER = Pattern.compile(ERROR_REGEX, Pattern.MULTILINE);
	
	protected void processCompilerErrors(String stderr) {
		Matcher matcher = ERROR_MATCHER.matcher(stderr);
		while(matcher.find()) {
			String file = matcher.group(1);
			String lineStr = matcher.group(2);
			String errorMsg = matcher.group(3);
			try {
				processErrorLine(file, lineStr, errorMsg);
			} catch (CoreException e) {
				DeeCore.logError(e);
				// ignore, continue
			}
		}
	}
	
	protected void processErrorLine(String file, String lineStr, String errorMsg) throws CoreException {
		IResource resource = getProject().findMember(file);
		if(resource == null || !resource.exists()) {
			return;
		}
		
		IMarker dubMarker = resource.createMarker(DEE_PROBLEM_ID);
		
		dubMarker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
		dubMarker.setAttribute(IMarker.MESSAGE, errorMsg);
		
		try {
			int line = Integer.valueOf(lineStr);
			dubMarker.setAttribute(IMarker.LINE_NUMBER, line);
		} catch (NumberFormatException e) {
			// don't set line attribute
			return;
		}
	}
	
}