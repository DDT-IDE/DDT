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
import mmrnmhrm.core.engine.DeeEngineClient;
import mmrnmhrm.core.engine.DeeToolManager;

public class LangCore_Actual extends LangCore_Base {
	
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
	
	/* ----------------- Owned singletons: ----------------- */
	
	protected final CoreSettings coreSettings;
	protected final DeeToolManager toolManager;
	protected final DeeBundleModelManager bundleManager;
	protected final BuildManager buildManager;
	protected final DeeEngineClient sourceModelManager;
	
	public LangCore_Actual() {
		super();
		
		coreSettings = createCoreSettings();
		toolManager = createToolManager();
		bundleManager = createBundleModelManager();
		buildManager = new DeeBuildManager(deeBundleModelManager().getModel(), toolManager);
		sourceModelManager = createSourceModelManager();
	}
	
	@Override
	protected CoreSettings createCoreSettings() {
		return new CoreSettings() {
			@Override
			public DubLocationValidator getSDKLocationValidator() {
				return new DubLocationValidator();
			}
		};
	}
	
	/* -----------------  ----------------- */
	
	protected DeeToolManager createToolManager() {
		return new DeeToolManager();
	}
	public static DeeToolManager deeToolManager() {
		return instance.toolManager;
	}
	
	public static DeeBundleModelManager createBundleModelManager() {
		return new DeeBundleModelManager();
	}
	public static DeeBundleModelManager deeBundleModelManager() {
		return instance.bundleManager;
	}
	
	public static DeeEngineClient createSourceModelManager() {
		return new DeeEngineClient();
	}
	public static DeeEngineClient deeSourceModelManager() {
		return instance.sourceModelManager;
	}
	
}