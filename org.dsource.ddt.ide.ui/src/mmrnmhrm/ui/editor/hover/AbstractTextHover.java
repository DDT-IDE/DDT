package mmrnmhrm.ui.editor.hover;

import mmrnmhrm.ui.DeePlugin;

import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.ITextHoverExtension;
import org.eclipse.ui.texteditor.ITextEditor;

@SuppressWarnings("restriction")
public class AbstractTextHover extends org.eclipse.dltk.internal.ui.text.hover.DocumentationHover 
implements ITextHoverExtension {


	/** The CSS used to format javadoc information. */
	protected static String fgCSSStyles;

	protected ITextEditor fEditor;

	
	public AbstractTextHover() {
		super();
		setPreferenceStore(DeePlugin.getPrefStore());
		//super(sourceViewer);
	}

	protected String getCSSStyles() {
		if(false)
			return getStyleSheet() + HoverUtil.CODE_CSS_CLASS;
		
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

	
	
	@Override
	public IInformationControlCreator getHoverControlCreator() {
		return super.getHoverControlCreator();
	}


}