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
import melnorme.lang.ide.ui.preferences.AbstractConfigurationBlockPreferencePage2;
import melnorme.lang.ide.ui.preferences.IPreferenceConfigurationBlock2;
import mmrnmhrm.ui.preferences.DeeEditorConfigurationBlock;


public class DeeEditorPreferencePage extends AbstractConfigurationBlockPreferencePage2 {
	
	public final static String PAGE_ID = LangUIPlugin.PLUGIN_ID + ".preferences.Editor";
	
	@Override
	protected void setDescription() {
		setDescription(null);
	}
	
	@Override
	protected void setPreferenceStore() {
		setPreferenceStore(LangUIPlugin.getInstance().getPreferenceStore());
	}
	
	@Override
	protected IPreferenceConfigurationBlock2 createConfigurationBlock() {
		return new DeeEditorConfigurationBlock(this, getPreferenceStore());
	}
	
	@Override
	protected String getHelpId() {
		return null;
	}
	
}