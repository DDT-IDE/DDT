/*******************************************************************************
 * Copyright (c) 2015, 2015 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package mmrnmhrm.ui.launch;

import java.nio.file.Path;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;

import melnorme.lang.ide.core.LangCore;
import melnorme.lang.ide.core.operations.build.BuildTarget;
import melnorme.lang.ide.core.project_model.ProjectBuildInfo;
import melnorme.lang.ide.core.utils.EclipseUtils;
import melnorme.lang.ide.ui.launch.AbstractLaunchShortcut2;
import melnorme.utilbox.concurrency.OperationCancellation;
import melnorme.utilbox.core.CommonException;
import mmrnmhrm.core.launch.DeeLaunchConstants;

public class DeeLaunchShortcut extends AbstractLaunchShortcut2 {
	
	@Override
	protected String getLaunchTypeId() {
		return DeeLaunchConstants.ID_DEE_LAUNCH_TYPE;
	}
	
	@Override
	protected ResourceLaunchTarget getLaunchTargetForResource(IResource resource) 
			throws CommonException, OperationCancellation {
		IProject project = (IProject) resource.getProject();
		
		resource = getProjectExecutableArtifact(project);
		if(resource == null) {
			return null;
		}
		return new ResourceLaunchTarget(resource);
	}
	
	protected IFile getProjectExecutableArtifact(IProject project) throws CommonException {
		ProjectBuildInfo buildInfo = LangCore.getBuildManager().getBuildInfo(project);
		if(buildInfo == null) throw new CommonException("No project build info available.");
		
		BuildTarget buildTarget = buildInfo.getDefaultBuildTarget();
		Path targetFilePath = buildTarget.getArtifactPath(project);
		if(targetFilePath == null) return null;
		
		return project.getFile(EclipseUtils.epath(targetFilePath));
	}
	
}