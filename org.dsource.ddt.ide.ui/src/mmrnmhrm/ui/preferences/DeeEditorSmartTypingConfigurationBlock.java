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
import java.util.List;

import melnorme.utilbox.misc.ArrayUtil;
import mmrnmhrm.ui.DeeUIPreferenceConstants;

import org.eclipse.dltk.ui.preferences.AbstractConfigurationBlock;
import org.eclipse.dltk.ui.preferences.OverlayPreferenceStore;
import org.eclipse.dltk.ui.preferences.OverlayPreferenceStore.OverlayKey;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class DeeEditorSmartTypingConfigurationBlock extends AbstractConfigurationBlock {
	
	public DeeEditorSmartTypingConfigurationBlock(OverlayPreferenceStore store) {
		super(store);
		
		addOverLayKeys(store);
	}
	
	protected void addOverLayKeys(OverlayPreferenceStore store) {
		List<OverlayKey> keys = new ArrayList<OverlayKey>();
		
		if(false) { // TODO implement
			keys.add(new OverlayKey(OverlayPreferenceStore.BOOLEAN, 
					DeeUIPreferenceConstants.AE_CLOSE_STRINGS));
			keys.add(new OverlayKey(OverlayPreferenceStore.BOOLEAN, 
					DeeUIPreferenceConstants.AE_CLOSE_BRACKETS));
		}
		keys.add(new OverlayKey(OverlayPreferenceStore.BOOLEAN, 
				DeeUIPreferenceConstants.AE_CLOSE_BRACES));
		
		keys.add(new OverlayKey(OverlayPreferenceStore.BOOLEAN, 
				DeeUIPreferenceConstants.AE_SMART_INDENT));
		
		keys.add(new OverlayKey(OverlayPreferenceStore.BOOLEAN, 
				DeeUIPreferenceConstants.AE_SMART_DEINDENT));
		
		keys.add(new OverlayKey(OverlayPreferenceStore.BOOLEAN, 
				DeeUIPreferenceConstants.AE_PARENTHESES_AS_BLOCKS));
		
		store.addKeys(ArrayUtil.createFrom(keys, OverlayKey.class));
	}
	
	@Override
	public Control createControl(Composite parent) {
		initializeDialogUnits(parent);
		
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout());
		
		createAutoClosingGroup(composite);
		createAutoEditGroup(composite);
		
		return composite;
	}
	
	protected void createAutoClosingGroup(Composite parent) {
		Composite group = createSubsection(parent, null, 
				DeePreferencesMessages.LangSmartTypingConfigurationBlock_autoclose_title);
		
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		group.setLayout(layout);
		
		if(false) {
		addCheckBox(group,
				DeePreferencesMessages.LangSmartTypingConfigurationBlock_closeStrings,
				DeeUIPreferenceConstants.AE_CLOSE_STRINGS, 0);
		
		addCheckBox(group,
				DeePreferencesMessages.LangSmartTypingConfigurationBlock_closeBrackets,
				DeeUIPreferenceConstants.AE_CLOSE_BRACKETS, 0);
		}
		
		addCheckBox(group,
				DeePreferencesMessages.LangSmartTypingConfigurationBlock_closeBraces,
				DeeUIPreferenceConstants.AE_CLOSE_BRACES, 0);
		
	}
	
	protected Composite createAutoEditGroup(Composite parent) {
		Composite group = createSubsection(parent, null, 
				DeePreferencesMessages.EditorPreferencePage_AutoEdits);
		
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		group.setLayout(layout);
		
		addCheckBox(group,
				DeePreferencesMessages.EditorPreferencePage_smartIndent,
				DeeUIPreferenceConstants.AE_SMART_INDENT, 2);
		
		if(false) { // TODO implement
		addCheckBox(group,
				DeePreferencesMessages.EditorPreferencePage_smartDeIndent,
				DeeUIPreferenceConstants.AE_SMART_DEINDENT, 2);
		}
		
		addCheckBox(group,
				DeePreferencesMessages.EditorPreferencePage_considerParenthesesAsBlocks,
				DeeUIPreferenceConstants.AE_PARENTHESES_AS_BLOCKS, 2);
		
		return group;
	}
	
}
