package mmrnmhrm.ui.preferences;

import java.io.InputStream;

import melnorme.lang.ide.ui.text.LangDocumentPartitionerSetup;
import mmrnmhrm.ui.editor.DeeSimpleSourceViewerConfiguration;
import mmrnmhrm.ui.text.DeePartitions;
import mmrnmhrm.ui.text.color.IDeeColorConstants;

import org.eclipse.dltk.internal.ui.editor.ScriptSourceViewer;
import org.eclipse.dltk.ui.preferences.IPreferenceConfigurationBlock;
import org.eclipse.dltk.ui.preferences.OverlayPreferenceStore;
import org.eclipse.dltk.ui.text.IColorManager;
import org.eclipse.dltk.ui.text.ScriptSourceViewerConfiguration;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.source.IOverviewRuler;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.texteditor.ITextEditor;

// TODO: DLTK: make tree expand, make list not sorted
public class DeeSourceColoringConfigurationBlock extends
		org.eclipse.dltk.ui.preferences.AbstractScriptEditorColoringConfigurationBlock implements
		IPreferenceConfigurationBlock {
	
	private static final String PREVIEW_FILE_NAME = "SourceColoringPreviewFile.d";
	
	private static final String[][] fSyntaxColorListModel = new String[][] {
			{ "Comment", IDeeColorConstants.DEE_COMMENT, sDocumentationCategory },
			{ "Doc Comment", IDeeColorConstants.DEE_DOCCOMMENT, sDocumentationCategory },
			
			{ "Keywords"		, IDeeColorConstants.DEE_KEYWORDS, sCoreCategory },
			{ "Basic Types"		, IDeeColorConstants.DEE_BASICTYPES, sCoreCategory },
			{ "@Annotations"	, IDeeColorConstants.DEE_ANNOTATIONS, sCoreCategory },
			{ "Literals"		, IDeeColorConstants.DEE_LITERALS, sCoreCategory },
            { "Character Literals", IDeeColorConstants.DEE_CHARACTER_LITERALS, sCoreCategory },
            { "Strings"			, IDeeColorConstants.DEE_STRING, sCoreCategory },
            { "Delimited String", IDeeColorConstants.DEE_DELIM_STRING, sCoreCategory },
            //{ "Operators"		, IDeeColorConstants.DEE_OPERATORS, sCoreCategory },
			{ "Default"			, IDeeColorConstants.DEE_DEFAULT, sCoreCategory },							
	};
	

	public DeeSourceColoringConfigurationBlock(OverlayPreferenceStore store) {
		super(store);
	}

	@Override
	protected String[][] getSyntaxColorListModel() {
		return fSyntaxColorListModel;
	}

	@Override
	protected ProjectionViewer createPreviewViewer(Composite parent,
			IVerticalRuler verticalRuler, IOverviewRuler overviewRuler,
			boolean showAnnotationsOverview, int styles, IPreferenceStore store) {
		return new ScriptSourceViewer(parent, verticalRuler, overviewRuler,
				showAnnotationsOverview, styles, store);
	}

	@Override
	protected ScriptSourceViewerConfiguration createSimpleSourceViewerConfiguration(
			IColorManager colorManager, IPreferenceStore preferenceStore,
			ITextEditor editor, boolean configureFormatter) {
		return new DeeSimpleSourceViewerConfiguration(colorManager,
				preferenceStore, editor, DeePartitions.PARTITIONING_ID,
				configureFormatter);
	}
	
	@Override
	protected void setDocumentPartitioning(IDocument document) {
		LangDocumentPartitionerSetup.getInstance().setup(document);;
	}
	
	@Override
	protected InputStream getPreviewContentReader() {
		return getClass().getResourceAsStream(PREVIEW_FILE_NAME);
	}
}
