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

import dtool.dub.BundlePath;
import melnorme.lang.ide.core.operations.BuildTarget;
import melnorme.lang.ide.core.operations.BuildOperationCreator.CommonBuildTargetOperation;
import melnorme.lang.ide.core.operations.OperationInfo;
import melnorme.lang.ide.core.project_model.BuildManager;
import melnorme.lang.ide.core.project_model.ProjectBuildInfo;
import melnorme.lang.ide.core.utils.EclipseUtils;
import melnorme.utilbox.collections.ArrayList2;
import melnorme.utilbox.core.CommonException;

public class DeeBuildManager extends BuildManager {
	
	public DeeBuildManager() {
		super();
	}
	
	@Override
	protected ManagerResourceListener init_createResourceListener() {
		return new ManagerResourceListener(EclipseUtils.epath(BundlePath.DUB_MANIFEST_Path));
	}
	
	@Override
	protected ProjectBuildInfo createDefaultProjectBuildInfo(IProject project) {
		return new ProjectBuildInfo(this, project, ArrayList2.create(
			createBuildTarget(true, null),
			createBuildTarget(true, DubBuildType.UNITTEST.getBuildTypeString())
		));
	}
	
	@Override
	protected BuildTarget createBuildTarget(boolean enabled, String targetName) {
		return new BuildTarget(enabled, targetName) {
			@Override
			public CommonBuildTargetOperation newBuildTargetOperation(OperationInfo parentOpInfo, IProject project,
					boolean fullBuild) throws CommonException {
				Path buildToolPath = getSDKToolPath();
				return new DubBuildOperation(parentOpInfo, project, buildToolPath, this, fullBuild);
			}
		};
	}
	
}