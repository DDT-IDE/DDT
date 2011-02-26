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
package mmrnmhrm.ui.preferences;

import java.util.ArrayList;

import melnorme.utilbox.misc.ArrayUtil;

import org.eclipse.dltk.ui.PreferenceConstants;
import org.eclipse.dltk.ui.preferences.EditorConfigurationBlock;
import org.eclipse.dltk.ui.preferences.OverlayPreferenceStore;
import org.eclipse.dltk.ui.preferences.OverlayPreferenceStore.OverlayKey;
import org.eclipse.dltk.ui.preferences.PreferencesMessages;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class DeeEditorConfigurationBlock extends EditorConfigurationBlock {
	
	public DeeEditorConfigurationBlock(PreferencePage mainPreferencePage,
			OverlayPreferenceStore store) {
		this(mainPreferencePage, store, FLAG_TAB_POLICY | FLAG_TAB_ALWAYS_INDENT);
		
	}
	
	protected DeeEditorConfigurationBlock(PreferencePage mainPreferencePage, OverlayPreferenceStore store, int flags) {
		super(mainPreferencePage, store, flags);
		store.addKeys(createOverlayPreferenceKeys());
	}
	
	protected OverlayKey[] createOverlayPreferenceKeys() {
		ArrayList<OverlayKey> keys = new ArrayList<OverlayKey>();
		
		keys.add(new OverlayPreferenceStore.OverlayKey(OverlayPreferenceStore.BOOLEAN,
				PreferenceConstants.EDITOR_SMART_HOME_END));
		keys.add(new OverlayPreferenceStore.OverlayKey(OverlayPreferenceStore.BOOLEAN,
				PreferenceConstants.EDITOR_SUB_WORD_NAVIGATION));
		
		return ArrayUtil.createFrom(keys, OverlayKey.class);
	}
	
	@Override
	public Control createControl(Composite parent) {
		Composite composite = (Composite) super.createControl(parent);
		Composite group = createSmartNavigationGroup(composite);
		group.moveBelow(composite.getChildren()[0]);
		return composite;
	}
	
	protected Composite createSmartNavigationGroup(Composite parent) {
		Composite group = createSubsection(parent, null, 
				PreferencesMessages.EditorPreferencePage_title0);
		
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		group.setLayout(layout);
		
		addCheckBox(group,
				PreferencesMessages.EditorPreferencePage_smartHomeEnd,
				PreferenceConstants.EDITOR_SMART_HOME_END, 0);
		
		addCheckBox(group,
				PreferencesMessages.EditorPreferencePage_subWordNavigation,
				PreferenceConstants.EDITOR_SUB_WORD_NAVIGATION, 0);
		
		return group;
	}
	
}
