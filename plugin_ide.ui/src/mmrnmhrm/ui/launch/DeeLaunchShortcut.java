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

import dtool.dub.DubBundleDescription;
import melnorme.lang.ide.core.utils.EclipseUtils;
import melnorme.lang.ide.ui.launch.AbstractLaunchShortcut2;
import mmrnmhrm.core.DeeCore;
import mmrnmhrm.core.launch.DeeLaunchConstants;

public class DeeLaunchShortcut extends AbstractLaunchShortcut2 {
	
	@Override
	protected String getLaunchTypeId() {
		return DeeLaunchConstants.ID_DEE_LAUNCH_TYPE;
	}
	
	@Override
	protected ResourceLaunchTarget getLaunchTargetForResource(IResource resource) {
		IProject project = (IProject) resource.getProject();
		
		resource = getProjectExecutableArtifact(project);
		if(resource == null) {
			return null;
		}
		return new ResourceLaunchTarget(resource);
	}
	
	protected IFile getProjectExecutableArtifact(IProject project) {
		DubBundleDescription bundleInfo = DeeCore.getWorkspaceModel().getBundleInfo(project);
		Path targetFilePath = bundleInfo.getMainBundle().getEffectiveTargetFullPath();
		
		return project.getFile(EclipseUtils.epath(targetFilePath));
	}
	
}