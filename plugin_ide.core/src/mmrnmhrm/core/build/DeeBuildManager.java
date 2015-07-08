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

import org.eclipse.core.resources.IProject;

import melnorme.lang.ide.core.operations.OperationInfo;
import melnorme.lang.ide.core.operations.build.BuildManager;
import melnorme.lang.ide.core.operations.build.BuildOperationCreator;
import melnorme.lang.ide.core.operations.build.BuildTarget;
import melnorme.lang.ide.core.operations.build.CommonBuildTargetOperation;
import melnorme.lang.ide.core.operations.build.IBuildTargetOperation;
import melnorme.lang.ide.core.project_model.AbstractBundleInfo;
import melnorme.lang.ide.core.project_model.AbstractBundleInfo.BuildConfiguration;
import melnorme.lang.ide.core.project_model.ProjectBuildInfo;
import melnorme.utilbox.collections.ArrayList2;
import melnorme.utilbox.core.CommonException;
import mmrnmhrm.core.DeeCore;
import mmrnmhrm.core.workspace.DeeBundleModel;
import mmrnmhrm.core.workspace.DubBundleInfo;

public class DeeBuildManager extends BuildManager {
	
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
	protected void addBuildTargetFromConfig(ArrayList2<BuildTarget> buildTargets, BuildConfiguration buildConfig,
			ProjectBuildInfo currentBuildInfo, boolean isFirstConfig) {
		String name = buildConfig.getName();
		addBuildTargetFromConfig(buildTargets, buildConfig, currentBuildInfo, isFirstConfig, name);
		
		// Create unittest variant
		String newName = buildConfig.getName() + ":" + DubBuildType.UNITTEST.getBuildTypeString();
		addBuildTargetFromConfig(buildTargets, buildConfig, currentBuildInfo, isFirstConfig, newName);
	}
	
	@Override
	public IBuildTargetOperation newProjectBuildOperation(IProject project, OperationInfo parentOpInfo,
			boolean fullBuild) throws CommonException {
		return new DeeBuildOperationCreator(project, parentOpInfo, fullBuild).newProjectBuildOperation();
	}
	
	public static class DeeBuildOperationCreator extends BuildOperationCreator {
		
		public DeeBuildOperationCreator(IProject project, OperationInfo parentOpInfo, boolean fullBuild) {
			super(project, parentOpInfo, fullBuild);
		}
		
		@Override
		public IBuildTargetOperation newProjectBuildOperation() throws CommonException {
			IBuildTargetOperation projectBuildOp = super.newProjectBuildOperation();
			
			return (pm) -> {
				DeeCore.getDubProcessManager().submitTaskAndAwaitResult(() -> {
					projectBuildOp.execute(pm);
					return null;
				});
			};
		}
		
	}
	
	@Override
	public CommonBuildTargetOperation createBuildTargetSubOperation(OperationInfo parentOpInfo, IProject project,
			Path buildToolPath, BuildTarget buildTarget, boolean fullBuild) {
		return new DubBuildTargetOperation(parentOpInfo, project, buildToolPath, buildTarget, fullBuild);
	}
	
}