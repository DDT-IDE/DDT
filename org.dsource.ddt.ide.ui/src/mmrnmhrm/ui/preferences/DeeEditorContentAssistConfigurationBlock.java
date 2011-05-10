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
package mmrnmhrm.ui.preferences;

import java.util.ArrayList;

import org.eclipse.dltk.ui.PreferenceConstants;
import org.eclipse.dltk.ui.preferences.CodeAssistConfigurationBlock;
import org.eclipse.dltk.ui.preferences.OverlayPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

public class DeeEditorContentAssistConfigurationBlock extends CodeAssistConfigurationBlock {
	
	public DeeEditorContentAssistConfigurationBlock(PreferencePage mainPreferencePage, OverlayPreferenceStore store) {
		super(mainPreferencePage, store);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	protected void getOverlayKeys(@SuppressWarnings("rawtypes") ArrayList overlayKeys) {
		super.getOverlayKeys(overlayKeys);
		overlayKeys.add(new OverlayPreferenceStore.OverlayKey(
				OverlayPreferenceStore.STRING,
				PreferenceConstants.CODEASSIST_AUTOACTIVATION_TRIGGERS));
	}
	
	@Override
	protected void addAutoActivationSection(Composite composite) {
		super.addAutoActivationSection(composite);
		
		String autoTriggerKey = PreferenceConstants.CODEASSIST_AUTOACTIVATION_TRIGGERS;
		String label = DeePreferencesMessages.ContentAssistConfigBlock_deeAutoActivationTriggers;
		Control[] autoTrigger = addLabelledTextField(composite, label, autoTriggerKey, 4, 2, false);
		((Text) autoTrigger[1]).setTextLimit(Text.LIMIT);
	}
	
}
