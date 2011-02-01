package mmrnmhrm.ui.editor.text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Collections;
import java.util.Map;

import mmrnmhrm.ui.DeePluginPreferences;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.dltk.ui.PreferenceConstants;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.ui.JDT_PreferenceConstants;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.StringConverter;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;
import org.osgi.framework.Bundle;

import descent.core.ddoc.Ddoc;
import dtool.IDeeDocColorConstants;
import dtool.DeeDocAccessor;
import dtool.ast.definitions.DefUnit;

public class HoverUtil {

	private static final Map<String, String> EMPTY_MAP = Collections.emptyMap();

	/** Get's the HTML info for the given DefUnit. */
	public static String getDefUnitHoverInfoWithDeeDoc(DefUnit defUnit) {
		String sig = defUnit.toStringForHoverSignature();
		String str = convertToHTMLContent(sig);
		str = "<b>" +str+ "</b>" 
		+"  <span style=\"color: #915F6D;\" >"+
			"("+defUnit.getArcheType().toString()+")"+"</span>";

		
		Ddoc ddoc = DeeDocAccessor.getDdoc(defUnit);
		if(ddoc != null) {
			StringBuffer stringBuffer = DeeDocAccessor.transform(ddoc, EMPTY_MAP);
			str += "<br/><br/>" + stringBuffer.toString();
		}
		return str;
	}

	@SuppressWarnings("restriction")
	private static String convertToHTMLContent(String str) {
		
		str = org.eclipse.jface.internal.text.html.
			HTMLPrinter.convertToHTMLContent(str);
		str = str.replace("\n", "<br/>");
		return str;
	}

	public static String loadStyleSheet(String cssfilepath) {
		Bundle bundle= Platform.getBundle(JavaPlugin.getPluginId());
		URL url= bundle.getEntry(cssfilepath); //$NON-NLS-1$
		if (url != null) {
			try {
				url= FileLocator.toFileURL(url);
				BufferedReader reader= new BufferedReader(new InputStreamReader(url.openStream()));
				StringBuffer buffer= new StringBuffer(200);
				String line= reader.readLine();
				while (line != null) {
					buffer.append(line);
					buffer.append('\n');
					line= reader.readLine();
				}
				return buffer.toString();
			} catch (IOException ex) {
				JavaPlugin.log(ex);
			}
		}
		return null;
	}

	@SuppressWarnings("restriction")
	public static String getCompleteHoverInfo(String info, String cssStyle) {
		
		if (info != null && info.length() > 0) {
			StringBuffer buffer= new StringBuffer();
			org.eclipse.jface.internal.text.html.
			HTMLPrinter.insertPageProlog(buffer, 0, cssStyle);
			buffer.append(info);
			org.eclipse.jface.internal.text.html.
			HTMLPrinter.addPageEpilog(buffer);
			info= buffer.toString();
		}
		return info;
	}

	@SuppressWarnings("restriction")
	static String setupCSSFont(String fgCSSStyles) {
		String css= fgCSSStyles;
		if (css != null) {
			FontData fontData= JFaceResources.getFontRegistry().
				getFontData(JDT_PreferenceConstants.APPEARANCE_JAVADOC_FONT)[0];
			css= org.eclipse.jface.internal.text.html.
				HTMLPrinter.convertTopLevelFont(css, fontData);
		}
		return css;
	}
	
	public static final String CODE_CSS_CLASS 
	= ".code		 { font-family: monospace; background-color: #e7e7e8; border: 2px solid #cccccc; padding: 0ex;}";

	
	public static String getDDocPreparedCSS(String filename) {
		String str = HoverUtil.loadStyleSheet(filename);
		str = HoverUtil.setupCSSFont(str);
		StringBuffer strBuf = new StringBuffer(str);
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
		
		boolString = DeePluginPreferences.getPreference(partialPreferenceKey + PreferenceConstants.EDITOR_BOLD_SUFFIX, null);
		bool = convertToBool(boolString);
		if (bool) {
			buffer.append("font-weight: bold;"); //$NON-NLS-1$
		}
		
		boolString = DeePluginPreferences.getPreference(partialPreferenceKey + PreferenceConstants.EDITOR_ITALIC_SUFFIX, null);
		bool = convertToBool(boolString);
		if (bool) {
			buffer.append("font-style: italic;"); //$NON-NLS-1$
		}
		
		boolString = DeePluginPreferences.getPreference(partialPreferenceKey + PreferenceConstants.EDITOR_UNDERLINE_SUFFIX, null);
		bool = convertToBool(boolString);
		
		boolString = DeePluginPreferences.getPreference(partialPreferenceKey + PreferenceConstants.EDITOR_STRIKETHROUGH_SUFFIX, null);
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
