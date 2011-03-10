package mmrnmhrm.ui.text;

import melnorme.utilbox.core.Assert;
import melnorme.utilbox.misc.ArrayUtil;

import org.eclipse.dltk.ui.editor.highlighting.ISemanticHighlighter;
import org.eclipse.dltk.ui.editor.highlighting.SemanticHighlighting;
import org.eclipse.dltk.ui.text.ScriptSourceViewerConfiguration;
import org.eclipse.dltk.ui.text.ScriptTextTools;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.rules.IPartitionTokenScanner;
import org.eclipse.ui.texteditor.ITextEditor;

public class DeeTextTools extends ScriptTextTools {
	
	protected static final String[] LEGAL_CONTENT_TYPES = 
		ArrayUtil.remove(DeePartitions.DEE_PARTITION_TYPES, DeePartitions.DEE_CODE);
	
	
	public DeeTextTools(boolean autoDisposeOnDisplayDispose) {
		super(DeePartitions.DEE_PARTITIONING, LEGAL_CONTENT_TYPES, autoDisposeOnDisplayDispose);
	}
	
	@Override
	public IPartitionTokenScanner createPartitionScanner() {
		return super.createPartitionScanner();
	}
	
	@Override
	public IPartitionTokenScanner getPartitionScanner() {
		return new DeePartitionScanner();
	}
	
	@Override
	public ScriptSourceViewerConfiguration createSourceViewerConfiguraton(
			IPreferenceStore preferenceStore, ITextEditor editor, String partitioning) {
		Assert.isTrue(partitioning.equals(DeePartitions.DEE_PARTITIONING));
		return new DeeSourceViewerConfiguration(getColorManager(), preferenceStore, editor,
				DeePartitions.DEE_PARTITIONING);
	}
	
	
	@Override
	public SemanticHighlighting[] getSemanticHighlightings() {
		return new SemanticHighlighting[0]; // TODO
	}
	
	@Override
	public ISemanticHighlighter getSemanticPositionUpdater() {
		return null; // TODO
	}
	
}
