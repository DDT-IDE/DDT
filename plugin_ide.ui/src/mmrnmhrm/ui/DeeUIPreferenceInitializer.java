/*******************************************************************************
 * Copyright (c) 2007, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package mmrnmhrm.ui;

import melnorme.lang.ide.ui.LangUIPlugin;
import melnorme.lang.ide.ui.editor.text.LangAutoEditPreferenceConstants;
import mmrnmhrm.ui.editor.folding.DeeFoldingPreferenceConstants;
import mmrnmhrm.ui.text.color.DeeColorPreferenceInitializer;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.dltk.ui.CodeFormatterConstants;
import org.eclipse.dltk.ui.PreferenceConstants;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.ui.editors.text.EditorsUI;

import dtool.ddoc.IDeeDocColorConstants;

public class DeeUIPreferenceInitializer extends AbstractPreferenceInitializer {
	
	// Extension point entry point
	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore store = LangUIPlugin.getInstance().getPreferenceStore();
		
		EditorsUI.useAnnotationsPreferencePage(store);
		EditorsUI.useQuickDiffPreferencePage(store);
		
		// DTLK default init
		PreferenceConstants.initializeDefaultValues(store);
		// Fix DLTK bug
		store.setDefault(PreferenceConstants.EDITOR_SOURCE_HOVER_BACKGROUND_COLOR_SYSTEM_DEFAULT, true);
		
		DeeColorPreferenceInitializer.initializeDefaults(store);
		DeeDoc_initializeDefaultValues(store);
		
		// Explorer
		store.setDefault(PreferenceConstants.SHOW_SOURCE_MODULE_CHILDREN, true); //Doesn't seem to be working in 3.0
		
		// Appearance
		store.setDefault(PreferenceConstants.APPEARANCE_COMPRESS_PACKAGE_NAMES, false);
		store.setDefault(PreferenceConstants.APPEARANCE_METHOD_RETURNTYPE, true);
		store.setDefault(PreferenceConstants.APPEARANCE_METHOD_TYPEPARAMETERS, true);
		// BM: I don't know how the next preference works:
		//store.setDefault(PreferenceConstants.APPEARANCE_PKG_NAME_PATTERN_FOR_PKG_VIEW, ""); //$NON-NLS-1$
		store.setDefault(DeeUIPreferenceConstants.ELEMENT_ICONS_STYLE, ""); // No specific default
		
		// Formatter
		store.setDefault(CodeFormatterConstants.FORMATTER_TAB_CHAR, CodeFormatterConstants.TAB);
		store.setDefault(CodeFormatterConstants.FORMATTER_INDENTATION_SIZE, 4);
		store.setDefault(CodeFormatterConstants.FORMATTER_TAB_SIZE, 4);
		
		// Editor
		store.setDefault(PreferenceConstants.EDITOR_SMART_INDENT, true);
		store.setDefault(PreferenceConstants.EDITOR_SMART_HOME_END, true);
		store.setDefault(PreferenceConstants.EDITOR_SUB_WORD_NAVIGATION, true);		
		store.setDefault(PreferenceConstants.EDITOR_TAB_ALWAYS_INDENT, true);
		store.setDefault(PreferenceConstants.EDITOR_TAB_WIDTH, 4);
		// TODO: there is cleanup redundancy in tab preferences keys
		
		
		store.setDefault(LangAutoEditPreferenceConstants.AE_CLOSE_STRINGS, true);
		store.setDefault(LangAutoEditPreferenceConstants.AE_CLOSE_BRACES, true);
		store.setDefault(LangAutoEditPreferenceConstants.AE_CLOSE_BRACKETS, true);
		
		store.setDefault(LangAutoEditPreferenceConstants.AE_SMART_INDENT, true);
		store.setDefault(LangAutoEditPreferenceConstants.AE_SMART_DEINDENT, true);
		store.setDefault(LangAutoEditPreferenceConstants.AE_PARENTHESES_AS_BLOCKS, true);
		
		store.setDefault(PreferenceConstants.EDITOR_SMART_TAB, true); // Not used currently
		store.setDefault(LangAutoEditPreferenceConstants.AE_SMART_PASTE, true); // Not used currently
		
		
		store.setDefault(PreferenceConstants.EDITOR_SYNC_OUTLINE_ON_CURSOR_MOVE, true);
		
		// Content assist
		store.setDefault(PreferenceConstants.CODEASSIST_INSERT_COMPLETION, false); // CA overwrites by default
		store.setDefault(PreferenceConstants.CODEASSIST_PREFIX_COMPLETION, true);
		store.setDefault(PreferenceConstants.CODEASSIST_AUTOINSERT, true);
		store.setDefault(PreferenceConstants.CODEASSIST_AUTOACTIVATION_TRIGGERS, ".");
		
		
		// Folding
		store.setDefault(PreferenceConstants.EDITOR_FOLDING_ENABLED, true);
		store.setDefault(PreferenceConstants.EDITOR_FOLDING_LINES_LIMIT, 2);
		store.setDefault(PreferenceConstants.EDITOR_COMMENTS_FOLDING_ENABLED, true);
		store.setDefault(PreferenceConstants.EDITOR_DOCS_FOLDING_ENABLED, true);
		store.setDefault(PreferenceConstants.EDITOR_COMMENT_FOLDING_JOIN_NEWLINES, false);
		
		store.setDefault(PreferenceConstants.EDITOR_FOLDING_INIT_COMMENTS, false);
		store.setDefault(PreferenceConstants.EDITOR_FOLDING_INIT_HEADER_COMMENTS, true);
		store.setDefault(PreferenceConstants.EDITOR_FOLDING_INIT_DOCS, false);
		store.setDefault(PreferenceConstants.EDITOR_FOLDING_INIT_METHODS, false);
		store.setDefault(PreferenceConstants.EDITOR_FOLDING_INIT_CLASSES, false);
		store.setDefault(DeeFoldingPreferenceConstants.EDITOR_STRINGS_FOLDING_ENABLED, true); // Not used much ATM
		store.setDefault(DeeFoldingPreferenceConstants.EDITOR_FOLDING_INIT_STRINGS, false); // Not used much ATM
		store.setDefault(DeeFoldingPreferenceConstants.EDITOR_FOLDING_INIT_UNITTESTS, true);
		store.setDefault(DeeFoldingPreferenceConstants.EDITOR_FOLDING_INIT_CONDITIONALS, false);
		store.setDefault(DeeFoldingPreferenceConstants.EDITOR_FOLDING_INIT_FUNCTIONLITERALS, false);
		store.setDefault(DeeFoldingPreferenceConstants.EDITOR_FOLDING_INIT_ANONCLASSES, false);
		
		
		// Search
		store.setDefault(PreferenceConstants.SEARCH_USE_REDUCED_MENU, true);

		// WIZARDS
		store.setDefault(PreferenceConstants.SRCBIN_FOLDERS_IN_NEWPROJ, true);
		store.setDefault(PreferenceConstants.SRC_SRCNAME, "src"); //$NON-NLS-1$		
		
	}
	
	public static void DeeDoc_initializeDefaultValues(IPreferenceStore store) {
		store.setDefault(PreferenceConstants.EDITOR_SHOW_SEGMENTS, false);
		
		// DdocPreferencePage
		//store.setDefault(PreferenceConstants.DDOC_SHOW_PARAMETER_TYPES, false);
		
		PreferenceConverter.setDefault(store, IDeeDocColorConstants.JAVA_MULTI_LINE_COMMENT, new RGB(63, 127, 95));
		store.setDefault(IDeeDocColorConstants.JAVA_MULTI_LINE_COMMENT + PreferenceConstants.EDITOR_BOLD_SUFFIX, false);
		store.setDefault(IDeeDocColorConstants.JAVA_MULTI_LINE_COMMENT + PreferenceConstants.EDITOR_ITALIC_SUFFIX, false);
		
		PreferenceConverter.setDefault(store, IDeeDocColorConstants.JAVA_MULTI_LINE_PLUS_COMMENT, new RGB(63, 127, 95));
		store.setDefault(IDeeDocColorConstants.JAVA_MULTI_LINE_PLUS_COMMENT + PreferenceConstants.EDITOR_BOLD_SUFFIX, false);
		store.setDefault(IDeeDocColorConstants.JAVA_MULTI_LINE_PLUS_COMMENT + PreferenceConstants.EDITOR_ITALIC_SUFFIX, false);
		
		PreferenceConverter.setDefault(store, IDeeDocColorConstants.JAVA_MULTI_LINE_PLUS_DOC_COMMENT, new RGB(63, 95, 191));
		store.setDefault(IDeeDocColorConstants.JAVA_MULTI_LINE_PLUS_DOC_COMMENT + PreferenceConstants.EDITOR_BOLD_SUFFIX, false);
		store.setDefault(IDeeDocColorConstants.JAVA_MULTI_LINE_PLUS_DOC_COMMENT + PreferenceConstants.EDITOR_ITALIC_SUFFIX, false);
		
		PreferenceConverter.setDefault(store, IDeeDocColorConstants.JAVA_SINGLE_LINE_COMMENT, new RGB(63, 127, 95));
		store.setDefault(IDeeDocColorConstants.JAVA_SINGLE_LINE_COMMENT + PreferenceConstants.EDITOR_BOLD_SUFFIX, false);
		store.setDefault(IDeeDocColorConstants.JAVA_SINGLE_LINE_COMMENT + PreferenceConstants.EDITOR_ITALIC_SUFFIX, false);
		
		PreferenceConverter.setDefault(store, IDeeDocColorConstants.JAVA_SINGLE_LINE_DOC_COMMENT, new RGB(63, 95, 191));
		store.setDefault(IDeeDocColorConstants.JAVA_SINGLE_LINE_DOC_COMMENT + PreferenceConstants.EDITOR_BOLD_SUFFIX, false);
		store.setDefault(IDeeDocColorConstants.JAVA_SINGLE_LINE_DOC_COMMENT + PreferenceConstants.EDITOR_ITALIC_SUFFIX, false);
		
		PreferenceConverter.setDefault(store, IDeeDocColorConstants.JAVA_KEYWORD, new RGB(127, 0, 85));
		store.setDefault(IDeeDocColorConstants.JAVA_KEYWORD + PreferenceConstants.EDITOR_BOLD_SUFFIX, true);
		store.setDefault(IDeeDocColorConstants.JAVA_KEYWORD + PreferenceConstants.EDITOR_ITALIC_SUFFIX, false);
		
		PreferenceConverter.setDefault(store, IDeeDocColorConstants.JAVA_STRING, new RGB(42, 0, 255));
		store.setDefault(IDeeDocColorConstants.JAVA_STRING + PreferenceConstants.EDITOR_BOLD_SUFFIX, false);
		store.setDefault(IDeeDocColorConstants.JAVA_STRING + PreferenceConstants.EDITOR_ITALIC_SUFFIX, false);
		
		PreferenceConverter.setDefault(store, IDeeDocColorConstants.JAVA_PRAGMA, new RGB(100, 100, 100));
		store.setDefault(IDeeDocColorConstants.JAVA_PRAGMA + PreferenceConstants.EDITOR_BOLD_SUFFIX, false);
		store.setDefault(IDeeDocColorConstants.JAVA_PRAGMA + PreferenceConstants.EDITOR_ITALIC_SUFFIX, false);
		
		PreferenceConverter.setDefault(store, IDeeDocColorConstants.JAVA_DEFAULT, new RGB(0, 0, 0));
		store.setDefault(IDeeDocColorConstants.JAVA_DEFAULT + PreferenceConstants.EDITOR_BOLD_SUFFIX, false);
		store.setDefault(IDeeDocColorConstants.JAVA_DEFAULT + PreferenceConstants.EDITOR_ITALIC_SUFFIX, false);
		
		PreferenceConverter.setDefault(store, IDeeDocColorConstants.JAVA_KEYWORD_RETURN, new RGB(127, 0, 85));
		store.setDefault(IDeeDocColorConstants.JAVA_KEYWORD_RETURN + PreferenceConstants.EDITOR_BOLD_SUFFIX, true);
		store.setDefault(IDeeDocColorConstants.JAVA_KEYWORD_RETURN + PreferenceConstants.EDITOR_ITALIC_SUFFIX, false);
		
		PreferenceConverter.setDefault(store, IDeeDocColorConstants.JAVA_SPECIAL_TOKEN, new RGB(100, 100, 100));
		store.setDefault(IDeeDocColorConstants.JAVA_SPECIAL_TOKEN + PreferenceConstants.EDITOR_BOLD_SUFFIX, false);
		store.setDefault(IDeeDocColorConstants.JAVA_SPECIAL_TOKEN + PreferenceConstants.EDITOR_ITALIC_SUFFIX, true);
		
		PreferenceConverter.setDefault(store, IDeeDocColorConstants.JAVA_OPERATOR, new RGB(0, 0, 0));
		store.setDefault(IDeeDocColorConstants.JAVA_OPERATOR + PreferenceConstants.EDITOR_BOLD_SUFFIX, false);
		store.setDefault(IDeeDocColorConstants.JAVA_OPERATOR + PreferenceConstants.EDITOR_ITALIC_SUFFIX, false);
		
		PreferenceConverter.setDefault(store, IDeeDocColorConstants.TASK_TAG, new RGB(127, 159, 191));
		store.setDefault(IDeeDocColorConstants.TASK_TAG + PreferenceConstants.EDITOR_BOLD_SUFFIX, true);
		store.setDefault(IDeeDocColorConstants.TASK_TAG + PreferenceConstants.EDITOR_ITALIC_SUFFIX, false);
		
		PreferenceConverter.setDefault(store, IDeeDocColorConstants.JAVADOC_KEYWORD, new RGB(127, 159, 191));
		store.setDefault(IDeeDocColorConstants.JAVADOC_KEYWORD + PreferenceConstants.EDITOR_BOLD_SUFFIX, true);
		store.setDefault(IDeeDocColorConstants.JAVADOC_KEYWORD + PreferenceConstants.EDITOR_ITALIC_SUFFIX, false);
		
		PreferenceConverter.setDefault(store, IDeeDocColorConstants.JAVADOC_TAG, new RGB(127, 127, 159));
		store.setDefault(IDeeDocColorConstants.JAVADOC_TAG + PreferenceConstants.EDITOR_BOLD_SUFFIX, false);
		store.setDefault(IDeeDocColorConstants.JAVADOC_TAG + PreferenceConstants.EDITOR_ITALIC_SUFFIX, false);
		
		PreferenceConverter.setDefault(store, IDeeDocColorConstants.JAVADOC_LINK, new RGB(63, 63, 191));
		store.setDefault(IDeeDocColorConstants.JAVADOC_LINK + PreferenceConstants.EDITOR_BOLD_SUFFIX, false);
		store.setDefault(IDeeDocColorConstants.JAVADOC_LINK + PreferenceConstants.EDITOR_ITALIC_SUFFIX, false);
		
		PreferenceConverter.setDefault(store, IDeeDocColorConstants.JAVADOC_DEFAULT, new RGB(63, 95, 191));
		store.setDefault(IDeeDocColorConstants.JAVADOC_DEFAULT + PreferenceConstants.EDITOR_BOLD_SUFFIX, false);
		store.setDefault(IDeeDocColorConstants.JAVADOC_DEFAULT + PreferenceConstants.EDITOR_ITALIC_SUFFIX, false);
		
	}
	
}