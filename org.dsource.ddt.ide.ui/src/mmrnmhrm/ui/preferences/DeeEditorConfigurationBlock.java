/*******************************************************************************
 * Copyright (c) 2007, 2014 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package mmrnmhrm.ui.preferences;

import melnorme.lang.ide.ui.preferences.EditorAppearanceColorsComponent.EditorColorItem;
import melnorme.lang.ide.ui.preferences.EditorConfigurationBlock;
import melnorme.lang.ide.ui.preferences.PreferencesMessages;
import melnorme.lang.ide.ui.preferences.fields.CheckBoxConfigField;

import org.eclipse.dltk.ui.PreferenceConstants;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

public class DeeEditorConfigurationBlock extends EditorConfigurationBlock {
	
	public DeeEditorConfigurationBlock(PreferencePage mainPreferencePage) {
		super(mainPreferencePage);
	}
	
	@Override
	protected void createAppearanceGroup(Composite parent) {
		addConfigComponent(parent, 0, new CheckBoxConfigField(
				PreferencesMessages.EditorPreferencePage_matchingBrackets, 
				PreferenceConstants.EDITOR_MATCHING_BRACKETS
		));
		
		super.createAppearanceGroup(parent);
	}
	
	@Override
	protected EditorColorItem[] createEditorAppearanceColorEntries() {
		return new EditorColorItem[] {
				new EditorColorItem(
					PreferencesMessages.EditorPreferencePage_matchingBracketsHighlightColor,
					PreferenceConstants.EDITOR_MATCHING_BRACKETS_COLOR),
				new EditorColorItem(
					PreferencesMessages.EditorPreferencePage_backgroundForMethodParameters,
					PreferenceConstants.CODEASSIST_PARAMETERS_BACKGROUND),
				new EditorColorItem(
					PreferencesMessages.EditorPreferencePage_foregroundForMethodParameters,
					PreferenceConstants.CODEASSIST_PARAMETERS_FOREGROUND),
				new EditorColorItem(
					PreferencesMessages.EditorPreferencePage_backgroundForCompletionReplacement,
					PreferenceConstants.CODEASSIST_REPLACEMENT_BACKGROUND),
				new EditorColorItem(
					PreferencesMessages.EditorPreferencePage_foregroundForCompletionReplacement,
					PreferenceConstants.CODEASSIST_REPLACEMENT_FOREGROUND),
				new EditorColorItem(
					PreferencesMessages.EditorPreferencePage_sourceHoverBackgroundColor,
					PreferenceConstants.EDITOR_SOURCE_HOVER_BACKGROUND_COLOR,
					PreferenceConstants.EDITOR_SOURCE_HOVER_BACKGROUND_COLOR_SYSTEM_DEFAULT,
					SWT.COLOR_INFO_BACKGROUND) };
	}
	
}