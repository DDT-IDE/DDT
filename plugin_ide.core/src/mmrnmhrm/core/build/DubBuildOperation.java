/*******************************************************************************
 * Copyright (c) 2015, 2015 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package mmrnmhrm.core.build;

import java.util.List;
import java.util.Map;

import melnorme.lang.ide.core.LangCore_Actual;
import melnorme.lang.ide.core.operations.CommonBuildOperation;
import melnorme.lang.ide.core.operations.IBuildTargetOperation;
import melnorme.lang.ide.core.operations.LangProjectBuilderExt;
import melnorme.lang.ide.core.utils.process.IRunProcessTask;
import melnorme.utilbox.collections.ArrayList2;
import melnorme.utilbox.concurrency.OperationCancellation;
import melnorme.utilbox.core.CommonException;
import melnorme.utilbox.misc.ArrayUtil;
import melnorme.utilbox.misc.CollectionUtil;
import melnorme.utilbox.process.ExternalProcessHelper.ExternalProcessResult;
import mmrnmhrm.core.DeeCore;
import mmrnmhrm.core.DeeCoreMessages;
import mmrnmhrm.core.DeeCorePreferences;
import mmrnmhrm.core.engine.DubProcessManager;
import mmrnmhrm.core.workspace.DubModelManager;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import dtool.dub.DubBuildOutputParser;

public class DubBuildOperation extends CommonBuildOperation implements IBuildTargetOperation {
	
	protected final String configuration;
	protected final DubBuildType buildType;
	
	public DubBuildOperation(IProject project, LangProjectBuilderExt langProjectBuilder, 
			String configuration, DubBuildType buildType) {
		super(project, langProjectBuilder);
		this.configuration = configuration;
		this.buildType = buildType;
	}
	
	@Override
	public IProject[] execute(IProject project, int kind, Map<String, String> args, IProgressMonitor monitor)
			throws CoreException, CommonException, OperationCancellation {
		String validatedDubPath = getBuildToolPath().toString();
		
		ArrayList2<String> commands = new ArrayList2<String>();
		commands.add(validatedDubPath);
		commands.add("build");
		
		if(kind == IncrementalProjectBuilder.FULL_BUILD) {
			commands.add("--force");
		}
		
		if(configuration != null) {
			commands.addElements("-c" , configuration);
		}
		
		if(buildType != null) {
			commands.addElements("-b" , buildType.getBuildTypeString());
		}
		
		String[] extraCommands = DeeCorePreferences.DUB_BUILD_OPTIONS.getParsedArguments(project);
		commands.addAll(CollectionUtil.createArrayList(extraCommands));
		
		ExternalProcessResult processResult = submitAndAwaitDubCommand(monitor, commands);
		processBuildOutput(processResult);
		
		return null;
	}
	
	protected ExternalProcessResult submitAndAwaitDubCommand(IProgressMonitor monitor, List<String> commandList) 
			throws CoreException, OperationCancellation {
		DubProcessManager dubProcessManager = DeeCore.getDubProcessManager();
		
		String[] commands = ArrayUtil.createFrom(commandList, String.class);
		IRunProcessTask runDubProcessOperation = dubProcessManager.newDubOperation(
			DeeCoreMessages.RunningDubBuild, getProject(), commands, monitor);
		return dubProcessManager.submitDubCommandAndWait(runDubProcessOperation);
	}
	
	protected void processBuildOutput(ExternalProcessResult processResult) throws CoreException {
		new DubBuildOutputParser<CoreException>() {
			@Override
			protected void processDubFailure(String dubErrorLine) throws CoreException {
				addDubFailureMarker(dubErrorLine);
			};
			
			@Override
			protected void processCompilerError(String file, String lineStr, String errorMsg) {
				try {
					addCompilerErrorMarker(file, lineStr, errorMsg);
				} catch (CoreException e) {
					// log, but otherwise ignore & continue
					DeeCore.logStatus(e);
				}
			}
		}.handleResult(processResult);
	}
	
	public void addDubFailureMarker(String dubErrorLine) throws CoreException {
		String errorMessage = 
				dubErrorLine == null ? DeeCoreMessages.RunningDubBuild_Error : dubErrorLine;
		
		IMarker dubMarker = getProject().createMarker(DubModelManager.DUB_PROBLEM_ID);
		dubMarker.setAttribute(IMarker.MESSAGE, errorMessage);
		dubMarker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
	}
	
	protected void addCompilerErrorMarker(String file, String lineStr, String errorMsg) throws CoreException {
		IResource resource = getProject().findMember(file);
		if(resource == null || !resource.exists()) {
			return;
		}
		
		IMarker dubMarker = resource.createMarker(LangCore_Actual.BUILD_PROBLEM_ID);
		
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