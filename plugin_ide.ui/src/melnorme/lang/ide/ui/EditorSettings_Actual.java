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

import melnorme.lang.ide.ui.editor.LangEditorContextMenuContributor;
import mmrnmhrm.ui.editor.DeeEditor;
import mmrnmhrm.ui.editor.DeeEditorContextMenuContributor;
import mmrnmhrm.ui.editor.DeeSimpleSourceViewerConfiguration;
import mmrnmhrm.ui.editor.DeeSourceViewerConfiguration;

import org.eclipse.cdt.ui.text.IColorManager;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.services.IServiceLocator;
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
	
	public static Class<DeeEditor> editorKlass() {
		return DeeEditor.class;
	}
	
	
	/* ----------------- actions ----------------- */
	
	public static interface EditorCommandIds {
		
		public static final String OpenDef_ID = "org.dsource.ddt.ide.ui.commands.openDefinition";
		
		public static final String GoToMatchingBracket = "LANG_PROJECT_ID.ide.ui.commands.GoToMatchingBracket";
		public static final String ToggleComment = "LANG_PROJECT_ID.ide.ui.commands.ToggleComment";
		
	}
	
	public static LangEditorContextMenuContributor createCommandsContribHelper(IServiceLocator svcLocator) {
		return new DeeEditorContextMenuContributor(svcLocator);
	}
	
}
