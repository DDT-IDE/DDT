/*******************************************************************************
 * Copyright (c) 2014, 2014 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package mmrnmhrm.core.workspace;

import mmrnmhrm.core.engine_client.DubProcessManager;

import org.eclipse.core.resources.IProject;

import dtool.dub.DubBundleDescription;

public class CoreDubModel {
	
	protected static final WorkspaceModel dubModel = new WorkspaceModel();
	
	public static WorkspaceModel getDubModel() {
		return CoreDubModel.dubModel;
	}
	
	public static DubBundleDescription getBundleInfo(String projectName) {
		return CoreDubModel.dubModel.getBundleInfo(projectName);
	}
	
	public static ProjectInfo getProjectInfo(IProject project) {
		return CoreDubModel.dubModel.getProjectInfo(project.getName());
	}
	
	protected static final WorkspaceModelManager modelManager = new WorkspaceModelManager(CoreDubModel.dubModel);
	
	public static WorkspaceModelManager getManager() {
		return CoreDubModel.modelManager;
	}
	
	public static void startDefaultManager() {
		modelManager.startManager();
	}
	
	public static void shutdownDefaultManager() {
		modelManager.shutdownManager();
	}
	
	public static DubProcessManager getProcessManager() {
		return getManager().getProcessManager();
	}
	
}