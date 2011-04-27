/*******************************************************************************
 * Copyright (c) 2008, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package mmrnmhrm.ui.text.color;



/**
 * Color preference constants used in the Dee preference store. 
 */
public interface IDeeColorConstants {

	/** Prefix for D preference keys. */
	String PREFIX = "dee.coloring."; 

	// BM: should we use DLTKColorConstants constants like other IDEs? AFAIK don't see any use in that
	String DEE_COMMENT = PREFIX + "comment";
	
	String DEE_DOCCOMMENT = PREFIX + "doccomment";
	
	String DEE_DEFAULT = PREFIX + "default";
	String DEE_KEYWORDS = PREFIX + "keyword";
	String DEE_BASICTYPES = PREFIX + "basictypes";
	String DEE_ANNOTATIONS = PREFIX + "annotations";
	String DEE_LITERALS = PREFIX + "literals";
	String DEE_OPERATORS = PREFIX + "operators"; // Not supported yet  
	String DEE_SPECIAL = PREFIX + "special"; // For debug purposes only
	
	String DEE_STRING = PREFIX + "string";
	
	String DEE_RAW_STRING = PREFIX + "rawstring";
	
	String DEE_DELIM_STRING = PREFIX + "delimstring";
	
	String DEE_CHARACTER_LITERALS = PREFIX + "character";

}
