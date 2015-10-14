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

import melnorme.lang.ide.ui.LangUIPlugin;
import melnorme.lang.ide.ui.preferences.LangEditorContentAssistConfigurationBlock;
import melnorme.lang.ide.ui.preferences.common.AbstractPreferencesBlockPrefPage;
import mmrnmhrm.ui.DeeUIPlugin;

public class DeeContentAssistPreferencePage extends AbstractPreferencesBlockPrefPage {
	
	public final static String PAGE_ID = DeeUIPlugin.PLUGIN_ID + ".PreferencePages.Editor.ContentAssist";
	
	public DeeContentAssistPreferencePage() {
		super(LangUIPlugin.getInstance().getPreferenceStore());
	}
	
	@Override
	protected LangEditorContentAssistConfigurationBlock createPreferencesBlock() {
		return new LangEditorContentAssistConfigurationBlock(this);
	}
	
	@Override
	protected String getHelpId() {
		return null;
	}
	
}
