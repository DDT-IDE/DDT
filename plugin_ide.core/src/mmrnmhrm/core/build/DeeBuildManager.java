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
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;

import dtool.dub.DubBuildOutputParser;
import melnorme.lang.ide.core.LangCore_Actual;
import melnorme.lang.ide.core.operations.OperationInfo;
import melnorme.lang.ide.core.operations.ToolMarkersUtil;
import melnorme.lang.ide.core.operations.build.BuildManager;
import melnorme.lang.ide.core.operations.build.BuildOperationCreator;
import melnorme.lang.ide.core.operations.build.BuildTarget;
import melnorme.lang.ide.core.operations.build.BuildTargetValidator;
import melnorme.lang.ide.core.operations.build.CommonBuildTargetOperation;
import melnorme.lang.ide.core.operations.build.IToolOperation;
import melnorme.lang.ide.core.project_model.AbstractBundleInfo;
import melnorme.lang.ide.core.utils.ResourceUtils;
import melnorme.lang.tooling.data.StatusLevel;
import melnorme.lang.tooling.ops.SourceLineColumnRange;
import melnorme.lang.tooling.ops.ToolSourceMessage;
import melnorme.utilbox.collections.ArrayList2;
import melnorme.utilbox.collections.Collection2;
import melnorme.utilbox.collections.Indexable;
import melnorme.utilbox.concurrency.OperationCancellation;
import melnorme.utilbox.core.CommonException;
import melnorme.utilbox.misc.Location;
import melnorme.utilbox.misc.PathUtil;
import melnorme.utilbox.misc.StringUtil;
import melnorme.utilbox.process.ExternalProcessHelper.ExternalProcessResult;
import mmrnmhrm.core.DeeCore;
import mmrnmhrm.core.DeeCoreMessages;
import mmrnmhrm.core.dub_model.DeeBundleModelManager;
import mmrnmhrm.core.dub_model.DeeBundleModelManager.DeeBundleModel;
import mmrnmhrm.core.dub_model.DubBundleInfo;

public class DeeBuildManager extends BuildManager {
	
	public static final String BuildType_Default = "";
	
	public DeeBuildManager(DeeBundleModel bundleModel) {
		super(bundleModel);
	}
	
	@Override
	protected void bundleProjectAdded(IProject project, AbstractBundleInfo bundleInfo) {
		DubBundleInfo dubBundleInfo = (DubBundleInfo) bundleInfo;
		if(dubBundleInfo.getBundleDesc().isResolved()) {
			// We ignore resolved description, because only unresolved ones have configuration info
			return;
		}
		super.bundleProjectAdded(project, dubBundleInfo);
	}
	
	@Override
	protected Indexable<BuildType> getBuildTypes_do() {
		return ArrayList2.<BuildType>create(
			new DeeBuildType(BuildType_Default),
			new DeeBuildType(DubBuildType.UNITTEST.getBuildTypeString())
		);
	}
	
	@Override
	protected BuildOperationCreator createBuildOperationCreator(OperationInfo opInfo, IProject project,
			boolean fullBuild) {
		return new DeeBuildOperationCreator(project, opInfo, fullBuild);
	}
	
	public static class DeeBuildOperationCreator extends BuildOperationCreator {
		
		public DeeBuildOperationCreator(IProject project, OperationInfo parentOpInfo, boolean fullBuild) {
			super(project, parentOpInfo, fullBuild);
		}
		
		@Override
		public IToolOperation newProjectBuildOperation(Collection2<BuildTarget> targetsToBuild) 
				throws CommonException {
			IToolOperation projectBuildOp = super.newProjectBuildOperation(targetsToBuild);
			
			return (pm) -> {
				DeeCore.getDubProcessManager().submitTaskAndAwaitResult(() -> {
					projectBuildOp.execute(pm);
					return null;
				});
			};
		}
		
	}
	
