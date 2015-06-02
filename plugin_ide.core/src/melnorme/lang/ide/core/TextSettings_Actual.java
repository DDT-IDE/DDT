package melnorme.lang.ide.core;

import melnorme.lang.ide.core.text.LangDocumentPartitionerSetup;
import mmrnmhrm.core.text.DeeDocumentSetupParticipant;
import mmrnmhrm.core.text.DeePartitionScanner;
import mmrnmhrm.core.text.DeePartitions;

import org.eclipse.jface.text.rules.IPartitionTokenScanner;


public class TextSettings_Actual {
	
	public static final String PARTITIONING_ID = DeePartitions.PARTITIONING_ID;
	
	public static final String[] PARTITION_TYPES = DeePartitions.DEE_PARTITION_TYPES;
	
//	public static interface LangPartitionTypes {
//		String CODE = IDocument.DEFAULT_CONTENT_TYPE;
//		String COMMENT = "comment";
//		String STRING = "string";
//	}
	
	public static IPartitionTokenScanner createPartitionScanner() {
		return new DeePartitionScanner();
	}
	
	public static LangDocumentPartitionerSetup createDocumentSetupHelper() {
		return new DeeDocumentSetupParticipant();
	}
	
}