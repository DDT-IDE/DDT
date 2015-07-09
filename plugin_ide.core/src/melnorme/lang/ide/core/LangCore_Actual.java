/*******************************************************************************
 * Copyright (c) 2015, 2015 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package melnorme.lang.ide.core;

import mmrnmhrm.core.DeeCore;
import mmrnmhrm.core.build.DeeBuildManager;
import mmrnmhrm.core.dub_model.DeeBundleModelManager;
import mmrnmhrm.core.engine.DeeEngineClient;
import mmrnmhrm.core.engine.DeeToolManager;

public class LangCore_Actual {
	
	public static final String PLUGIN_ID = "org.dsource.ddt.ide.core";
	public static final String NATURE_ID = PLUGIN_ID +".nature";
	
	public static final String BUILDER_ID = PLUGIN_ID + ".DubBuilder";
	public static final String BUILD_PROBLEM_ID = PLUGIN_ID + ".build_problem";
	public static final String SOURCE_PROBLEM_ID = PLUGIN_ID + ".source_problem";
	
	public static final String LANGUAGE_NAME = "D";
	
	public static DeeToolManager createToolManagerSingleton() {
		return new DeeToolManager();
	}
	public static DeeEngineClient createEngineClient() {
		return new DeeEngineClient();
	}
	public static DeeBundleModelManager createBundleModelManager() {
		return new DeeBundleModelManager();
	}
	public static DeeBuildManager createBuildManager() {
		return new DeeBuildManager(DeeCore.getDeeBundleModel());
	}
	
}