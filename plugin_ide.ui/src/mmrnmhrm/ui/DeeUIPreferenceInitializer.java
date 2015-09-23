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

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.ui.editors.text.EditorsUI;

import _org.eclipse.dltk.ui.PreferenceConstants;
import dtool.ddoc.IDeeDocColorConstants;
import melnorme.lang.ide.ui.CodeFormatterConstants_Actual;
import melnorme.lang.ide.ui.LangAutoEditPreferenceConstants;
import melnorme.lang.ide.ui.LangUIPreferencesInitializer;
import melnorme.lang.ide.ui.text.coloring.TextColoringConstants;
import melnorme.utilbox.misc.MiscUtil;
import mmrnmhrm.ui.editor.folding.DeeFoldingPreferenceConstants;

public class DeeUIPreferenceInitializer extends LangUIPreferencesInitializer {
	
	@Override
	protected void initializeDefaultPreferences_other(IPreferenceStore langUIStore) {
		
		IPreferenceStore store = langUIStore;
		
		EditorsUI.useAnnotationsPreferencePage(store);
		EditorsUI.useQuickDiffPreferencePage(store);
		
		DeeDoc_initializeDefaultValues(store);
		
		
		// Formatter
		MiscUtil.loadClass(CodeFormatterConstants_Actual.class);
		
		MiscUtil.loadClass(LangAutoEditPreferenceConstants.class);
		
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
		
		
	}
	
