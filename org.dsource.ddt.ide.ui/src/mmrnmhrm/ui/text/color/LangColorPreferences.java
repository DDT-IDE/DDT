package mmrnmhrm.ui.text.color;

import org.eclipse.dltk.ui.PreferenceConstants;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.swt.graphics.RGB;

public class LangColorPreferences {
	
	public static final String SUFFIX_BOLD = PreferenceConstants.EDITOR_BOLD_SUFFIX;
	public static final String SUFFIX_COLOR = "";
	public static final String SUFFIX_ENABLE = "_enabled";
	public static final String SUFFIX_ITALIC = PreferenceConstants.EDITOR_ITALIC_SUFFIX;
	public static final String SUFFIX_UNDERLINE = PreferenceConstants.EDITOR_UNDERLINE_SUFFIX;

	public static String getEnabledKey(String key) {
		return key + SUFFIX_ENABLE;
	}

	public static String getBoldKey(String key) {
		return key + SUFFIX_BOLD;
	}

	public static String getItalicKey(String key) {
		return key + SUFFIX_ITALIC;
	}

	public static String getUnderlineKey(String key) {
		return key + SUFFIX_UNDERLINE;
	}

	public static String getColorKey(String key) {
		return key + SUFFIX_COLOR;
	}


	public static boolean getIsEnabled(IPreferenceStore store, String key) {
		return store.getBoolean(getEnabledKey(key));
	}
	
	public static RGB getColor(IPreferenceStore store, String key) {
		return PreferenceConverter.getColor(store, getColorKey(key));
	}

	public static boolean getIsBold(IPreferenceStore store, String key) {
		return store.getBoolean(getBoldKey(key));
	}

	public static boolean getIsItalic(IPreferenceStore store, String key) {
		return store.getBoolean(getItalicKey(key));
	}
	
	public static boolean getIsUnderline(IPreferenceStore store, String key) {
		return store.getBoolean(getUnderlineKey(key));
	}
}

