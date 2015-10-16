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

import org.osgi.framework.BundleContext;

import melnorme.lang.ide.core.LangCore;
import mmrnmhrm.core.dub_model.DeeBundleModelManager;
import mmrnmhrm.core.dub_model.DeeBundleModelManager.DeeBundleModel;
import mmrnmhrm.core.engine.DeeEngineClient;
import mmrnmhrm.core.engine.DeeToolManager;

public class DeeCore extends LangCore {
	
	public static DeeEngineClient getDToolClient() {
		return (DeeEngineClient) getEngineClient();
	}
	
	public static DeeBundleModelManager getDeeBundleModelManager() {
		return (DeeBundleModelManager) getBundleModelManager();
	}
	
	public static DeeToolManager getDubProcessManager() {
		return getDeeBundleModelManager().getProcessManager();
	}
	
	public static DeeBundleModel getDeeBundleModel() {
		return getDeeBundleModelManager().getModel();
	}
	
	@Override
	protected void doCustomStart(BundleContext context) {
	}
	
	@Override
	protected void doCustomStop(BundleContext context) {
	}
	
}