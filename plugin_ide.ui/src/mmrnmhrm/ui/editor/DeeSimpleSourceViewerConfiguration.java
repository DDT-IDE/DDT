package mmrnmhrm.ui.editor;

import org.eclipse.cdt.ui.text.IColorManager;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.formatter.IContentFormatter;
import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;
import org.eclipse.jface.text.information.IInformationPresenter;
import org.eclipse.jface.text.source.IAnnotationHover;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditor;

import _org.eclipse.dltk.internal.ui.editor.ScriptSourceViewer;


/**
 * This simple source viewer configuration basically provides syntax coloring
 * and disables all other features like code assist, quick outlines, hyperlinking, etc.
 */
public class DeeSimpleSourceViewerConfiguration extends DeeSourceViewerConfiguration {
	
	private boolean fConfigureFormatter;
	
	public DeeSimpleSourceViewerConfiguration(IColorManager colorManager, IPreferenceStore preferenceStore, 
			AbstractDecoratedTextEditor editor, boolean configureFormatter) {
		super(colorManager, preferenceStore, editor);
		fConfigureFormatter = configureFormatter;
	}
	
	@Override
	public IAutoEditStrategy[] getAutoEditStrategies(ISourceViewer sourceViewer, String contentType) {
		return null;
	}
	
	@Override
	public IAnnotationHover getAnnotationHover(ISourceViewer sourceViewer) {
		return null;
	}
	
	@Override
	public IAnnotationHover getOverviewRulerAnnotationHover(
			ISourceViewer sourceViewer) {
		return null;
	}
	
	@Override
	public int[] getConfiguredTextHoverStateMasks(ISourceViewer sourceViewer, String contentType) {
		return null;
	}
	
	@Override
	public ITextHover getTextHover_do(ISourceViewer sourceViewer, String contentType, int stateMask) {
		return null;
	}
	
	@Override
	public IContentFormatter getContentFormatter(ISourceViewer sourceViewer) {
		if (fConfigureFormatter)
			return super.getContentFormatter(sourceViewer);
		else
			return null;
	}
	
	@Override
	public IInformationControlCreator getInformationControlCreator(ISourceViewer sourceViewer) {
		return null;
	}
	
	@Override
	public IInformationPresenter getInformationPresenter(ISourceViewer sourceViewer) {
		return null;
	}
	
	@Override
	public IInformationPresenter getOutlinePresenter(ISourceViewer sourceViewer, 
			boolean doCodeResolve) {
		return null;
	}
	
	@Override
	public IInformationPresenter getHierarchyPresenter(ScriptSourceViewer sourceViewer, boolean doCodeResolve) {
		return null;
	}
	
	@Override
	public IHyperlinkDetector[] getHyperlinkDetectors(ISourceViewer sourceViewer) {
		return null;
	}
	
}