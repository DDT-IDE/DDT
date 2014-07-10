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
import melnorme.lang.ide.ui.preferences.AbstractPreferencesComponentPrefPage;
import melnorme.lang.ide.ui.preferences.IPreferencesBlock;
import melnorme.lang.ide.ui.preferences.LangEditorSmartTypingConfigurationBlock;


public class DeeEditorSmartTypingPreferencePage extends AbstractPreferencesComponentPrefPage {
	
	public final static String PAGE_ID = LangUIPlugin.PLUGIN_ID + ".preferences.Editor.typing";
	
	public DeeEditorSmartTypingPreferencePage() {
		super(LangUIPlugin.getInstance().getPreferenceStore());
	}
	
	@Override
	protected void setDescription() {
		setDescription(null);
	}
	
	@Override
	protected IPreferencesBlock createPreferencesComponent() {
		return new LangEditorSmartTypingConfigurationBlock(this);
	}
	
	@Override
	protected String getHelpId() {
		return null;
	}
	
}