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

import melnorme.lang.ide.core.utils.prefs.ProgramArgumentsPreference;
import melnorme.lang.ide.core.utils.prefs.StringPreference;

public interface DeeCorePreferencesConstants {
	
	static StringPreference PREF_DUB_PATH = new StringPreference("DUB_PATH", "dub");
	static ProgramArgumentsPreference DUB_BUILD_OPTIONS = new ProgramArgumentsPreference("DUB_BUILD_OPTIONS", "");
	
}