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

import java.nio.file.Path;

import melnorme.lang.ide.core.operations.ToolchainPreferences;
import melnorme.lang.ide.core.utils.prefs.DerivedValuePreference;
import melnorme.lang.utils.validators.LocationOrSinglePathValidator;

public interface DeeToolPreferences extends ToolchainPreferences {
	
	public static final DerivedValuePreference<Path> DFMT_PATH = new DerivedValuePreference<>( 
			"dfmt_path", "", ToolchainPreferences.USE_PROJECT_SETTINGS,
		new LocationOrSinglePathValidator("dfmt:"));
	
}