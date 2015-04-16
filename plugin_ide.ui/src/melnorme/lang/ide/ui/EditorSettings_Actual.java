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
import melnorme.lang.ide.ui.editor.text.EditorPrefConstants_Common;
import melnorme.lang.ide.ui.text.AbstractLangSourceViewerConfiguration;
import mmrnmhrm.ui.editor.DeeEditor;
import mmrnmhrm.ui.editor.DeeEditorContextMenuContributor;
import mmrnmhrm.ui.editor.DeeSimpleSourceViewerConfiguration;
import mmrnmhrm.ui.editor.DeeSourceViewerConfiguration;
import mmrnmhrm.ui.text.DeeColorPreferences;

import org.eclipse.cdt.ui.text.IColorManager;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.services.IServiceLocator;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditor;


public class EditorSettings_Actual {
	
	public static final String EDITOR_ID = LangUIPlugin.PLUGIN_ID + ".editors.DeeEditor";
	public static final String EDITOR_CONTEXT_ID = LangUIPlugin.PLUGIN_ID + ".Contexts.Editor";
	
	public static final String EDITOR_CODE_TARGET = LangUIPlugin.PLUGIN_ID + ".texteditor.deeCodeTarget";
	
	public static DeeSimpleSourceViewerConfiguration createSimpleSourceViewerConfiguration(
			IPreferenceStore preferenceStore, IColorManager colorManager) {
		return new DeeSimpleSourceViewerConfiguration(colorManager, preferenceStore, null, false);
	}
	
	public static AbstractLangSourceViewerConfiguration createSourceViewerConfiguration(
			IPreferenceStore preferenceStore, AbstractDecoratedTextEditor editor) {
		IColorManager colorManager = LangUIPlugin.getInstance().getColorManager();
		return new DeeSourceViewerConfiguration(colorManager, preferenceStore, editor);
	}
	
	public static Class<DeeEditor> editorKlass() {
		return DeeEditor.class;
	}
	
	public static interface EditorPrefConstants extends EditorPrefConstants_Common {
		
	}
	
	public static final String TEMPLATE_CONTEXT_TYPE_ID = "DeeUniversalTemplateContextType";
	
	public static final String CODE_DEFAULT_COLOR = DeeColorPreferences.DEFAULT.key;
	
	
	/* ----------------- actions ----------------- */
	
	public static interface EditorCommandIds {
		
		public static final String OpenDef_ID = LangUIPlugin.PLUGIN_ID + ".commands.openDefinition";
		
		public static final String GoToMatchingBracket = LangUIPlugin.PLUGIN_ID + ".commands.GoToMatchingBracket";
		public static final String ToggleComment = LangUIPlugin.PLUGIN_ID + ".commands.ToggleComment";
		
	}
	
	public static LangEditorContextMenuContributor createCommandsContribHelper(IServiceLocator svcLocator) {
		return new DeeEditorContextMenuContributor(svcLocator);
	}
	
}