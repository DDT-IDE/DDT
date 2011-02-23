/*******************************************************************************
 * Copyright (c) 2010, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package mmrnmhrm.ui.text;

import org.eclipse.jface.text.IDocument;

public interface DeePartitions {
	
	String DEE_PARTITIONING = "___dee_partioning";
	
	String DEE_CODE = IDocument.DEFAULT_CONTENT_TYPE;
	String DEE_STRING = "___dee_string";
	String DEE_RAW_STRING = "___dee_raw_string"; // a WYSIWYG string
	String DEE_DELIM_STRING = "___dee_delim_string"; // a delimited string
	String DEE_CHARACTER = "___dee_character";
	String DEE_SINGLE_COMMENT = "___dee_single_comment";  
	String DEE_SINGLE_DOCCOMMENT = "___dee_single_doccomment";  
	String DEE_MULTI_COMMENT = "___dee_multi_comment";  
	String DEE_MULTI_DOCCOMMENT = "___dee_multi_doccomment";  
	String DEE_NESTED_COMMENT = "___dee_nested_comment";  
	String DEE_NESTED_DOCCOMMENT = "___dee_nested_doccomment";  
	
	public static final String[] DEE_PARTITION_TYPES = {
		DEE_CODE, // Same as IDocument.DEFAULT_CONTENT_TYPE
		DEE_STRING,
		DEE_RAW_STRING,
		DEE_DELIM_STRING,
		DEE_CHARACTER,
		DEE_SINGLE_COMMENT,
		DEE_SINGLE_DOCCOMMENT,
		DEE_MULTI_COMMENT,
		DEE_MULTI_DOCCOMMENT,
		DEE_NESTED_COMMENT,
		DEE_NESTED_DOCCOMMENT,
	};
	
}