	@Override
	public BuildTargetValidator createBuildTargetValidator2(IProject project, String buildConfigName,
			String buildTypeName, String buildArguments) throws CommonException {
		return new BuildTargetValidator(project, buildConfigName, buildTypeName, buildArguments);
	}
	
	/* -----------------  ----------------- */
	
	protected class DeeBuildType extends BuildType {
		
		public DeeBuildType(String name) {
			super(name);
		}
		
		@Override
		public String getDefaultBuildOptions(BuildTargetValidator buildTargetValidator) throws CommonException {
			
			String buildConfigName = buildTargetValidator.getBuildConfigName();
			String buildTypeName = buildTargetValidator.getBuildTypeName();
			
			ArrayList2<String> commands = new ArrayList2<>();
			commands.add("build");
			
			if(!buildConfigName.equals(DubBundleInfo.DEFAULT_CONFIGURATION)) {
				commands.addElements("-c" , buildConfigName);
			}
			
			if(!buildTypeName.equals(DeeBuildManager.BuildType_Default)) {
				commands.addElements("-b" , buildTypeName);
			}
			return DebugPlugin.renderArguments(commands.toArray(String.class), null);
		}
		
		@Override
		public CommonBuildTargetOperation getBuildOperation(BuildTargetValidator buildTargetValidator,
				OperationInfo opInfo, Path buildToolPath, boolean fullBuild) throws CommonException, CoreException {
			return new DubBuildTargetOperation(buildTargetValidator, opInfo, buildToolPath, fullBuild);
		}
		
	}
	
	public class DubBuildTargetOperation extends CommonBuildTargetOperation {
		
		public DubBuildTargetOperation(BuildTargetValidator buildTargetValidator, 
				OperationInfo opInfo, Path buildToolPath, boolean fullBuild
		) throws CommonException, CoreException {
			super(buildTargetValidator.getBuildManager(), buildTargetValidator, opInfo, buildToolPath, fullBuild);
		}
		
		@Override
		protected void addToolCommand(ArrayList2<String> commands)
				throws CoreException, CommonException, OperationCancellation {
//			super.addToolCommand(commands);
		}
		
		@Override
		protected String[] getMainArguments() throws CoreException, CommonException, OperationCancellation {
			ArrayList2<String> commands = new ArrayList2<>();
//			if(fullBuild) {
//				commands.add("--force");
//			}
			return commands.toArray(String.class);
		}
		
		@Override
		protected ProcessBuilder getProcessBuilder(ArrayList2<String> commands)
				throws CommonException, OperationCancellation, CoreException {
			Location projectLocation = ResourceUtils.getProjectLocation(getProject());
			return getToolManager().createToolProcessBuilder(getBuildToolPath(), projectLocation, 
				commands.toArray(String.class));
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
		
		protected void addCompilerErrorMarker(String file, String startPosStr, String errorMsg) throws CoreException {
			IResource resource = getProject().findMember(file);
			if(resource == null || !resource.exists()) {
				return; /* FIXME: errors for other projects */
			}
			
			int line = -1;
			int column = -1;
			int endLine = -1;
			int endColumn = -1;
			
			try {
				String lineStr = StringUtil.substringUntilMatch(startPosStr, ",");
				String columnStr = StringUtil.segmentAfterMatch(startPosStr, ",");
				line = Integer.valueOf(lineStr);
				column = Integer.valueOf(columnStr);
			} catch (NumberFormatException e) {
			}
			
			/* FIXME: SourceLineColumnRange path*/
			SourceLineColumnRange sourceLinePos = new SourceLineColumnRange(PathUtil.createValidPath(""), line, column, endLine, endColumn);
			ToolSourceMessage toolMessage = new ToolSourceMessage(sourceLinePos, StatusLevel.ERROR, errorMsg);
			/* FIXME: end position*/
			ToolMarkersUtil.addErrorMarker(resource, toolMessage, LangCore_Actual.BUILD_PROBLEM_ID);
		}
		
	}
	
}