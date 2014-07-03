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

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import melnorme.utilbox.misc.ListenerListHelper;
import melnorme.utilbox.misc.SimpleLogger;
import mmrnmhrm.core.workspace.IWorkspaceModel.IWorkspaceModelListener;

import org.eclipse.core.resources.IProject;

import dtool.dub.DubBundleDescription;
import dtool.engine.compiler_installs.CompilerInstall;

public class WorkspaceModel extends ListenerListHelper<IWorkspaceModelListener> implements IWorkspaceModel {
	
	protected final SimpleLogger log = WorkspaceModelManager.log;
	
	protected final HashMap<String, ProjectInfo> projectInfos = new HashMap<>();
	
	public WorkspaceModel() {
	}
	
	@Override
	public void addListener(IWorkspaceModelListener listener) {
		super.addListener(listener);
	}
	@Override
	public void removeListener(IWorkspaceModelListener listener) {
		super.removeListener(listener);
	}
	
	protected void fireUpdateEvent(DubModelUpdateEvent updateEvent) {
		for (IWorkspaceModelListener listener : getListeners()) {
			listener.notifyUpdateEvent(updateEvent);
		}
	}
	
	@Override
	public synchronized DubBundleDescription getBundleInfo(IProject project) {
		ProjectInfo projectInfo = getProjectInfo(project);
		return projectInfo == null ? null : projectInfo.getBundleDesc();
	}
	
	@Override
	public synchronized ProjectInfo getProjectInfo(IProject project) {
		return projectInfos.get(project.getName());
	}
	
	@Override
	public synchronized Set<String> getDubProjects() {
		return new HashSet<>(projectInfos.keySet());
	}
	
	protected synchronized ProjectInfo addProjectInfo(IProject project, DubBundleDescription dubBundleDescription, 
			CompilerInstall compilerInstall) {
		ProjectInfo newProjectInfo = new ProjectInfo(compilerInstall, dubBundleDescription);
		return addProjectInfo(project, newProjectInfo);
	}
	
	protected synchronized ProjectInfo addProjectInfo(IProject project, ProjectInfo newProjectInfo) {
		String projectName = project.getName();
		projectInfos.put(projectName, newProjectInfo);
		log.println("DUB project model added: " + projectName);
		fireUpdateEvent(new DubModelUpdateEvent(project, newProjectInfo.getBundleDesc()));
		return newProjectInfo;
	}
	
	protected synchronized void removeProjectInfo(IProject project) {
		ProjectInfo oldProjectInfo = projectInfos.remove(project.getName());
		assertNotNull(oldProjectInfo);
		DubBundleDescription oldDesc = oldProjectInfo.getBundleDesc();
		log.println("DUB project model removed: " + project.getName());
		fireUpdateEvent(new DubModelUpdateEvent(project, oldDesc));
	}
	
}