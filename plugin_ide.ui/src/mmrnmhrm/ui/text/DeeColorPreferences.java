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
		true, new RGB(144, 144, 144), false, false, false);
	ColoringItemPreference DOCCOMMENT = new ColoringItemPreference(PREFIX + "doccomment",
		true, new RGB(65, 95, 185), false, false, false);
	
	ColoringItemPreference DEFAULT = new ColoringItemPreference(PREFIX + "default",
		true, new RGB(0, 0, 0), false, false, false);
	ColoringItemPreference KEYWORDS = new ColoringItemPreference(PREFIX + "keyword",
		true, new RGB(127, 0, 85), true, false, false);
	ColoringItemPreference KW_BASICTYPES = new ColoringItemPreference(PREFIX + "basictypes",
		true, new RGB(150, 40, 210), false, false, false);
	ColoringItemPreference KW_LITERALS = new ColoringItemPreference(PREFIX + "kw_literals",
		true, new RGB(160, 120, 70), false, false, false);
	ColoringItemPreference ANNOTATIONS = new ColoringItemPreference(PREFIX + "annotations",
		true, new RGB(230, 75, 0), false, false, false);
	ColoringItemPreference OPERATORS = new ColoringItemPreference(PREFIX + "operators",
		true, new RGB(0, 0, 0), false, false, false); // Not supported yet  
	
	ColoringItemPreference CHARACTER_LITERALS = new ColoringItemPreference(PREFIX + "character",
		true, new RGB(0, 170, 10), false, false, false);
	ColoringItemPreference STRING = new ColoringItemPreference(PREFIX + "string",
		true, new RGB(126, 164, 0), false, false, false);
	ColoringItemPreference DELIM_STRING = new ColoringItemPreference(PREFIX + "delimstring",
		true, new RGB(175, 175, 0), false, false, false);
	
}