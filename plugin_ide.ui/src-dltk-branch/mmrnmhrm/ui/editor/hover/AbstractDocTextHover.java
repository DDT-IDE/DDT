package mmrnmhrm.ui.editor.hover;

import melnorme.lang.ide.ui.editor.ILangEditorTextHover;
import mmrnmhrm.ui.DeeUIPlugin;

import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.IInformationControlExtension2;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextHoverExtension;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.ui.IEditorPart;

//TODO: this class needs some cleanup
public class AbstractDocTextHover extends org.eclipse.dltk.internal.ui.text.hover.DocumentationHover 
	implements ITextHoverExtension, ILangEditorTextHover<String> {
	
	public AbstractDocTextHover() {
		super();
		setPreferenceStore(DeeUIPlugin.getPrefStore());
	}
	
	@Override
	protected IEditorPart getEditor() {
		return super.getEditor();
	}
	
	
	@Override
	public Object getHoverInfo2(ITextViewer textViewer, IRegion hoverRegion) {
		return getHoverInfo2_do(textViewer, hoverRegion);
	}
	
	@Override
	public String getHoverInfo2_do(ITextViewer textViewer, IRegion hoverRegion) {
		return getHoverInfo(textViewer, hoverRegion);
	}
	
	/** 
	 * Note: if our hover info type is String, then the {@link IInformationControlCreator} we return don't have to 
	 * implement {@link IInformationControl}'s that implement {@link IInformationControlExtension2}.
	 * {@link IInformationControl#setInformation(String)} will be used instead. */
	@Override
	public IInformationControlCreator getHoverControlCreator() {
		return super.getHoverControlCreator();
	}
	
	/** The CSS used to format javadoc information. */
	protected static String fgCSSStyles;
	
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
	
}