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

import dtool.dub.DubHelper;
import melnorme.lang.ide.core.operations.ToolchainPreferences;
import melnorme.lang.ide.core.utils.prefs.PreferenceHelper;

public class PreferencesOverride {
	
	@SuppressWarnings("unused")
	public static String getKeyIdentifer(String key, PreferenceHelper<?> helper) {
		return key;
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T getDefaultValue(T defaultValue, PreferenceHelper<?> helper) {
		if(helper == ToolchainPreferences.SDK_PATH) {
//			ToolchainPreferences.SDK_PATH.setPreferencesDefaultValue(
//				DubHelper.DUB_PATH_OVERRIDE != null ? DubHelper.DUB_PATH_OVERRIDE : "dub");
			return (T) (DubHelper.DUB_PATH_OVERRIDE != null ? DubHelper.DUB_PATH_OVERRIDE : "dub");
		}
		
		return defaultValue;
	}
	
}