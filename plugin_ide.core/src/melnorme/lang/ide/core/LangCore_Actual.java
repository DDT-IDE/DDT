/*******************************************************************************
 * Copyright (c) 2015 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package melnorme.lang.ide.core;

import melnorme.lang.ide.core.operations.build.BuildManager;
import mmrnmhrm.core.build.DeeBuildManager;
import mmrnmhrm.core.build.DubLocationValidator;
import mmrnmhrm.core.dub_model.DeeBundleModelManager;
import mmrnmhrm.core.dub_model.DeeBundleModelManager.DeeBundleModel;
import mmrnmhrm.core.engine.DeeEngineClient;
import mmrnmhrm.core.engine.DeeToolManager;

public class LangCore_Actual {
	
	public static final String PLUGIN_ID = "org.dsource.ddt.ide.core";
	public static final String NATURE_ID = PLUGIN_ID +".nature";
	
	public static final String BUILDER_ID = PLUGIN_ID + ".DubBuilder";
	public static final String BUILD_PROBLEM_ID = PLUGIN_ID + ".build_problem";
	public static final String SOURCE_PROBLEM_ID = PLUGIN_ID + ".source_problem";
	
	// Note: the variable should not be named with a prefix of LANGUAGE, 
	// or it will interfere with MelnormeEclipse templating
	public static final String NAME_OF_LANGUAGE = "D";
	
	public static final String VAR_NAME_SdkToolPath = "DUB_TOOL_PATH";
	public static final String VAR_NAME_SdkToolPath_DESCRIPTION = "The path of the DUB tool";
	
	public static LangCore instance;
	
	/* ----------------- Owned singletons: ----------------- */
	
	protected final CoreSettings coreSettings;
	protected final DeeToolManager toolManager;
	protected final DeeBundleModelManager bundleManager;
	protected final BuildManager buildManager;
	protected final DeeEngineClient sourceModelManager;
	
	public LangCore_Actual() {
		instance = (LangCore) this;
		
		coreSettings = createCoreSettings();
		toolManager = new DeeToolManager();
		bundleManager = createBundleModelManager();
		buildManager = new DeeBuildManager(bundleManager.getModel(), toolManager);
		sourceModelManager = createSourceModelManager();
	}
	
	protected CoreSettings createCoreSettings() {
		return new CoreSettings() {
			@Override
			public DubLocationValidator getSDKLocationValidator() {
				return new DubLocationValidator();
			}
		};
	}
	
	/* -----------------  ----------------- */
	
	public static DeeEngineClient createSourceModelManager() {
		return new DeeEngineClient();
	}
	
	public static DeeBundleModelManager createBundleModelManager() {
		return new DeeBundleModelManager();
	}
	
	/* -----------------  ----------------- */ 
	
	public static CoreSettings settings() {
		return instance.coreSettings;
	}
	
	
	public static DeeToolManager getToolManager() {
		return instance.toolManager;
	}
	
	public static DeeBundleModelManager getBundleModelManager() {
		return instance.bundleManager;
	}
	public static DeeBundleModel getBundleModel() {
		return getBundleModelManager().getModel();
	}
	
	public static BuildManager getBuildManager() {
		return instance.buildManager;
	}
	public static DeeEngineClient getSourceModelManager() {
		return instance.sourceModelManager;
	}
	
}