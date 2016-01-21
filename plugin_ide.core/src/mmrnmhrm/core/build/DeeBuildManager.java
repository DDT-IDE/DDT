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
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import dtool.dub.DubBuildOutputParser;
import melnorme.lang.ide.core.BundleInfo;
import melnorme.lang.ide.core.LangCore;
import melnorme.lang.ide.core.operations.ILangOperationsListener_Default.IOperationConsoleHandler;
import melnorme.lang.ide.core.operations.ToolMarkersHelper;
import melnorme.lang.ide.core.operations.build.BuildManager;
import melnorme.lang.ide.core.operations.build.BuildOperationCreator;
import melnorme.lang.ide.core.operations.build.BuildTarget;
import melnorme.lang.ide.core.operations.build.CommonBuildTargetOperation;
import melnorme.lang.ide.core.operations.build.IToolOperation;
import melnorme.lang.ide.core.operations.build.ValidatedBuildTarget;
import melnorme.lang.ide.core.utils.ResourceUtils;
import melnorme.lang.tooling.data.Severity;
import melnorme.lang.tooling.ops.SourceLineColumnRange;
import melnorme.lang.tooling.ops.ToolSourceMessage;
import melnorme.utilbox.collections.ArrayList2;
import melnorme.utilbox.collections.Indexable;
import melnorme.utilbox.concurrency.OperationCancellation;
import melnorme.utilbox.core.CommonException;
import melnorme.utilbox.misc.PathUtil;
import melnorme.utilbox.misc.StringUtil;
import melnorme.utilbox.process.ExternalProcessHelper.ExternalProcessResult;
import mmrnmhrm.core.DeeCoreMessages;
import mmrnmhrm.core.dub_model.DeeBundleModelManager;
import mmrnmhrm.core.dub_model.DeeBundleModelManager.DeeBundleModel;

public class DeeBuildManager extends BuildManager {
	
	public static final String BuildType_Default = "";
	
	public DeeBuildManager(DeeBundleModel bundleModel) {
		super(bundleModel);
	}
	
	@Override
	protected void bundleProjectAdded(IProject project, BundleInfo bundleInfo) {
		if(bundleInfo.getBundleDesc().isResolved()) {
			// We ignore resolved description, because only unresolved ones have configuration info
			return;
		}
		super.bundleProjectAdded(project, bundleInfo);
	}
	
	@Override
	protected Indexable<BuildType> getBuildTypes_do() {
		return ArrayList2.<BuildType>create(
			new DeeBuildType(BuildType_Default),
			new DeeBuildType(DubBuildType.UNITTEST.getBuildTypeString())
		);
	}
	
	@Override
	protected BuildOperationCreator createBuildOperationCreator(IOperationConsoleHandler opHandler, IProject project) {
		return new BuildOperationCreator(project, opHandler) {
			
			@Override
			protected IToolOperation doCreateClearBuildMarkersOperation() {
				return new RunInDubAgentWrapper(super.doCreateClearBuildMarkersOperation());
			}
			
			@Override
			public IToolOperation doCreateBuildTargetOperation(IOperationConsoleHandler opHandler, IProject project,
					Path buildToolPath, BuildTarget buildTarget) throws CommonException, CoreException {
				return new RunInDubAgentWrapper(
					super.doCreateBuildTargetOperation(opHandler, project, buildToolPath, buildTarget));
			}
			
		};
	}
	
	protected static class RunInDubAgentWrapper implements IToolOperation {
		
		protected final IToolOperation toolOp;
		
		public RunInDubAgentWrapper(IToolOperation toolOp) {
			this.toolOp = toolOp;
		}
		
		@Override
		public void execute(IProgressMonitor pm) throws CoreException, CommonException, OperationCancellation {
			LangCore.getToolManager().submitTaskAndAwaitResult(() -> {
				toolOp.execute(pm);
				return null;
			});
		}
		
	}
	
	/* -----------------  ----------------- */
	
	protected class DeeBuildType extends BuildType {
		
		public DeeBuildType(String name) {
			super(name);
		}
		
		@Override
		protected void getDefaultBuildOptions(ValidatedBuildTarget vbt, ArrayList2<String> buildArgs) 
				throws CommonException {
			
			String buildConfigName = vbt.getBuildConfigName();
			String buildTypeName = vbt.getBuildTypeName();
			
			buildArgs.add("build");
			
			if(!buildConfigName.equals(BundleInfo.DEFAULT_CONFIGURATION)) {
				buildArgs.addElements("-c" , buildConfigName);
			}
			
			if(!buildTypeName.equals(DeeBuildManager.BuildType_Default)) {
				buildArgs.addElements("-b" , buildTypeName);
			}
		}
		
		@Override
		public CommonBuildTargetOperation getBuildOperation(ValidatedBuildTarget validatedBuildTarget,
				IOperationConsoleHandler opHandler, Path buildToolPath) throws CommonException, CoreException {
			return new DeeBuildTargetOperation(validatedBuildTarget, opHandler, buildToolPath);
		}
		
	}
	
	public class DeeBuildTargetOperation extends CommonBuildTargetOperation {
		
		public DeeBuildTargetOperation(
				ValidatedBuildTarget validatedBuildTarget, IOperationConsoleHandler opHandler, Path buildToolPath
		) throws CommonException, CoreException {
			super(validatedBuildTarget.getBuildManager(), validatedBuildTarget, opHandler, buildToolPath);
		}
		
		@Override
		protected void processBuildOutput(ExternalProcessResult processResult, IProgressMonitor pm) 
				throws CoreException {
			new DubBuildOutputParser<CoreException>() {
				@Override
				protected void processDubFailure(String dubErrorLine) throws CoreException {
					addDubFailureMarker(dubErrorLine);
				};
				
				@Override
				protected void processCompilerError(String filePathStr, String startPosStr, String errorMsg) {
					Path filePath;
					try {
						filePath = PathUtil.createPath(filePathStr);
					} catch(CommonException e) {
						LangCore.logError("Invalid path for tool message: ", e);
						return;
					}
					try {
						addCompilerErrorMarker(filePath, startPosStr, errorMsg);
					} catch (CoreException e) {
						// log, but otherwise ignore & continue
						LangCore.logStatus(e);
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
		
		protected void addCompilerErrorMarker(Path filePath, String startPosStr, String errorMsg) 
				throws CoreException {
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
			
			SourceLineColumnRange sourceLinePos = new SourceLineColumnRange(line, column, endLine, endColumn);
			ToolSourceMessage toolMessage = new ToolSourceMessage(filePath, sourceLinePos, Severity.ERROR, errorMsg);
			
			new ToolMarkersHelper(true).addErrorMarkers(toolMessage, ResourceUtils.getProjectLocation(project));
		}
		
	}
	
}