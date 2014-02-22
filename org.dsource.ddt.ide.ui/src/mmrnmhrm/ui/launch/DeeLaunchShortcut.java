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

import java.util.ArrayList;
import java.util.List;

import melnorme.lang.ide.ui.LangUIMessages;
import melnorme.lang.ide.ui.launch.AbstractLaunchShortcut;
import mmrnmhrm.core.launch.DeeLaunchConstants;
import mmrnmhrm.core.projectmodel.DubModel;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.jface.dialogs.MessageDialog;

import dtool.dub.DubBundleDescription;

public class DeeLaunchShortcut extends AbstractLaunchShortcut {
	
	@Override
	protected ILaunchConfigurationType getConfigurationType() {
		return getLaunchManager().getLaunchConfigurationType(DeeLaunchConstants.ID_DEE_LAUNCH_TYPE);
	}
	
	@Override
	protected IResource[] findLaunchablesDo(Object[] objects, IProgressMonitor pm) {
		List<IResource> list = new ArrayList<IResource>(objects.length);
		for (int i = 0; i < objects.length; i++) {
			Object object = objects[i];
			if (object instanceof IFile) {
				IFile f = (IFile) object;
				if(!f.getName().startsWith("."))
					list.add(f);
			} else if (object instanceof IProject) {
				IProject proj = (IProject) object;
				list.add(proj);
			} else if (object instanceof IScriptProject) {
				IScriptProject scriptProj = (IScriptProject) object;
				list.add(scriptProj.getProject());
			}
		}
		return list.toArray(new IResource[list.size()]);
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
		
		MessageDialog.openError(getShell(), LangUIMessages.ScriptLaunchShortcut_Error1, 
			"Could not determine default target executable for project " + name );
		
		return null; // TODO determine target binaryPath
	}
	
}