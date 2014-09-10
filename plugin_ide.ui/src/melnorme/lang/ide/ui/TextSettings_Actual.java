package melnorme.lang.ide.ui;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import melnorme.lang.ide.ui.text.AbstractLangSourceViewerConfiguration;
import melnorme.utilbox.misc.ArrayUtil;
import mmrnmhrm.ui.editor.DeeSimpleSourceViewerConfiguration;
import mmrnmhrm.ui.text.DeePartitionScanner;
import mmrnmhrm.ui.text.DeePartitions;

import org.eclipse.cdt.internal.ui.text.util.CColorManager;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.IPartitionTokenScanner;


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
	
	public static AbstractLangSourceViewerConfiguration createSimpleSourceViewerConfiguration(
			IPreferenceStore preferenceStore, CColorManager colorManager) {
		// TODO:
		new DeeSimpleSourceViewerConfiguration(colorManager, preferenceStore, null, false);
		throw assertFail();
	}
	
}