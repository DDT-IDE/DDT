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

import mmrnmhrm.core.DeeCorePreferences;
import mmrnmhrm.ui.DeeUIMessages;
import mmrnmhrm.ui.DeeUIPlugin;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;


public class DubPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {
	
	public DubPreferencePage() {
		super(GRID);
	}
	
	@Override
	public void init(IWorkbench workbench) {
		setPreferenceStore(DeeUIPlugin.getInstance().getCorePreferenceStore());
	}
	
	protected FileFieldEditor dubPathEditor;
	
	@Override
	protected void createFieldEditors() {
		dubPathEditor = new FileFieldEditor(DeeCorePreferences.PREF_DUB_PATH.key, 
			DeeUIMessages.DubPrefPage_fieldLabel, false, FileFieldEditor.VALIDATE_ON_KEY_STROKE, 
			getFieldEditorParent()) {
			@Override
			protected boolean checkState() {
				return true;
			}
		};
		addField(dubPathEditor);
		
	}
	
	@Override
	protected Control createContents(Composite parent) {
		return super.createContents(parent);
	}
	
}