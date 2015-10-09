/*******************************************************************************
 * Copyright (c) 2014 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package mmrnmhrm.core;

import org.eclipse.core.resources.IProject;

import dtool.dub.DubHelper;
import melnorme.lang.ide.core.LangCore;
import melnorme.lang.ide.core.operations.ToolchainPreferences;
import melnorme.lang.ide.core.utils.prefs.StringPreference;

public interface DeeCorePreferences {
	
	public static StringPreference PREF_DUB_PATH = new StringPreference(LangCore.PLUGIN_ID, "DUB_PATH", "dub",
		ToolchainPreferences.USE_PROJECT_SETTINGS);
	
	
	public static String getEffectiveDubPath(IProject project) {
		return DubHelper.DUB_PATH_OVERRIDE != null ? 
				DubHelper.DUB_PATH_OVERRIDE : 
				PREF_DUB_PATH.getProjectPreference().getEffectiveValue(project);
	}
	
}