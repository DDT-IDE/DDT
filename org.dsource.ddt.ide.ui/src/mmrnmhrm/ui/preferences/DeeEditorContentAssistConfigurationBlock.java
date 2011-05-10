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

import org.eclipse.dltk.ui.preferences.CodeAssistConfigurationBlock;
import org.eclipse.dltk.ui.preferences.OverlayPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;

public class DeeEditorContentAssistConfigurationBlock extends CodeAssistConfigurationBlock {
	
	public DeeEditorContentAssistConfigurationBlock(PreferencePage mainPreferencePage, OverlayPreferenceStore store) {
		super(mainPreferencePage, store);
	}
	
}
