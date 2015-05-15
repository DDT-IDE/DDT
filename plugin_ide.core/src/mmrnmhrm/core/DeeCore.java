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
package mmrnmhrm.core;

import melnorme.lang.ide.core.LangCore;
import mmrnmhrm.core.engine.DToolClient;
import mmrnmhrm.core.engine.DubProcessManager;
import mmrnmhrm.core.workspace.DubModelManager;
import mmrnmhrm.core.workspace.DubWorkspaceModel;

import org.osgi.framework.BundleContext;

public class DeeCore extends LangCore {
	
	protected static final DubWorkspaceModel dubModel = new DubWorkspaceModel();
	protected static final DubModelManager modelManager = new DubModelManager(dubModel);
	
	public static DubProcessManager getDubProcessManager() {
		return getWorkspaceModelManager().getProcessManager();
	}
	
	public static DubWorkspaceModel getWorkspaceModel() {
		return dubModel;
	}
	
	public static DubModelManager getWorkspaceModelManager() {
		return modelManager;
	}
	
	public static DToolClient getDToolClient() {
		return (DToolClient) getEngineClient();
	}
	
	@Override
	protected void doCustomStart(BundleContext context) {
	}
	
	@Override
	public void doInitializeAfterUIStart() {
		modelManager.startManager(); // Start this after UI, to allow UI listener to register.
	}
	
	@Override
	protected void doCustomStop(BundleContext context) {
		modelManager.shutdownManager();
	}
	
}