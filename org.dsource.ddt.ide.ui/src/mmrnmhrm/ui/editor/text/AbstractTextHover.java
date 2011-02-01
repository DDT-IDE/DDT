package mmrnmhrm.ui.editor.text;

import mmrnmhrm.ui.DeePlugin;

import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jface.text.AbstractReusableInformationControlCreator;
import org.eclipse.jface.text.DefaultInformationControl;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.ITextHoverExtension;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
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
		if(true)
		return super.getHoverControlCreator();
		
		// TODO: remove this?
		if(false)
		return new IInformationControlCreator() {
			@Override
			public IInformationControl createInformationControl(Shell parent) {
				return new DefaultInformationControl(parent, "TODO",  
						//new	org.eclipse.jface.internal.text.html.HTMLTextPresenter(true),
						new org.eclipse.dltk.internal.ui.text.HTMLTextPresenter(true)
						);
			}
		};
		
		Shell shell= JavaPlugin.getActiveWorkbenchShell();
		if (shell == null
				|| !org.eclipse.dltk.internal.ui.BrowserInformationControl
						.isAvailable(shell))
			return null;
		
		return new AbstractReusableInformationControlCreator() {
			@Override
			public IInformationControl doCreateInformationControl(Shell parent) {
				return new org.eclipse.dltk.internal.ui.BrowserInformationControl(
						parent, SWT.NO_TRIM, SWT.NONE, "STATUS");
			}
		};
	}


}