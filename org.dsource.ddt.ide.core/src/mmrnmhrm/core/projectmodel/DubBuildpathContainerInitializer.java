/*******************************************************************************
 * Copyright (c) 2013, 2013 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package mmrnmhrm.core.projectmodel;

import static melnorme.utilbox.core.CoreUtil.array;
import mmrnmhrm.core.DeeCore;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.dltk.core.BuildpathContainerInitializer;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IBuildpathContainer;
import org.eclipse.dltk.core.IScriptProject;

public class DubBuildpathContainerInitializer extends BuildpathContainerInitializer {
	
	public static String ID = DeeCore.PLUGIN_ID + ".DubContainer";
	
	public DubBuildpathContainerInitializer() {
	}
	
	@Override
	public String getDescription(IPath containerPath, IScriptProject project) {
		return super.getDescription(containerPath, project);
	}
	
	@Override
	public void initialize(final IPath containerPath, IScriptProject project) throws CoreException {
		IBuildpathContainer container = new DubContainer(containerPath, project, null) {
			@Override
			public String getDescription() {
				return super.getDescription() + "(Initializing)";
			}
		};
		
		DLTKCore.setBuildpathContainer(containerPath, array(project), array(container), null);
	}
	
}