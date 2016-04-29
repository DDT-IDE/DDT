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
import melnorme.lang.ide.core.LangCore;
import melnorme.lang.ide.core.operations.ICoreOperation;
import melnorme.lang.ide.core.operations.ToolMarkersHelper;
import melnorme.lang.ide.core.operations.build.BuildManager;
import melnorme.lang.ide.core.operations.build.BuildTarget;
import melnorme.lang.ide.core.operations.build.BuildTargetOperation;
import melnorme.lang.ide.core.operations.build.BuildTargetOperation.BuildOperationParameters;
import melnorme.lang.ide.core.utils.ResourceUtils;
import melnorme.lang.tooling.bundle.BundleInfo;
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
import mmrnmhrm.core.engine.DeeToolManager;

public class DeeBuildManager extends BuildManager {
	
	public static final String BuildType_Default = "";
	
	public DeeBuildManager(DeeBundleModel bundleModel, DeeToolManager toolManager) {
		super(bundleModel, toolManager);
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
	
	/* FIXME: review removed used of RunInDubAgentWrapper */
	
	protected static class RunInDubAgentWrapper implements ICoreOperation {
		
		protected final ICoreOperation coreOp;
		
		public RunInDubAgentWrapper(ICoreOperation toolOp) {
			this.coreOp = toolOp;
		}
		
		@Override
		public void execute(IProgressMonitor pm) throws CommonException, OperationCancellation {
			/* FIXME: review */
			try {
				LangCore.getToolManager().submitTaskAndAwaitResult(() -> {
					coreOp.execute(pm);
					return null;
				});
			} catch(CoreException e) {
				throw LangCore.createCommonException(e);
			}
		}
		
	}
	
	/* -----------------  ----------------- */
	
	protected class DeeBuildType extends BuildType {
		
		public DeeBuildType(String name) {
			super(name);
		}
		
		@Override
		public String getDefaultCommandArguments(BuildTarget bt) throws CommonException {
			ArrayList2<String> buildArgs = new ArrayList2<>();
			
			String buildConfigName = bt.getBuildConfigName();
			String buildTypeName = bt.getBuildTypeName();
			
			buildArgs.add("build");
			
			if(!buildConfigName.equals(BundleInfo.DEFAULT_CONFIGURATION)) {
				buildArgs.addElements("-c" , buildConfigName);
			}
			
			if(!buildTypeName.equals(DeeBuildManager.BuildType_Default)) {
				buildArgs.addElements("-b" , buildTypeName);
			}
			
			return StringUtil.collToString(buildArgs, " ");
		}
		
		@Override
		public BuildTargetOperation getBuildOperation(BuildOperationParameters buildOpParams) throws CommonException {
			return new DeeBuildTargetOperation(buildOpParams);
		}
		
	}
	
	public class DeeBuildTargetOperation extends BuildTargetOperation {
		
		public DeeBuildTargetOperation(BuildOperationParameters buildOpParams) {
			super(buildOpParams);
		}
		
		@Override
		protected void processBuildOutput(ExternalProcessResult processResult, IProgressMonitor pm) 
				throws CommonException {
			new DubBuildOutputParser<CommonException>() {
				@Override
				protected void processDubFailure(String dubErrorLine) throws CommonException {
					try {
						addDubFailureMarker(dubErrorLine);
					} catch(CoreException e) {
						throw LangCore.createCommonException(e);
					}
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