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

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;


/**
 * The root preference page for DDT 
 */
public class DeeRootPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {
	
	public DeeRootPreferencePage() {
		super(GRID);
	}
	
	@Override
	protected void createFieldEditors() {
		addField(new RadioGroupFieldEditor("label-style", "Label provider visual style", 1,
				                           new String[][] { { "JDT-style labels (methods and variables have protection dependent icons)", "jdt" },
														    { "DDT-style labels (protection level is overlayed in all cases)", "ddt" }
														  },
										   getFieldEditorParent()
				)
		);
	}	

	@Override
	public void init(IWorkbench workbench) {
		setPreferenceStore(DeePlugin.getInstance().getPreferenceStore());
		setDescription("DDT Preferences");
		getPreferenceStore().setDefault("label-style", "ddt");
	}
	
}

