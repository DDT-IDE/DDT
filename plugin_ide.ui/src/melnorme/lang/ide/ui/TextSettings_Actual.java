package melnorme.lang.ide.ui;

import melnorme.lang.ide.ui.text.LangDocumentPartitionerSetup;
import melnorme.utilbox.misc.ArrayUtil;
import mmrnmhrm.ui.editor.DeeSimpleSourceViewerConfiguration;
import mmrnmhrm.ui.editor.DeeSourceViewerConfiguration;
import mmrnmhrm.ui.text.DeeDocumentSetupParticipant;
import mmrnmhrm.ui.text.DeePartitionScanner;
import mmrnmhrm.ui.text.DeePartitions;

import org.eclipse.cdt.ui.text.IColorManager;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.IPartitionTokenScanner;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditor;


public class TextSettings_Actual {
	
	public static final String PARTITIONING_ID = DeePartitions.PARTITIONING_ID;
	
	public static final String[] PARTITION_TYPES = DeePartitions.DEE_PARTITION_TYPES;
	
//	public static interface LangPartitionTypes {
//		String CODE = IDocument.DEFAULT_CONTENT_TYPE;
//		String COMMENT = "comment";
//		String STRING = "string";
//	}
	
	public static final String[] LEGAL_CONTENT_TYPES = 
			ArrayUtil.remove(PARTITION_TYPES, IDocument.DEFAULT_CONTENT_TYPE);
	
	public static IPartitionTokenScanner createPartitionScanner() {
		return new DeePartitionScanner();
	}
	
	public static LangDocumentPartitionerSetup createDocumentSetupHelper() {
		return new DeeDocumentSetupParticipant();
	}
	
	public static DeeSourceViewerConfiguration createSourceViewerConfiguration(
			IPreferenceStore preferenceStore, AbstractDecoratedTextEditor editor) {
		IColorManager colorManager = LangUIPlugin.getInstance().getColorManager();
		return new DeeSourceViewerConfiguration(colorManager, preferenceStore, editor);
	}
	
	public static DeeSimpleSourceViewerConfiguration createSimpleSourceViewerConfiguration(
			IPreferenceStore preferenceStore, IColorManager colorManager) {
		return new DeeSimpleSourceViewerConfiguration(colorManager, preferenceStore, null, false);
	}
	
}