	public static void DeeDoc_initializeDefaultValues(IPreferenceStore store) {
		store.setDefault(PreferenceConstants.EDITOR_SHOW_SEGMENTS, false);
		
		// DdocPreferencePage
		//store.setDefault(PreferenceConstants.DDOC_SHOW_PARAMETER_TYPES, false);
		
		PreferenceConverter.setDefault(store, IDeeDocColorConstants.JAVA_MULTI_LINE_COMMENT, new RGB(63, 127, 95));
		store.setDefault(IDeeDocColorConstants.JAVA_MULTI_LINE_COMMENT + TextColoringConstants.EDITOR_BOLD_SUFFIX, false);
		store.setDefault(IDeeDocColorConstants.JAVA_MULTI_LINE_COMMENT + TextColoringConstants.EDITOR_ITALIC_SUFFIX, false);
		
		PreferenceConverter.setDefault(store, IDeeDocColorConstants.JAVA_MULTI_LINE_PLUS_COMMENT, new RGB(63, 127, 95));
		store.setDefault(IDeeDocColorConstants.JAVA_MULTI_LINE_PLUS_COMMENT + TextColoringConstants.EDITOR_BOLD_SUFFIX, false);
		store.setDefault(IDeeDocColorConstants.JAVA_MULTI_LINE_PLUS_COMMENT + TextColoringConstants.EDITOR_ITALIC_SUFFIX, false);
		
		PreferenceConverter.setDefault(store, IDeeDocColorConstants.JAVA_MULTI_LINE_PLUS_DOC_COMMENT, new RGB(63, 95, 191));
		store.setDefault(IDeeDocColorConstants.JAVA_MULTI_LINE_PLUS_DOC_COMMENT + TextColoringConstants.EDITOR_BOLD_SUFFIX, false);
		store.setDefault(IDeeDocColorConstants.JAVA_MULTI_LINE_PLUS_DOC_COMMENT + TextColoringConstants.EDITOR_ITALIC_SUFFIX, false);
		
		PreferenceConverter.setDefault(store, IDeeDocColorConstants.JAVA_SINGLE_LINE_COMMENT, new RGB(63, 127, 95));
		store.setDefault(IDeeDocColorConstants.JAVA_SINGLE_LINE_COMMENT + TextColoringConstants.EDITOR_BOLD_SUFFIX, false);
		store.setDefault(IDeeDocColorConstants.JAVA_SINGLE_LINE_COMMENT + TextColoringConstants.EDITOR_ITALIC_SUFFIX, false);
		
		PreferenceConverter.setDefault(store, IDeeDocColorConstants.JAVA_SINGLE_LINE_DOC_COMMENT, new RGB(63, 95, 191));
		store.setDefault(IDeeDocColorConstants.JAVA_SINGLE_LINE_DOC_COMMENT + TextColoringConstants.EDITOR_BOLD_SUFFIX, false);
		store.setDefault(IDeeDocColorConstants.JAVA_SINGLE_LINE_DOC_COMMENT + TextColoringConstants.EDITOR_ITALIC_SUFFIX, false);
		
		PreferenceConverter.setDefault(store, IDeeDocColorConstants.JAVA_KEYWORD, new RGB(127, 0, 85));
		store.setDefault(IDeeDocColorConstants.JAVA_KEYWORD + TextColoringConstants.EDITOR_BOLD_SUFFIX, true);
		store.setDefault(IDeeDocColorConstants.JAVA_KEYWORD + TextColoringConstants.EDITOR_ITALIC_SUFFIX, false);
		
		PreferenceConverter.setDefault(store, IDeeDocColorConstants.JAVA_STRING, new RGB(42, 0, 255));
		store.setDefault(IDeeDocColorConstants.JAVA_STRING + TextColoringConstants.EDITOR_BOLD_SUFFIX, false);
		store.setDefault(IDeeDocColorConstants.JAVA_STRING + TextColoringConstants.EDITOR_ITALIC_SUFFIX, false);
		
		PreferenceConverter.setDefault(store, IDeeDocColorConstants.JAVA_PRAGMA, new RGB(100, 100, 100));
		store.setDefault(IDeeDocColorConstants.JAVA_PRAGMA + TextColoringConstants.EDITOR_BOLD_SUFFIX, false);
		store.setDefault(IDeeDocColorConstants.JAVA_PRAGMA + TextColoringConstants.EDITOR_ITALIC_SUFFIX, false);
		
		PreferenceConverter.setDefault(store, IDeeDocColorConstants.JAVA_DEFAULT, new RGB(0, 0, 0));
		store.setDefault(IDeeDocColorConstants.JAVA_DEFAULT + TextColoringConstants.EDITOR_BOLD_SUFFIX, false);
		store.setDefault(IDeeDocColorConstants.JAVA_DEFAULT + TextColoringConstants.EDITOR_ITALIC_SUFFIX, false);
		
		PreferenceConverter.setDefault(store, IDeeDocColorConstants.JAVA_KEYWORD_RETURN, new RGB(127, 0, 85));
		store.setDefault(IDeeDocColorConstants.JAVA_KEYWORD_RETURN + TextColoringConstants.EDITOR_BOLD_SUFFIX, true);
		store.setDefault(IDeeDocColorConstants.JAVA_KEYWORD_RETURN + TextColoringConstants.EDITOR_ITALIC_SUFFIX, false);
		
		PreferenceConverter.setDefault(store, IDeeDocColorConstants.JAVA_SPECIAL_TOKEN, new RGB(100, 100, 100));
		store.setDefault(IDeeDocColorConstants.JAVA_SPECIAL_TOKEN + TextColoringConstants.EDITOR_BOLD_SUFFIX, false);
		store.setDefault(IDeeDocColorConstants.JAVA_SPECIAL_TOKEN + TextColoringConstants.EDITOR_ITALIC_SUFFIX, true);
		
		PreferenceConverter.setDefault(store, IDeeDocColorConstants.JAVA_OPERATOR, new RGB(0, 0, 0));
		store.setDefault(IDeeDocColorConstants.JAVA_OPERATOR + TextColoringConstants.EDITOR_BOLD_SUFFIX, false);
		store.setDefault(IDeeDocColorConstants.JAVA_OPERATOR + TextColoringConstants.EDITOR_ITALIC_SUFFIX, false);
		
		PreferenceConverter.setDefault(store, IDeeDocColorConstants.TASK_TAG, new RGB(127, 159, 191));
		store.setDefault(IDeeDocColorConstants.TASK_TAG + TextColoringConstants.EDITOR_BOLD_SUFFIX, true);
		store.setDefault(IDeeDocColorConstants.TASK_TAG + TextColoringConstants.EDITOR_ITALIC_SUFFIX, false);
		
		PreferenceConverter.setDefault(store, IDeeDocColorConstants.JAVADOC_KEYWORD, new RGB(127, 159, 191));
		store.setDefault(IDeeDocColorConstants.JAVADOC_KEYWORD + TextColoringConstants.EDITOR_BOLD_SUFFIX, true);
		store.setDefault(IDeeDocColorConstants.JAVADOC_KEYWORD + TextColoringConstants.EDITOR_ITALIC_SUFFIX, false);
		
		PreferenceConverter.setDefault(store, IDeeDocColorConstants.JAVADOC_TAG, new RGB(127, 127, 159));
		store.setDefault(IDeeDocColorConstants.JAVADOC_TAG + TextColoringConstants.EDITOR_BOLD_SUFFIX, false);
		store.setDefault(IDeeDocColorConstants.JAVADOC_TAG + TextColoringConstants.EDITOR_ITALIC_SUFFIX, false);
		
		PreferenceConverter.setDefault(store, IDeeDocColorConstants.JAVADOC_LINK, new RGB(63, 63, 191));
		store.setDefault(IDeeDocColorConstants.JAVADOC_LINK + TextColoringConstants.EDITOR_BOLD_SUFFIX, false);
		store.setDefault(IDeeDocColorConstants.JAVADOC_LINK + TextColoringConstants.EDITOR_ITALIC_SUFFIX, false);
		
		PreferenceConverter.setDefault(store, IDeeDocColorConstants.JAVADOC_DEFAULT, new RGB(63, 95, 191));
		store.setDefault(IDeeDocColorConstants.JAVADOC_DEFAULT + TextColoringConstants.EDITOR_BOLD_SUFFIX, false);
		store.setDefault(IDeeDocColorConstants.JAVADOC_DEFAULT + TextColoringConstants.EDITOR_ITALIC_SUFFIX, false);
		
	}
	
}