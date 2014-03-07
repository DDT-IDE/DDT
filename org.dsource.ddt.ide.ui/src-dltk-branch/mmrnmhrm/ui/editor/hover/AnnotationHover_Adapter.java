package mmrnmhrm.ui.editor.hover;

import melnorme.lang.ide.ui.editors.ILangEditorTextHover;
import mmrnmhrm.ui.DeeUIPlugin;

import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.ui.IEditorPart;

/**
 * Adapt {@link org.eclipse.dltk.internal.ui.text.hover.ProblemHover} to {@link ILangEditorTextHover}
 */
public class AnnotationHover_Adapter implements ILangEditorTextHover<String> {
	
	protected final org.eclipse.dltk.internal.ui.text.hover.AbstractAnnotationHover fHover;
	
	public AnnotationHover_Adapter() {
		this(new org.eclipse.dltk.internal.ui.text.hover.AnnotationHover());
	}
	
	public AnnotationHover_Adapter(org.eclipse.dltk.internal.ui.text.hover.AbstractAnnotationHover problemHover) {
		fHover = problemHover;
	}
	
	@Override
	public void setEditor(IEditorPart editor) {
		fHover.setEditor(editor);
		fHover.setPreferenceStore(DeeUIPlugin.getPrefStore());
	}
	
	@Override
	public IRegion getHoverRegion(ITextViewer textViewer, int offset) {
		return fHover.getHoverRegion(textViewer, offset);
	}
	
	@Override
	public String getHoverInfo(ITextViewer textViewer, IRegion hoverRegion) {
		return fHover.getHoverInfo(textViewer, hoverRegion);
	}
	
	@Override
	public final Object getHoverInfo2(ITextViewer textViewer, IRegion hoverRegion) {
		return getHoverInfo2_do(textViewer, hoverRegion);
	}
	
	@Override
	public String getHoverInfo2_do(ITextViewer textViewer, IRegion hoverRegion) {
		return getHoverInfo(textViewer, hoverRegion);
	}
	
	@Override
	public IInformationControlCreator getHoverControlCreator() {
		return fHover.getHoverControlCreator();
	}
	
	public static class ProblemHover_Adapter extends AnnotationHover_Adapter {
		
		public ProblemHover_Adapter() {
			super(new org.eclipse.dltk.internal.ui.text.hover.ProblemHover());
		}
		
	}
	
}