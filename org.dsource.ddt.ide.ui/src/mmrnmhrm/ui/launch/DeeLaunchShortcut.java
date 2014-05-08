/*******************************************************************************
 * Copyright (c) 2009, 2012 IBM Corporation and others.
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

import melnorme.lang.ide.core.utils.EclipseUtils;
import melnorme.lang.ide.ui.launch.AbstractLaunchShortcut;
import mmrnmhrm.core.launch.DeeLaunchConstants;
import mmrnmhrm.core.projectmodel.DubModel;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.debug.core.ILaunchConfigurationType;

import dtool.dub.DubBundleDescription;

public class DeeLaunchShortcut extends AbstractLaunchShortcut {
	
	@Override
	protected ILaunchConfigurationType getConfigurationType() {
		return getLaunchManager().getLaunchConfigurationType(DeeLaunchConstants.ID_DEE_LAUNCH_TYPE);
	}
	
	@Override
	protected void launch(IResource resource, String mode) {
		if(resource instanceof IProject) {
			IProject project = (IProject) resource;
			resource = getProjectExecutableArtifact(project);
			if(resource == null) {
				return;
			}
		}
		super.launch(resource, mode);
	}
	
	protected IFile getProjectExecutableArtifact(IProject project) {
		String name = project.getName();
		DubBundleDescription bundleInfo = DubModel.getBundleInfo(name);
		Path targetFilePath = bundleInfo.getMainBundle().getEffectiveTargetFullPath();
		
		return project.getFile(EclipseUtils.getPath(targetFilePath));
	}
	
}