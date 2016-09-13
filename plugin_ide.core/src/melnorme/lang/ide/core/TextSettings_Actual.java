package melnorme.lang.ide.core;

import org.eclipse.jface.text.IDocument;

import melnorme.utilbox.misc.ArrayUtil;


public class TextSettings_Actual {
	
	public static final String PARTITIONING_ID = "___dee_partioning";
	
	public static enum LangPartitionTypes {
		DEE_CODE,
		DEE_STRING,
		DEE_RAW_STRING,  // a WYSIWYG string, with ``
		DEE_RAW_STRING2,  // a WYSIWYG string, with r"" syntax
		DEE_DELIM_STRING,  // a delimited string
		DEE_CHARACTER,
		DEE_SINGLE_COMMENT,
		DEE_SINGLE_DOCCOMMENT,
		DEE_MULTI_COMMENT,
		DEE_MULTI_DOCCOMMENT,
		DEE_NESTED_COMMENT,
		DEE_NESTED_DOCCOMMENT;
		
		public String getId() {
			if(ordinal() == 0) {
				return IDocument.DEFAULT_CONTENT_TYPE;
			}
			return toString();
		}
	}
	
	/* ----------------- Common code ----------------- */
	
	public static final String[] PARTITION_TYPES = ArrayUtil.map(LangPartitionTypes.values(), 
		obj -> obj.getId(), String.class
	);
	
}