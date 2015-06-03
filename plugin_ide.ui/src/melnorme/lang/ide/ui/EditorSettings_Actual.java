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

import static melnorme.utilbox.core.CoreUtil.array;
import melnorme.lang.ide.ui.editor.LangEditorContextMenuContributor;
import melnorme.lang.ide.ui.editor.text.EditorPrefConstants_Common;
import melnorme.lang.ide.ui.text.AbstractLangSourceViewerConfiguration;
import mmrnmhrm.core.text.DeePartitions;
import mmrnmhrm.ui.editor.DeeEditor;
import mmrnmhrm.ui.editor.DeeEditorContextMenuContributor;
import mmrnmhrm.ui.editor.DeeSimpleSourceViewerConfiguration;
import mmrnmhrm.ui.editor.DeeSourceViewerConfiguration;
import mmrnmhrm.ui.text.DeeColorPreferences;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.ui.services.IServiceLocator;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditor;

import _org.eclipse.cdt.ui.text.IColorManager;


public class EditorSettings_Actual {
	
	public static final String EDITOR_ID = LangUIPlugin.PLUGIN_ID + ".editors.DeeEditor";
	public static final String EDITOR_CONTEXT_ID = LangUIPlugin.PLUGIN_ID + ".Contexts.Editor";
	
	public static final String EDITOR_CODE_TARGET = LangUIPlugin.PLUGIN_ID + ".texteditor.deeCodeTarget";
	
	public static DeeSimpleSourceViewerConfiguration createSimpleSourceViewerConfiguration(
			IPreferenceStore preferenceStore, IColorManager colorManager) {
		return new DeeSimpleSourceViewerConfiguration(colorManager, preferenceStore, false);
	}
	
	public static AbstractLangSourceViewerConfiguration createSourceViewerConfiguration(
			IPreferenceStore preferenceStore, AbstractDecoratedTextEditor editor) {
		IColorManager colorManager = LangUIPlugin.getInstance().getColorManager();
		return new DeeSourceViewerConfiguration(colorManager, preferenceStore, editor);
	}
	
	public static SourceViewerConfiguration createTemplateEditorSourceViewerConfiguration(
			IPreferenceStore store, final IContentAssistProcessor templateCAP) {
		IColorManager colorManager = LangUIPlugin.getInstance().getColorManager();
		return new DeeSimpleSourceViewerConfiguration(colorManager, store, false) {
			@Override
			public ContentAssistant getContentAssistant(ISourceViewer sourceViewer) {
				return setupSimpleContentAssistant(templateCAP, array(
					DeePartitions.DEE_CODE,
					DeePartitions.DEE_MULTI_COMMENT,
					DeePartitions.DEE_MULTI_DOCCOMMENT,
					DeePartitions.DEE_NESTED_COMMENT,
					DeePartitions.DEE_NESTED_DOCCOMMENT,
					DeePartitions.DEE_SINGLE_COMMENT,
					DeePartitions.DEE_SINGLE_DOCCOMMENT
					));
			}
		};
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
		
		public static final String QuickOutline = LangUIPlugin.PLUGIN_ID + ".commands.QuickOutline";
		
	}
	
	public static LangEditorContextMenuContributor createCommandsContribHelper(IServiceLocator svcLocator) {
		return new DeeEditorContextMenuContributor(svcLocator);
	}
	
}