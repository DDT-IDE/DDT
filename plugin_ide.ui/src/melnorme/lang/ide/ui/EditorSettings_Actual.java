/*******************************************************************************
 * Copyright (c) 2014, 2014 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package melnorme.lang.ide.ui;

import mmrnmhrm.ui.editor.DeeSimpleSourceViewerConfiguration;
import mmrnmhrm.ui.editor.DeeSourceViewerConfiguration;

import org.eclipse.cdt.ui.text.IColorManager;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditor;


public class EditorSettings_Actual {
	
	public static final String EDITOR_ID = "org.dsource.ddt.ide.ui.editors.DeeEditor";
	public static final String EDITOR_CONTEXT_ID = "org.dsource.ddt.ide.ui.contexts.DeeEditor";
	
	public static final String EDITOR_CODE_TARGET = "org.dsource.ddt.ide.ui.texteditor.deeCodeTarget";
	
	public static DeeSourceViewerConfiguration createSourceViewerConfiguration(
			IPreferenceStore preferenceStore, AbstractDecoratedTextEditor editor) {
		IColorManager colorManager = LangUIPlugin.getInstance().getColorManager();
		return new DeeSourceViewerConfiguration(colorManager, preferenceStore, editor);
	}
	
	public static DeeSimpleSourceViewerConfiguration createSimpleSourceViewerConfiguration(
			IPreferenceStore preferenceStore, IColorManager colorManager) {
		return new DeeSimpleSourceViewerConfiguration(colorManager, preferenceStore, null, false);
	}
	
	/* ----------------- actions ----------------- */
	
	public static final String COMMAND_OpenDef_ID = "org.dsource.ddt.ide.ui.commands.openDefinition";
	
}