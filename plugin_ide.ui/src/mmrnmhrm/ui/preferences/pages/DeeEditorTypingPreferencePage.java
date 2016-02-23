/*******************************************************************************
 * Copyright (c) 2007, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package mmrnmhrm.ui.preferences.pages;


import melnorme.lang.ide.ui.LangUIPlugin;
import melnorme.lang.ide.ui.preferences.EditorTypingConfigurationBlock;
import melnorme.lang.ide.ui.preferences.common.AbstractPreferencesBlockPrefPage;
import melnorme.lang.ide.ui.preferences.common.PreferencesPageContext;


public class DeeEditorTypingPreferencePage extends AbstractPreferencesBlockPrefPage {
	
	public final static String PAGE_ID = LangUIPlugin.PLUGIN_ID + "PreferencePages.Editor.Typing";
	
	public DeeEditorTypingPreferencePage() {
		super();
	}
	
	@Override
	protected EditorTypingConfigurationBlock init_createPreferencesBlock(PreferencesPageContext prefContext) {
		return new EditorTypingConfigurationBlock(prefContext);
	}
	
	@Override
	protected String getHelpId() {
		return null;
	}
	
}