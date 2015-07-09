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

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import dtool.dub.DubBuildOutputParser;
import melnorme.lang.ide.core.LangCore_Actual;
import melnorme.lang.ide.core.operations.OperationInfo;
import melnorme.lang.ide.core.operations.build.BuildTarget;
import melnorme.lang.ide.core.operations.build.CommonBuildTargetOperation;
import melnorme.utilbox.collections.ArrayList2;
import melnorme.utilbox.concurrency.OperationCancellation;
import melnorme.utilbox.core.CommonException;
import melnorme.utilbox.misc.CollectionUtil;
import melnorme.utilbox.misc.StringUtil;
import melnorme.utilbox.process.ExternalProcessHelper.ExternalProcessResult;
import mmrnmhrm.core.DeeCore;
import mmrnmhrm.core.DeeCoreMessages;
import mmrnmhrm.core.DeeCorePreferences;
import mmrnmhrm.core.dub_model.DeeBundleModelManager;

public class DubBuildTargetOperation extends CommonBuildTargetOperation {
	
	protected final String configuration;
	protected final String buildType;
	
	public DubBuildTargetOperation(OperationInfo parentOpInfo, IProject project, Path buildToolPath, 
			BuildTarget buildTarget, boolean fullBuild) {
		super(parentOpInfo, project, buildToolPath, buildTarget, fullBuild);
		
		String targetName = buildTarget.getTargetName();
		configuration = StringUtil.emptyAsNull(StringUtil.substringUntilMatch(targetName, ":"));
		buildType = StringUtil.segmentAfterMatch(targetName, ":");
	}
	
	protected String getConfiguration() {
		return configuration;
	}
	
	public String getBuildType() {
		return buildType;
	}
	
	@Override
	public void execute(IProgressMonitor pm) throws CoreException, CommonException, OperationCancellation {
		String validatedDubPath = getBuildToolPath2().toString();
		
		ArrayList2<String> commands = new ArrayList2<String>();
		commands.add(validatedDubPath);
		commands.add("build");
		
		if(fullBuild) {
			commands.add("--force");
		}
		
		if(getConfiguration() != null) {
			commands.addElements("-c" , getConfiguration());
		}
		
		if(getBuildType() != null) {
			commands.addElements("-b" , getBuildType());
		}
		
		String[] extraCommands = DeeCorePreferences.DUB_BUILD_OPTIONS.getParsedArguments(project);
		commands.addAll(CollectionUtil.createArrayList(extraCommands));
		
		ExternalProcessResult processResult = getToolManager().newRunProcessOperation(getProject(), 
			DeeCoreMessages.RunningDubBuild, commands.toArray(String.class) , pm).runProcess();
		
		processBuildOutput(processResult);
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
		
		IMarker dubMarker = getProject().createMarker(DeeBundleModelManager.DUB_PROBLEM_ID);
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