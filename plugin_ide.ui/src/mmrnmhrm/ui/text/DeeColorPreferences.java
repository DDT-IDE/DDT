/*******************************************************************************
 * Copyright (c) 2014, 2014 Bruno Medeiros and other Contributors.
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

interface ColorConstants {
	
	RGB COLOR_BLACK_RGB       = new RGB(0x00, 0x00, 0x00);
	RGB COLOR_CYAN_RGB        = new RGB(0x00, 0xFF, 0xFF); 
	RGB COLOR_DARK_YELLOW_RGB = new RGB(0x80, 0x80, 0x00); 
	
}

public interface DeeColorPreferences {
	
	/** Prefix for D preference keys. */
	String PREFIX = "editor.coloring."; 
	
	ColoringItemPreference DEE_COMMENT = new ColoringItemPreference(PREFIX + "comment",
		true, new RGB(63, 127, 95), false, false, false);
	
	ColoringItemPreference DEE_DOCCOMMENT = new ColoringItemPreference(PREFIX + "doccomment",
		true, new RGB(63, 95, 191), false, false, false);
	
	ColoringItemPreference DEE_DEFAULT = new ColoringItemPreference(PREFIX + "default",
		true, ColorConstants.COLOR_BLACK_RGB, false, false, false);
	ColoringItemPreference DEE_KEYWORDS = new ColoringItemPreference(PREFIX + "keyword",
		true, new RGB(0, 0, 127), true, false, false);
	ColoringItemPreference DEE_BASICTYPES = new ColoringItemPreference(PREFIX + "basictypes",
		true, new RGB(0, 0, 127), false, false, false);
	ColoringItemPreference DEE_ANNOTATIONS = new ColoringItemPreference(PREFIX + "annotations",
		true, new RGB(100, 100, 100), false, false, false);
	ColoringItemPreference DEE_LITERALS = new ColoringItemPreference(PREFIX + "literals",
		true, new RGB(127, 64, 64), false, false, false);
	ColoringItemPreference DEE_OPERATORS = new ColoringItemPreference(PREFIX + "operators",
		true, ColorConstants.COLOR_BLACK_RGB, false, false, false); // Not supported yet  
	ColoringItemPreference DEE_SPECIAL = new ColoringItemPreference(PREFIX + "special",
		false, ColorConstants.COLOR_CYAN_RGB, false, false, true); // For debug purposes only
	
	
	ColoringItemPreference DEE_STRING = new ColoringItemPreference(PREFIX + "string",
		true, ColorConstants.COLOR_DARK_YELLOW_RGB, false, false, false);
	
	ColoringItemPreference DEE_DELIM_STRING = new ColoringItemPreference(PREFIX + "delimstring",
		true, ColorConstants.COLOR_DARK_YELLOW_RGB, false, false, false);
	
	ColoringItemPreference DEE_CHARACTER_LITERALS = new ColoringItemPreference(PREFIX + "character",
		true, ColorConstants.COLOR_DARK_YELLOW_RGB, false, false, false);
	
}