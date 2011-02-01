package mmrnmhrm.ui;

import mmrnmhrm.ui.text.color.DeeColorPreferenceInitializer;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.dltk.ui.CodeFormatterConstants;
import org.eclipse.dltk.ui.PreferenceConstants;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.ui.editors.text.EditorsUI;

import dtool.IDeeDocColorConstants;

public class DeeUIPreferenceInitializer extends AbstractPreferenceInitializer {
	
	// Extension point entry point
	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore store = DeePlugin.getInstance().getPreferenceStore();

		initializeDefaultValues(store);
	}

	private void initializeDefaultValues(IPreferenceStore store) {
		EditorsUI.useAnnotationsPreferencePage(store);
		EditorsUI.useQuickDiffPreferencePage(store);
		
		// DTLK default init
		PreferenceConstants.initializeDefaultValues(store);
	
		DeeColorPreferenceInitializer.initializeDefaults(store);
		DeeDoc_initializeDefaultValues(store);
		
		store.setDefault(PreferenceConstants.CODEASSIST_AUTOACTIVATION_TRIGGERS, ".");
		store.setDefault(PreferenceConstants.CODEASSIST_AUTOINSERT, true);
		store.setDefault(PreferenceConstants.CODEASSIST_PREFIX_COMPLETION, true);
		
		store.setDefault(PreferenceConstants.EDITOR_SMART_INDENT, true);
		store.setDefault(PreferenceConstants.EDITOR_TAB_ALWAYS_INDENT, true);
		store.setDefault(PreferenceConstants.EDITOR_CLOSE_STRINGS, true);
		store.setDefault(PreferenceConstants.EDITOR_CLOSE_BRACKETS, true);
		store.setDefault(PreferenceConstants.EDITOR_CLOSE_BRACES, true);
		store.setDefault(PreferenceConstants.EDITOR_SMART_TAB, true);
		store.setDefault(PreferenceConstants.EDITOR_SMART_PASTE, true);
		store.setDefault(PreferenceConstants.EDITOR_SMART_HOME_END, true);
		store.setDefault(PreferenceConstants.EDITOR_SUB_WORD_NAVIGATION, true);		
		store.setDefault(PreferenceConstants.EDITOR_TAB_WIDTH, 4);
		store.setDefault(PreferenceConstants.EDITOR_SYNC_OUTLINE_ON_CURSOR_MOVE, true);
		
		// folding
		store.setDefault(PreferenceConstants.EDITOR_FOLDING_ENABLED, true);
		store.setDefault(PreferenceConstants.EDITOR_COMMENTS_FOLDING_ENABLED, true);		
		store.setDefault(PreferenceConstants.SEARCH_USE_REDUCED_MENU, true);

		
		store.setDefault(CodeFormatterConstants.FORMATTER_TAB_CHAR, CodeFormatterConstants.TAB);
		store.setDefault(CodeFormatterConstants.FORMATTER_INDENTATION_SIZE, 4);
		store.setDefault(CodeFormatterConstants.FORMATTER_TAB_SIZE, 4);

		/*
		store.setDefault(APPEARANCE_COMPRESS_PACKAGE_NAMES, false);
		store.setDefault(APPEARANCE_METHOD_RETURNTYPE, false);
		store.setDefault(APPEARANCE_METHOD_TYPEPARAMETERS, true);
		store.setDefault(APPEARANCE_PKG_NAME_PATTERN_FOR_PKG_VIEW, ""); //$NON-NLS-1$

		store.setDefault(SHOW_SOURCE_MODULE_CHILDREN, true);

		store.setDefault(CODEASSIST_AUTOACTIVATION_TRIGGERS, ".:$@"); //$NON-NLS-1$
		 */

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



