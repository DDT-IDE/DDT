/*******************************************************************************
 * Copyright (c) 2014, 2014 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package mmrnmhrm.core.workspace;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IProject;

import dtool.dub.DubBundleDescription;
import dtool.engine.compiler_installs.CompilerInstall;
import melnorme.lang.ide.core.project_model.ProjectBasedModel;
import melnorme.utilbox.misc.SimpleLogger;

/**
 * DUB model. Holds information about DUB bundles, for the projects in the workspace.
 * Designed to be managed concurrently by some other code (see {@link DubModelManager}).
 * Can notify listeners of updates. 
 */
public class DubWorkspaceModel extends ProjectBasedModel<DubProjectInfo, IDubModelListener> {
	
	protected final SimpleLogger log = DubModelManager.log;
	
	public DubWorkspaceModel() {
	}
	
	@Override
	protected SimpleLogger getLog() {
		return log;
	}
	
	@Override
	public DubProjectInfo getProjectInfo(IProject project) {
		return super.getProjectInfo(project);
	}
	
	public synchronized DubBundleDescription getBundleInfo(IProject project) {
		DubProjectInfo projectInfo = getProjectInfo(project);
		return projectInfo == null ? null : projectInfo.getBundleDesc();
	}
	
	public synchronized Set<String> getDubProjects() {
		return new HashSet<>(projectInfos.keySet());
	}
	
	protected synchronized DubProjectInfo addProjectInfo(IProject project, DubBundleDescription dubBundleDescription, 
			CompilerInstall compilerInstall) {
		DubProjectInfo newProjectInfo = new DubProjectInfo(compilerInstall, dubBundleDescription);
		return setProjectInfo(project, newProjectInfo);
	}
	
	@Override
	public DubProjectInfo removeProjectInfo(IProject project) {
		return super.removeProjectInfo(project);
	}
	
}