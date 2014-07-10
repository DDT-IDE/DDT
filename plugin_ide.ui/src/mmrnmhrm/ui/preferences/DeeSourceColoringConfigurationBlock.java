package mmrnmhrm.ui.preferences;

import static melnorme.utilbox.core.CoreUtil.array;

import java.io.IOException;
import java.io.InputStream;

import melnorme.lang.ide.ui.text.coloring.EditorSourceColoringConfigComponent;
import melnorme.util.swt.jface.LabeledTreeElement;
import melnorme.utilbox.misc.StreamUtil;
import melnorme.utilbox.misc.StringUtil;
import mmrnmhrm.ui.DeeUIPlugin;
import mmrnmhrm.ui.editor.DeeSimpleSourceViewerConfiguration;
import mmrnmhrm.ui.text.DeePartitions;
import mmrnmhrm.ui.text.color.IDeeColorConstants;

import org.eclipse.cdt.internal.ui.text.util.CColorManager;
import org.eclipse.dltk.internal.ui.editor.ScriptSourceViewer;
import org.eclipse.dltk.ui.text.IColorManager;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.texteditor.ITextEditor;

public class DeeSourceColoringConfigurationBlock extends EditorSourceColoringConfigComponent {
	
	private static final String PREVIEW_FILE_NAME = "SourceColoringPreviewFile.d";
	
	protected static final LabeledTreeElement[] treeElements = array(
		new SourceColoringCategory("Source", array(
			new SourceColoringElement("Default", IDeeColorConstants.DEE_DEFAULT),
			new SourceColoringElement("Keywords", IDeeColorConstants.DEE_KEYWORDS),
			new SourceColoringElement("Keywords - Basic Types", IDeeColorConstants.DEE_BASICTYPES),
			new SourceColoringElement("Keywords - Literals", IDeeColorConstants.DEE_LITERALS),
			new SourceColoringElement("Character Literals", IDeeColorConstants.DEE_CHARACTER_LITERALS),
			new SourceColoringElement("Strings", IDeeColorConstants.DEE_STRING),
			new SourceColoringElement("Delimited String", IDeeColorConstants.DEE_DELIM_STRING),
			new SourceColoringElement("@Annotations", IDeeColorConstants.DEE_ANNOTATIONS)
		)),
		new SourceColoringCategory("Comments", array(
			new SourceColoringElement("Comment", IDeeColorConstants.DEE_COMMENT),
			new SourceColoringElement("Doc Comment", IDeeColorConstants.DEE_DOCCOMMENT)
		))
	);
	
	protected final IColorManager fColorManager = new DLTKColorManager_Adapter();
	
	public static class DLTKColorManager_Adapter extends CColorManager implements IColorManager {
		
	}
	
	public DeeSourceColoringConfigurationBlock(IPreferenceStore store) {
		super(store);
	}
	
	@Override
	public void dispose() {
		fColorManager.dispose();
		super.dispose();
	}
	
	@Override
	protected LabeledTreeElement[] getTreeElements() {
		return treeElements;
	}
	
	@Override
	protected String getPreviewContent() {
		InputStream is = getClass().getResourceAsStream(PREVIEW_FILE_NAME);
		try {
			return StreamUtil.readAllBytesFromStream(is).toString(StringUtil.UTF8);
		} catch (IOException e) {
			DeeUIPlugin.log(e);
			return "<INTERNAL ERROR: COULD NOT READ PREVIEW FILE";
		}
	}
	
	@Override
	protected ProjectionViewer createPreviewViewer(Composite parent, boolean showAnnotationsOverview,
			int styles, IPreferenceStore store) {
		ScriptSourceViewer sourceViewer = new ScriptSourceViewer(parent, null, null,
			showAnnotationsOverview, styles, store);
		DeeSimpleSourceViewerConfiguration configuration = createSimpleSourceViewerConfiguration(store, null, false);
		sourceViewer.configure(configuration);
		configuration.setupViewerForTextPresentationPrefChanges(sourceViewer);
		return sourceViewer;
	}
	
	protected DeeSimpleSourceViewerConfiguration createSimpleSourceViewerConfiguration( 
			IPreferenceStore preferenceStore, ITextEditor editor, boolean configureFormatter) {
		return new DeeSimpleSourceViewerConfiguration(fColorManager, preferenceStore, editor,
			DeePartitions.PARTITIONING_ID, configureFormatter);
	}
	
}