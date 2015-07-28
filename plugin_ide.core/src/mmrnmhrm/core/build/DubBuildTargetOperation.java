/*******************************************************************************
 * Copyright (c) 2015 Bruno Medeiros and other Contributors.
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
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import dtool.dub.DubBuildOutputParser;
import melnorme.lang.ide.core.LangCore_Actual;
import melnorme.lang.ide.core.operations.OperationInfo;
import melnorme.lang.ide.core.operations.build.BuildTargetValidator;
import melnorme.lang.ide.core.operations.build.CommonBuildTargetOperation;
import melnorme.lang.ide.core.utils.ResourceUtils;
import melnorme.utilbox.collections.ArrayList2;
import melnorme.utilbox.concurrency.OperationCancellation;
import melnorme.utilbox.core.CommonException;
import melnorme.utilbox.misc.Location;
import melnorme.utilbox.process.ExternalProcessHelper.ExternalProcessResult;
import mmrnmhrm.core.DeeCore;
import mmrnmhrm.core.DeeCoreMessages;
import mmrnmhrm.core.dub_model.DeeBundleModelManager;

public class DubBuildTargetOperation extends CommonBuildTargetOperation {
	
	public DubBuildTargetOperation(BuildTargetValidator buildTargetValidator, 
			OperationInfo opInfo, Path buildToolPath, boolean fullBuild
	) throws CommonException, CoreException {
		super(buildTargetValidator.getBuildManager(), buildTargetValidator, opInfo, buildToolPath, fullBuild);
	}
	
	@Override
	protected void addToolCommand(ArrayList2<String> commands)
			throws CoreException, CommonException, OperationCancellation {
		//super.addToolCommand(commands);
	}
	
	@Override
	protected void addMainArguments(ArrayList2<String> commands) {
		commands.add("build");
		
		if(fullBuild) {
			commands.add("--force");
		}
		
		if(!getConfigurationName().isEmpty()) {
			commands.addElements("-c" , getConfigurationName());
		}
		
		String buildTypeName = getBuildType().getName();
		if(!buildTypeName.equals(DeeBuildManager.BuildType_Default)) {
			commands.addElements("-b" , buildTypeName);
		}
	}
	
	@Override
	protected ProcessBuilder getProcessBuilder(ArrayList2<String> commands)
			throws CommonException, OperationCancellation, CoreException {
		Location projectLocation = ResourceUtils.getProjectLocation(getProject());
		return getToolManager().createToolProcessBuilder(getBuildToolPath(), projectLocation, 
			commands.toArray(String.class));
	}
	
	@Override
	protected ExternalProcessResult runBuildTool(OperationInfo opInfo, ProcessBuilder pb, IProgressMonitor pm)
			throws CommonException, OperationCancellation {
		return getToolManager().newRunToolTask(opInfo, pb, pm).runProcess();
	}
	
	@Override
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