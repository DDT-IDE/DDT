package melnorme.lang.ide.ui;

import melnorme.lang.ide.core.utils.prefs.IntPreference;
import melnorme.lang.ide.core.utils.prefs.StringPreference;


public interface CodeFormatterConstants_Actual {
	
	public static final String QUALIFIER = LangUIPlugin.PLUGIN_ID;
	
	public static final String TAB = org.eclipse.dltk.ui.CodeFormatterConstants.TAB;
	public static final String SPACES = org.eclipse.dltk.ui.CodeFormatterConstants.SPACE;
	public static final String MIXED = org.eclipse.dltk.ui.CodeFormatterConstants.MIXED;
	
	
	StringPreference FORMATTER_INDENT_MODE = 
			new StringPreference(QUALIFIER, org.eclipse.dltk.ui.CodeFormatterConstants.FORMATTER_TAB_CHAR, TAB);
	IntPreference FORMATTER_TAB_SIZE = 
			new IntPreference(QUALIFIER, org.eclipse.dltk.ui.CodeFormatterConstants.FORMATTER_TAB_SIZE, 4);
	IntPreference FORMATTER_INDENTATION_SPACES_SIZE = 
			new IntPreference(QUALIFIER, org.eclipse.dltk.ui.CodeFormatterConstants.FORMATTER_INDENTATION_SIZE, 4);
	
	class Helper {
		
		public static void initDefaults() {
			// This will ensure default value is initialized
			FORMATTER_INDENT_MODE.get();
			FORMATTER_TAB_SIZE.get();
			FORMATTER_INDENTATION_SPACES_SIZE.get();
		}
		
	}

}