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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;

import melnorme.lang.ide.core.CorePreferencesLookup;

public class DeeCorePreferences implements DeeCorePreferencesConstants {
	
	public static String getDubPath() {
		return new CorePreferencesLookup().getString(PREF_DUB_PATH, "");
	}

	public static String getDubBuildOptions(IProject project) {
		return new CorePreferencesLookup(project).getString(PREF_DUB_BUILD_OPTIONS, "");
	}
	
	public static void putDubBuildOptions(IProject project, String value) {
		getProjectPreferences(project).put(PREF_DUB_BUILD_OPTIONS, value);
	}
	
	public static IEclipsePreferences getProjectPreferences(IProject project) {
		return new ProjectScope(project).getNode(DeeCore.PLUGIN_ID);
	}
	
}