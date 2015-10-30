/*******************************************************************************
 * Copyright (c) 2011, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package mmrnmhrm.ui.preferences.pages;

import melnorme.lang.ide.ui.preferences.EditorContentAssistConfigurationBlock;
import melnorme.lang.ide.ui.preferences.common.AbstractPreferencesBlockPrefPage;
import mmrnmhrm.ui.DeeUIPlugin;

public class DeeContentAssistPreferencePage extends AbstractPreferencesBlockPrefPage {
	
	public final static String PAGE_ID = DeeUIPlugin.PLUGIN_ID + ".PreferencePages.Editor.ContentAssist";
	
	public DeeContentAssistPreferencePage() {
		super();
	}
	
	@Override
	protected EditorContentAssistConfigurationBlock init_createPreferencesBlock() {
		return new EditorContentAssistConfigurationBlock();
	}
	
	@Override
	protected String getHelpId() {
		return null;
	}
	
}
