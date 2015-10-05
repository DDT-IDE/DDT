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
package mmrnmhrm.core.text;

import melnorme.lang.ide.core.TextSettings_Actual.LangPartitionTypes;

public interface DeePartitions {
	
	String DEE_STRING = LangPartitionTypes.DEE_STRING.getId();
	String DEE_RAW_STRING = LangPartitionTypes.DEE_RAW_STRING.getId();
	String DEE_RAW_STRING2 = LangPartitionTypes.DEE_RAW_STRING2.getId();
	String DEE_DELIM_STRING = LangPartitionTypes.DEE_DELIM_STRING.getId();
	String DEE_CHARACTER = LangPartitionTypes.DEE_CHARACTER.getId();
	String DEE_SINGLE_COMMENT = LangPartitionTypes.DEE_SINGLE_COMMENT.getId();  
	String DEE_SINGLE_DOCCOMMENT = LangPartitionTypes.DEE_SINGLE_DOCCOMMENT.getId();  
	String DEE_MULTI_COMMENT = LangPartitionTypes.DEE_MULTI_COMMENT.getId();  
	String DEE_MULTI_DOCCOMMENT = LangPartitionTypes.DEE_MULTI_DOCCOMMENT.getId();  
	String DEE_NESTED_COMMENT = LangPartitionTypes.DEE_NESTED_COMMENT.getId();  
	String DEE_NESTED_DOCCOMMENT = LangPartitionTypes.DEE_NESTED_DOCCOMMENT.getId();  
	
}