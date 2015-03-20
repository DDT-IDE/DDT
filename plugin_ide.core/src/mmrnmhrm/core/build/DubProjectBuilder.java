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

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Map;

import melnorme.lang.ide.core.LangCore;
import melnorme.lang.ide.core.LangCore_Actual;
import melnorme.lang.ide.core.operations.LangProjectBuilder;
import melnorme.lang.ide.core.operations.SDKLocationValidator;
import melnorme.lang.ide.core.utils.process.IRunProcessTask;
import melnorme.lang.tooling.data.LocationValidator;
import melnorme.lang.tooling.data.StatusException;
import melnorme.lang.tooling.data.StatusLevel;
import melnorme.lang.tooling.data.ValidationMessages;
import melnorme.lang.utils.SearchPathForExecutable;
import melnorme.utilbox.concurrency.OperationCancellation;
import melnorme.utilbox.core.CommonException;
import melnorme.utilbox.misc.ArrayUtil;
import melnorme.utilbox.misc.CollectionUtil;
import melnorme.utilbox.misc.Location;
import melnorme.utilbox.process.ExternalProcessHelper.ExternalProcessResult;
import mmrnmhrm.core.DeeCore;
import mmrnmhrm.core.DeeCoreMessages;
import mmrnmhrm.core.DeeCorePreferences;
import mmrnmhrm.core.engine_client.DubProcessManager;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import dtool.dub.DubBuildOutputParser;


public class DubProjectBuilder extends LangProjectBuilder {
	
	public static class DubLocationValidator extends SDKLocationValidator {
		
		public DubLocationValidator() {
			super(DeeCoreMessages.DUB_PATH_Label);
			directoryOnly = false;
			fileOnly = true;
		}
		
		@Override
		protected Location validatePath(Path path) throws ValidationException {
			if(!path.isAbsolute() && path.getNameCount() == 1) {
				String pathEnvExe = path.toString();
				
				try {
					new SearchPathForExecutable(pathEnvExe).checkIsFound();
				} catch (CommonException e) {
					throw createException(StatusLevel.WARNING, e.getMessage());
				}
				return null; // special case allowed
			}
			
			return super.validatePath(path);
		}
		
		@Override
		protected ValidationException error_NotAbsolute(Path path) throws ValidationException {
			return createException(StatusLevel.ERROR, ValidationMessages.Location_NotAbsoluteNorSingle(path));
		}
		
		@Override
		protected Location getSDKExecutableLocation(Location location) {
			return location;
		}
		
		@Override
		protected String getSDKExecutable_append() {
			return ""; 
		}
		
	}
	
	public static final String DUB_BUILD_PROBLEM_ID = DeeCore.PLUGIN_ID + ".DubBuildProblem";
	
	@Override
	protected LocationValidator getSDKLocationValidator() {
		return new DubLocationValidator(); // Not actually used at the moment.
	}
	
	@Override
	protected void clean(IProgressMonitor monitor) throws CoreException {
		IFolder dubCacheFolder = getProject().getFolder(".dub");
		if(dubCacheFolder.exists()) {
			dubCacheFolder.delete(true, monitor);
		}
		deleteProjectBuildMarkers();
	}
	
	@Override
	protected String getBuildProblemId() {
		return DUB_BUILD_PROBLEM_ID;
	}
	
	@Override
	protected String getSDKToolPath() throws CoreException {
		String pathString = DeeCorePreferences.getEffectiveDubPath();
		try {
			getSDKLocationValidator().getValidatedField(pathString);
			return pathString;
		} catch (StatusException se) {
			throw LangCore.createCoreException(se);
		}
	}
	
	@Override
	protected IProject[] doBuild(IProject project, int kind, Map<String, String> args, IProgressMonitor monitor) 
			throws CoreException, OperationCancellation {
		
		String validatedDubPath = getSDKToolPath();
		
		ArrayList<String> commands = new ArrayList<String>();
		commands.add(validatedDubPath);
		commands.add("build");
		
		if(kind == FULL_BUILD) {
			commands.add("--force");
		}
		
		String[] extraCommands = DeeCorePreferences.DUB_BUILD_OPTIONS.getParsedArguments(project);
		commands.addAll(CollectionUtil.createArrayList(extraCommands));
		
		ExternalProcessResult processResult;
		try {
			processResult = submitAndAwaitDubCommand(monitor, ArrayUtil.createFrom(commands, String.class));
		} catch (CoreException ce) {
			if(!monitor.isCanceled()) {
				DeeCore.logStatus(ce.getStatus());
			}
			
			forgetLastBuiltState();
			throw ce; // Note: if monitor is cancelled, exception will be ignored.
		} finally {
			project.refreshLocal(IResource.DEPTH_INFINITE, monitor);
		}
		
		processBuildOutput(processResult);
		
		return null;
	}
	
	protected ExternalProcessResult submitAndAwaitDubCommand(IProgressMonitor monitor, String... commands) 
			throws CoreException, OperationCancellation {
		DubProcessManager dubProcessManager = DeeCore.getDubProcessManager();
		
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
		
		IMarker dubMarker = getProject().createMarker(getBuildProblemId());
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