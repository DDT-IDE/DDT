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

import java.nio.file.Path;
import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import dtool.dub.DubBuildOutputParser;
import melnorme.lang.ide.core.LangCore_Actual;
import melnorme.lang.ide.core.operations.AbstractToolManager.RunProcessOperation;
import melnorme.lang.ide.core.operations.BuildTarget;
import melnorme.lang.ide.core.operations.BuildOperationCreator.CommonBuildTargetOperation;
import melnorme.lang.ide.core.operations.OperationInfo;
import melnorme.utilbox.collections.ArrayList2;
import melnorme.utilbox.concurrency.OperationCancellation;
import melnorme.utilbox.core.CommonException;
import melnorme.utilbox.misc.ArrayUtil;
import melnorme.utilbox.misc.CollectionUtil;
import melnorme.utilbox.process.ExternalProcessHelper.ExternalProcessResult;
import mmrnmhrm.core.DeeCore;
import mmrnmhrm.core.DeeCoreMessages;
import mmrnmhrm.core.DeeCorePreferences;
import mmrnmhrm.core.engine.DeeToolManager;
import mmrnmhrm.core.workspace.DubModelManager;

public class DubBuildOperation extends CommonBuildTargetOperation {
	
	protected final IProject project;
	protected final boolean fullBuild;
	
	public DubBuildOperation(OperationInfo parentOpInfo, IProject project, Path buildToolPath, 
			BuildTarget buildTarget, boolean fullBuild) {
		super(parentOpInfo, buildToolPath, buildTarget);
		this.project = project;
		this.fullBuild = fullBuild;
	}
	
	public IProject getProject() {
		return project;
	}
	
	protected String getConfiguration() {
		return null; // TODO
	}
	
	@Override
	public void execute(IProgressMonitor monitor)
			throws CoreException, CommonException, OperationCancellation {
		String validatedDubPath = getBuildToolPath().toString();
		
		ArrayList2<String> commands = new ArrayList2<String>();
		commands.add(validatedDubPath);
		commands.add("build");
		
		if(fullBuild) {
			commands.add("--force");
		}
		
		if(getConfiguration() != null) {
			commands.addElements("-c" , getConfiguration());
		}
		
		if(getBuildTargetName() != null) {
			commands.addElements("-b" , getBuildTargetName());
		}
		
		String[] extraCommands = DeeCorePreferences.DUB_BUILD_OPTIONS.getParsedArguments(project);
		commands.addAll(CollectionUtil.createArrayList(extraCommands));
		
		ExternalProcessResult processResult = submitAndAwaitDubCommand(monitor, commands);
		processBuildOutput(processResult);
	}
	
	protected ExternalProcessResult submitAndAwaitDubCommand(IProgressMonitor monitor, List<String> commandList) 
			throws CoreException, OperationCancellation {
		DeeToolManager dubProcessManager = DeeCore.getDubProcessManager();
		
		String[] commands = ArrayUtil.createFrom(commandList, String.class);
		RunProcessOperation runDubProcessOperation = dubProcessManager.newRunProcessOperation(
			getProject(), DeeCoreMessages.RunningDubBuild, commands, monitor);
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