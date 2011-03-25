package mmrnmhrm.ui.editor.text;

import org.eclipse.dltk.ui.DLTKUIPlugin;
import org.eclipse.dltk.ui.text.hover.IScriptEditorTextHover;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.ui.IEditorPart;

/**
 * Used in org.eclipse.dltk.ui.editorTextHovers
 * A bit of hack. This wrapper is necessary so that this contribution is sorted 
 * correctly in {@link DLTKUIPlugin#initializeEditorTextHoverDescriprtors}
 * TODO: review this code in DLTK 3.0
 */
public class ProblemHoverWrapper implements IScriptEditorTextHover {

	private IScriptEditorTextHover fHover;

	public ProblemHoverWrapper() {
		fHover = new org.eclipse.dltk.internal.ui.text.hover.ProblemHover();
	}

	@Override
	public void setEditor(IEditorPart editor) {
		fHover.setEditor(editor);
	}

	@Override
	public void setPreferenceStore(IPreferenceStore store) {
		fHover.setPreferenceStore(store);
	}

	@Override
	public IRegion getHoverRegion(ITextViewer textViewer, int offset) {
		return fHover.getHoverRegion(textViewer, offset);
	}

	@Override
	public String getHoverInfo(ITextViewer textViewer, IRegion hoverRegion) {
		return fHover.getHoverInfo(textViewer, hoverRegion);
	}
}
