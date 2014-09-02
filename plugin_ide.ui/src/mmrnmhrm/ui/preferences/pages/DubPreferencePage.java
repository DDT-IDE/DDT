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

import melnorme.util.swt.SWTFactoryUtil;
import mmrnmhrm.core.DeeCorePreferencesConstants;
import mmrnmhrm.ui.DeeUIMessages;
import mmrnmhrm.ui.DeeUIPlugin;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;


public class DubPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {
	
	protected static final String LABEL_MSG = 
			"The 'DUB path' control above is currently disabled due to a bug. Only the default value works.";
	
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
		dubPathEditor = new FileFieldEditor(DeeCorePreferencesConstants.PREF_DUB_PATH, 
			DeeUIMessages.DubPrefPage_fieldLabel, false, FileFieldEditor.VALIDATE_ON_KEY_STROKE, 
			getFieldEditorParent()) {
			@Override
			protected boolean checkState() {
				return true;
			}
		};
		addField(dubPathEditor);
		dubPathEditor.setEnabled(false, getFieldEditorParent());
		
		SWTFactoryUtil.createLabel(getFieldEditorParent(), SWT.NONE, LABEL_MSG, 
			GridDataFactory.swtDefaults().span(3, 1).create());
	}
	
	@Override
	protected Control createContents(Composite parent) {
		return super.createContents(parent);
	}
	
}