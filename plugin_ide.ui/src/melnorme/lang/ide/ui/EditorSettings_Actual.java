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

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.ui.services.IServiceLocator;

import melnorme.lang.ide.core.TextSettings_Actual.LangPartitionTypes;
import melnorme.lang.ide.ui.editor.LangEditorContextMenuContributor;
import melnorme.lang.ide.ui.editor.text.EditorPrefConstants_Common;
import melnorme.lang.ide.ui.text.SimpleSourceViewerConfiguration;
import melnorme.lang.ide.ui.text.coloring.StylingPreferences;
import melnorme.lang.ide.ui.text.coloring.ThemedTextStylingPreference;
import mmrnmhrm.ui.editor.DeeEditor;
import mmrnmhrm.ui.editor.DeeEditorContextMenuContributor;
import mmrnmhrm.ui.text.DeeColorPreferences;


public class EditorSettings_Actual {
	
	public static final String EDITOR_ID = LangUIPlugin.PLUGIN_ID + ".editors.DeeEditor";
	public static final String EDITOR_CONTEXT_ID = LangUIPlugin.PLUGIN_ID + ".Contexts.Editor";
	
	public static final String EDITOR_CODE_TARGET = LangUIPlugin.PLUGIN_ID + ".texteditor.deeCodeTarget";
	
	public static StylingPreferences getStylingPreferences() {
		return new StylingPreferences(
			DeeColorPreferences.DEFAULT,
			
			DeeColorPreferences.COMMENT,
			DeeColorPreferences.DOC_COMMENT,
			
			DeeColorPreferences.KEYWORDS,
			DeeColorPreferences.KW_NATIVE_TYPES,
			DeeColorPreferences.KW_LITERALS,
			DeeColorPreferences.ANNOTATIONS,
			DeeColorPreferences.OPERATORS,
			
			DeeColorPreferences.NUMBER,
			DeeColorPreferences.CHARACTER,
			DeeColorPreferences.STRING,
			DeeColorPreferences.DELIM_STRING
		);
	}
	
	public static SourceViewerConfiguration createTemplateEditorSourceViewerConfiguration(
			IPreferenceStore store, final IContentAssistProcessor templateCAP) {
		return new SimpleSourceViewerConfiguration(store) {
			@Override
			public ContentAssistant getContentAssistant(ISourceViewer sourceViewer) {
				return setupSimpleContentAssistant(templateCAP, array(
					LangPartitionTypes.DEE_CODE.getId(),
					LangPartitionTypes.DEE_MULTI_COMMENT.getId(),
					LangPartitionTypes.DEE_MULTI_DOCCOMMENT.getId(),
					LangPartitionTypes.DEE_NESTED_COMMENT.getId(),
					LangPartitionTypes.DEE_NESTED_DOCCOMMENT.getId(),
					LangPartitionTypes.DEE_SINGLE_COMMENT.getId(),
					LangPartitionTypes.DEE_SINGLE_DOCCOMMENT.getId()
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
	
	public static final ThemedTextStylingPreference CODE_DEFAULT_COLOR = DeeColorPreferences.DEFAULT;
	
	
	/* ----------------- actions ----------------- */
	
	public static interface EditorCommandIds {
		
		public static final String OpenDef_ID = LangUIPlugin.PLUGIN_ID + ".commands.openDefinition";
		
		public static final String GoToMatchingBracket = LangUIPlugin.PLUGIN_ID + ".commands.GoToMatchingBracket";
		public static final String ToggleComment = LangUIPlugin.PLUGIN_ID + ".commands.ToggleComment";
		
		public static final String QuickOutline = LangUIPlugin.PLUGIN_ID + ".commands.QuickOutline";
		public static final String Format = LangUIPlugin.PLUGIN_ID + ".commands.Format";
		
	}
	
	public static LangEditorContextMenuContributor createCommandsContribHelper(IServiceLocator svcLocator) {
		return new DeeEditorContextMenuContributor(svcLocator);
	}
	
}