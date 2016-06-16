package mmrnmhrm.ui.editor.hover;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.StringConverter;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;

import _org.eclipse.jdt.internal.ui.text.HTMLPrinter;
import dtool.ddoc.DDocResources;
import dtool.ddoc.IDeeDocColorConstants;
import melnorme.lang.ide.ui.LangUIPlugin;
import melnorme.lang.ide.ui.text.coloring.TextColoringConstants;
import mmrnmhrm.ui.DeeUIPlugin;

class JDT_PreferenceConstants {

	private JDT_PreferenceConstants() {
	}
	
	/**
	 * The symbolic font name for the font used to display Javadoc 
	 * (value <code>"org.eclipse.jdt.ui.javadocfont"</code>).
	 * 
	 * @since 3.3
	 */
	public final static String APPEARANCE_JAVADOC_FONT= LangUIPlugin.PLUGIN_ID + ".javadocfont";
	
}

public class HoverUtil {
	
	public static class DeePluginPreferences {
		public static String getPreference(String key, @SuppressWarnings("unused") IProject project) {
			return DeeUIPlugin.getPrefStore().getString(key);
		}
	}
	
	public static String getCompleteHoverInfo(String info, String cssStyle) {
		
		if (info != null && info.length() > 0) {
			StringBuffer buffer= new StringBuffer();
			HTMLPrinter.insertPageProlog(buffer, cssStyle);
			buffer.append(info);
			HTMLPrinter.addPageEpilog(buffer);
			info= buffer.toString();
		}
		return info;
	}

	static String setupCSSFont(String fgCSSStyles) {
		String css= fgCSSStyles;
		if (css != null) {
			FontData fontData= JFaceResources.getFontRegistry().
				getFontData(JDT_PreferenceConstants.APPEARANCE_JAVADOC_FONT)[0];
			css= HTMLPrinter.convertTopLevelFont(css, fontData);
		}
		return css;
	}
	
	public static final String CODE_CSS_CLASS 
	= ".code		 { font-family: monospace; background-color: #e7e7e8; border: 2px solid #cccccc; padding: 0ex;}";

	
	public static String getDDocPreparedCSS() {
		return getDDocPreparedCSS(DDocResources.defaultDocStyleSheet);
	}
	
	public static String getDDocPreparedCSS(String cssStyles) {
		String css = setupCSSFont(cssStyles);
		StringBuffer strBuf = new StringBuffer(css);
		strBuf.append(CODE_CSS_CLASS);
		addPreferencesFontsAndColorsToStyleSheet(strBuf);
		return strBuf.toString();
	}
	
	static void addPreferencesFontsAndColorsToStyleSheet(StringBuffer buffer) {
		addStyle(buffer, IDeeDocColorConstants.JAVA_KEYWORD);
		addStyle(buffer, IDeeDocColorConstants.JAVA_KEYWORD_RETURN);
		addStyle(buffer, IDeeDocColorConstants.JAVA_SPECIAL_TOKEN);
		addStyle(buffer, IDeeDocColorConstants.JAVA_OPERATOR);
		addStyle(buffer, IDeeDocColorConstants.JAVA_DEFAULT);
		addStyle(buffer, IDeeDocColorConstants.JAVA_PRAGMA);
		addStyle(buffer, IDeeDocColorConstants.JAVA_STRING);
		addStyle(buffer, IDeeDocColorConstants.JAVA_SINGLE_LINE_COMMENT);
		addStyle(buffer, IDeeDocColorConstants.JAVA_SINGLE_LINE_DOC_COMMENT);
		addStyle(buffer, IDeeDocColorConstants.JAVA_MULTI_LINE_COMMENT);
		addStyle(buffer, IDeeDocColorConstants.JAVA_MULTI_LINE_PLUS_COMMENT);
		addStyle(buffer, IDeeDocColorConstants.JAVA_MULTI_LINE_PLUS_DOC_COMMENT);
		addStyle(buffer, IDeeDocColorConstants.JAVADOC_DEFAULT);
	}
	
	private static void addStyle(StringBuffer buffer, String partialPreferenceKey) {
		//IJavaProject javaProject = null;
		
		buffer.append("."); //$NON-NLS-1$
		buffer.append(partialPreferenceKey);
		buffer.append("{"); //$NON-NLS-1$

		String colorString = DeePluginPreferences.getPreference(partialPreferenceKey, null);
		RGB color = colorString == null ? new RGB(0, 0, 0) : StringConverter.asRGB(colorString);
		buffer.append("color: "); //$NON-NLS-1$
		HTMLPrinter_appendColor(buffer, color);
		buffer.append(";"); //$NON-NLS-1$
		
		String boolString;
		boolean bool, bool2;
		
		boolString = DeePluginPreferences.getPreference(partialPreferenceKey + TextColoringConstants.EDITOR_BOLD_SUFFIX, null);
		bool = convertToBool(boolString);
		if (bool) {
			buffer.append("font-weight: bold;"); //$NON-NLS-1$
		}
		
		boolString = DeePluginPreferences.getPreference(partialPreferenceKey + TextColoringConstants.EDITOR_ITALIC_SUFFIX, null);
		bool = convertToBool(boolString);
		if (bool) {
			buffer.append("font-style: italic;"); //$NON-NLS-1$
		}
		
		boolString = DeePluginPreferences.getPreference(partialPreferenceKey + TextColoringConstants.EDITOR_UNDERLINE_SUFFIX, null);
		bool = convertToBool(boolString);
		
		boolString = DeePluginPreferences.getPreference(partialPreferenceKey + TextColoringConstants.EDITOR_STRIKETHROUGH_SUFFIX, null);
		bool2 = convertToBool(boolString);
		
		if (bool || bool2) {
			buffer.append("text-decoration:"); //$NON-NLS-1$
			if (bool) {
				buffer.append("underline"); //$NON-NLS-1$
			}
			if (bool && bool2) {
				buffer.append(", "); //$NON-NLS-1$
			}
			if (bool2) {
				buffer.append("line-through"); //$NON-NLS-1$
			}
			buffer.append(";"); //$NON-NLS-1$
		}
		
		buffer.append("}\n"); //$NON-NLS-1$
	}

	private static boolean convertToBool(String boolString) {
		return boolString == null || boolString.equals("") ? 
				false : StringConverter.asBoolean(boolString);
	}

	
	public static void HTMLPrinter_appendColor(StringBuffer buffer, RGB rgb) {
		buffer.append('#');
		buffer.append(toHexString(rgb.red));
		buffer.append(toHexString(rgb.green));
		buffer.append(toHexString(rgb.blue));
	}
	
	private static String toHexString(int value) {
		String s = Integer.toHexString(value);
		if (s.length() != 2) {
			return "0" + s;
		}
		return s;
	}

}