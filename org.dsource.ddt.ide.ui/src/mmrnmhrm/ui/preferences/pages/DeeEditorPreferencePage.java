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


import mmrnmhrm.ui.DeeUIPlugin;
import mmrnmhrm.ui.preferences.DeeEditorConfigurationBlock;

import org.eclipse.dltk.ui.preferences.AbstractConfigurationBlockPreferencePage;
import org.eclipse.dltk.ui.preferences.IPreferenceConfigurationBlock;
import org.eclipse.dltk.ui.preferences.OverlayPreferenceStore;


public class DeeEditorPreferencePage extends AbstractConfigurationBlockPreferencePage {
	
	public final static String PAGE_ID = DeeUIPlugin.PLUGIN_ID + ".preferences.Editor";
	
	@Override
	protected void setDescription() {
		setDescription(null);
	}
	
	@Override
	protected void setPreferenceStore() {
		setPreferenceStore(DeeUIPlugin.getInstance().getPreferenceStore());
	}
	
	@Override
	protected IPreferenceConfigurationBlock createConfigurationBlock(OverlayPreferenceStore overlayPreferenceStore) {
		return new DeeEditorConfigurationBlock(this, overlayPreferenceStore);
	}
}
