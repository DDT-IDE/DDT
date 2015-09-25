/*******************************************************************************
 * Copyright (c) 2014 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package mmrnmhrm.ui.text;

import melnorme.lang.ide.ui.text.coloring.ColoringItemPreference;

import org.eclipse.swt.graphics.RGB;

public interface DeeColorPreferences {
	
	/** Prefix for the color preference keys. */
	String PREFIX = "editor.coloring."; 
	
	ColoringItemPreference COMMENT = new ColoringItemPreference(PREFIX + "comment",
		true, new RGB(63, 127, 95), false, false, false);
	
	ColoringItemPreference DOCCOMMENT = new ColoringItemPreference(PREFIX + "doccomment",
		true, new RGB(63, 95, 191), false, false, false);
	
	ColoringItemPreference DEFAULT = new ColoringItemPreference(PREFIX + "default",
		true, new RGB(0x00, 0x00, 0x00), false, false, false);
	ColoringItemPreference KEYWORDS = new ColoringItemPreference(PREFIX + "keyword",
		true, new RGB(0, 0, 127), true, false, false);
	ColoringItemPreference BASICTYPES = new ColoringItemPreference(PREFIX + "basictypes",
		true, new RGB(0, 0, 127), false, false, false);
	ColoringItemPreference ANNOTATIONS = new ColoringItemPreference(PREFIX + "annotations",
		true, new RGB(100, 100, 100), false, false, false);
	ColoringItemPreference LITERALS = new ColoringItemPreference(PREFIX + "literals",
		true, new RGB(127, 64, 64), false, false, false);
	ColoringItemPreference OPERATORS = new ColoringItemPreference(PREFIX + "operators",
		true, new RGB(0x00, 0x00, 0x00), false, false, false); // Not supported yet  
	
	
	ColoringItemPreference STRING = new ColoringItemPreference(PREFIX + "string",
		true, new RGB(0x80, 0x80, 0x00), false, false, false);
	
	ColoringItemPreference DELIM_STRING = new ColoringItemPreference(PREFIX + "delimstring",
		true, new RGB(0x80, 0x80, 0x00), false, false, false);
	
	ColoringItemPreference CHARACTER_LITERALS = new ColoringItemPreference(PREFIX + "character",
		true, new RGB(0x80, 0x80, 0x00), false, false, false);
	
}