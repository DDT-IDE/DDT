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
package mmrnmhrm.core.projectmodel;

import mmrnmhrm.core.projectmodel.elements.DubDependenciesContainer;

import org.eclipse.core.resources.IProject;

import dtool.dub.DubBundleDescription;

public class CoreDubModel {

	protected static final DubModel dubModel = new DubModel();
	
	public static IDubModel getDubModel() {
		return CoreDubModel.dubModel;
	}
	
	public static DubBundleDescription getBundleInfo(String projectName) {
		return CoreDubModel.dubModel.getBundleInfo(projectName);
	}
	
	public static DubDependenciesContainer getDubContainer(IProject project) {
		DubBundleDescription bundleInfo = CoreDubModel.getBundleInfo(project.getName());
		if(bundleInfo == null)
			return null;
		return new DubDependenciesContainer(bundleInfo, project);
	}
	
	
	protected static final DubModelManager modelManager = new DubModelManager(CoreDubModel.dubModel);
	
	public static DubModelManager getManager() {
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