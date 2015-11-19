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

import dtool.dub.DubHelper;
import melnorme.lang.ide.core.LangCore;
import melnorme.lang.ide.core.operations.ToolchainPreferences;

public class DeeCore extends LangCore {
	
	@Override
	protected void doCustomStart(BundleContext context) {
		ToolchainPreferences.SDK_PATH2.getGlobalPreference().setPreferencesDefaultValue(
			DubHelper.DUB_PATH_OVERRIDE != null ? DubHelper.DUB_PATH_OVERRIDE : "dub");
	}
	
	@Override
	protected void doCustomStop(BundleContext context) {
	}
	
}