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

//Note: the file /resources/e4-dark_sourcehighlighting.css needs to updated with changes made here, 
//such as key name changes, or the color defaults
public interface DeeColorPreferences {
	
	String PREFIX = "editor.coloring."; 
	
	ColoringItemPreference DEFAULT = new ColoringItemPreference(PREFIX + "default",
		new RGB(0, 0, 0), false, false);
	
	ColoringItemPreference COMMENT = new ColoringItemPreference(PREFIX + "comment",
		new RGB(144, 144, 144), false, false);
	ColoringItemPreference DOC_COMMENT = new ColoringItemPreference(PREFIX + "doc_comment",
		new RGB(65, 95, 185), false, false);
	
	ColoringItemPreference KEYWORDS = new ColoringItemPreference(PREFIX + "keyword",
		new RGB(127, 0, 85), true, false);
	ColoringItemPreference KW_BASICTYPES = new ColoringItemPreference(PREFIX + "basictypes",
		new RGB(150, 40, 210), false, false);
	ColoringItemPreference KW_LITERALS = new ColoringItemPreference(PREFIX + "kw_literals",
		new RGB(160, 120, 70), false, false);
	ColoringItemPreference ANNOTATIONS = new ColoringItemPreference(PREFIX + "annotations",
		new RGB(230, 75, 0), false, false);
	ColoringItemPreference OPERATORS = new ColoringItemPreference(PREFIX + "operators",
		new RGB(0, 0, 0), false, false); // Not supported yet  
	
	ColoringItemPreference CHARACTER_LITERALS = new ColoringItemPreference(PREFIX + "character",
		new RGB(0, 170, 10), false, false);
	ColoringItemPreference STRING = new ColoringItemPreference(PREFIX + "string",
		new RGB(126, 164, 0), false, false);
	ColoringItemPreference DELIM_STRING = new ColoringItemPreference(PREFIX + "delimstring",
		new RGB(175, 175, 0), false, false);
	
}