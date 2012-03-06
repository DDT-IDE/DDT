/*******************************************************************************
 * Copyright (c) 2008, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package mmrnmhrm.ui.preferences.pages;

import mmrnmhrm.ui.DeePlugin;
import mmrnmhrm.ui.DeeUIPreferenceConstants;

import org.eclipse.dltk.ui.PreferenceConstants;
import org.eclipse.dltk.ui.viewsupport.AppearanceAwareLabelProvider;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;


/**
 * The root preference page for DDT 
 */
public class DeeAppearancePreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {
	
	private static final String LABEL_PROVIDER_STYLE = 
			"Icon style for D elements in viewers:";
	private static final String LABEL_PROVIDER_STYLE_DDT = 
			"DDT default style (protection is overlayed for all element kinds)";
	private static final String LABEL_PROVIDER_STYLE_JDT = 
			"JDT style (methods and variables have protection dependent base icons)";

	public DeeAppearancePreferencePage() {
		super(GRID);
	}
	
	@Override
	public void init(IWorkbench workbench) {
		setPreferenceStore(DeePlugin.getInstance().getPreferenceStore());
	}
	
	@Override
	protected void createFieldEditors() {
		String[][] labelAndValues = new String[][] { 
				{ LABEL_PROVIDER_STYLE_DDT, DeeUIPreferenceConstants.ElementIconsStyle.DDT.toString() },
				{ LABEL_PROVIDER_STYLE_JDT, DeeUIPreferenceConstants.ElementIconsStyle.JDTLIKE.toString() },
		};
		RadioGroupFieldEditor editor = new RadioGroupFieldEditor(
				DeeUIPreferenceConstants.ELEMENT_ICONS_STYLE, LABEL_PROVIDER_STYLE, 1, 
				labelAndValues, getFieldEditorParent());
		addField(editor);
	}
	
	@Override
	public boolean performOk() {
		boolean performOk = super.performOk();
		refreshViewers();
		return performOk;
	}
	
	/** Triggers a refresh on  viewers with model element label providers. 
	 * (Uses a workaround to trigger refresh in {@link AppearanceAwareLabelProvider} ) */
	protected void refreshViewers() {
		IPreferenceStore prefStore = DeePlugin.getInstance().getPreferenceStore();
		String value = prefStore.getString(PreferenceConstants.APPEARANCE_METHOD_RETURNTYPE);
		prefStore.firePropertyChangeEvent(PreferenceConstants.APPEARANCE_METHOD_RETURNTYPE, value, value);
	}
}
