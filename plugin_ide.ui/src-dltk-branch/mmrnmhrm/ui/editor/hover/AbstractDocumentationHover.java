package mmrnmhrm.ui.editor.hover;

import melnorme.lang.ide.ui.editor.hover.BrowserControlHover;
import melnorme.lang.ide.ui.editor.hover.ILangEditorTextHover;

import org.eclipse.jface.text.ITextHoverExtension;

// TODO: refactor to LANG code
public abstract class AbstractDocumentationHover extends BrowserControlHover 
	implements ITextHoverExtension, ILangEditorTextHover<String> {
	
	public AbstractDocumentationHover() {
		super();
	}
	
	/** The CSS used to format javadoc information. */
	protected static String fgCSSStyles;
	
	protected String getCSSStyles() {
		if(false)
			return HoverUtil.getDDocPreparedCSS("/JavadocHoverStyleSheet.css");
		
		if (fgCSSStyles == null) {
			fgCSSStyles= HoverUtil.loadStyleSheet("/JavadocHoverStyleSheet.css");
		}
		String css = HoverUtil.setupCSSFont(fgCSSStyles);
		StringBuffer strBuf = new StringBuffer(css);
		strBuf.append(HoverUtil.CODE_CSS_CLASS);
		HoverUtil.addPreferencesFontsAndColorsToStyleSheet(strBuf);
		return strBuf.toString();
	}
	
}