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
package mmrnmhrm.ui.preferences.pages;

import melnorme.lang.ide.ui.preferences.LangSDKConfigBlock;
import melnorme.lang.ide.ui.preferences.common.PreferencesPageContext;
import melnorme.lang.ide.ui.preferences.pages.RootPreferencePage;

/**
 * The root preference page for D
 */
public class DeeRootPreferencePage extends RootPreferencePage {
	
	public DeeRootPreferencePage() {
	}
	
	@Override
	protected LangSDKConfigBlock init_createPreferencesBlock(PreferencesPageContext prefContext) {
		return new DeeSDKConfigBlock(prefContext);
	}
	
}