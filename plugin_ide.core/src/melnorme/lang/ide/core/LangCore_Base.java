/*******************************************************************************
 * Copyright (c) 2016 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package melnorme.lang.ide.core;

import melnorme.lang.ide.core.engine.SourceModelManager;
import melnorme.lang.ide.core.operations.ToolManager;
import melnorme.lang.ide.core.operations.build.BuildManager;
import melnorme.lang.ide.core.project_model.BundleModelManager;
import melnorme.lang.ide.core.project_model.LangBundleModel;

/* FIXME: make lang code*/
public abstract class LangCore_Base {
	
	public static LangCore instance;
	
	public static LangCore get() {
		return instance;
	}
	
	public LangCore_Base() {
		super();
		instance = (LangCore) this;
	}
	
	protected abstract CoreSettings createCoreSettings();
	
	/* -----------------  ----------------- */ 
	
	public static CoreSettings settings() {
		return instance.coreSettings;
	}
	
	
	public static ToolManager getToolManager() {
		return instance.toolManager;
	}
	
	public static BundleModelManager<? extends LangBundleModel> getBundleModelManager() {
		return instance.bundleManager;
	}
	public static LangBundleModel getBundleModel() {
		return getBundleModelManager().getModel();
	}
	
	public static BuildManager getBuildManager() {
		return instance.buildManager;
	}
	public static SourceModelManager getSourceModelManager() {
		return instance.sourceModelManager;
	}

}