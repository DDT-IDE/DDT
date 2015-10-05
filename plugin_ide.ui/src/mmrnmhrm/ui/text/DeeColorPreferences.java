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

import org.eclipse.swt.graphics.RGB;

import melnorme.lang.ide.ui.text.coloring.TextStylingPreference;

//Note: the file /resources/e4-dark_sourcehighlighting.css needs to updated with changes made here, 
//such as key name changes, or the color defaults
public interface DeeColorPreferences {
	
	String PREFIX = "editor.coloring."; 
	
	TextStylingPreference DEFAULT = new TextStylingPreference(PREFIX + "default",
		new RGB(0, 0, 0), false, false);
	
	TextStylingPreference COMMENT = new TextStylingPreference(PREFIX + "comment",
		new RGB(144, 144, 144), false, false);
	TextStylingPreference DOC_COMMENT = new TextStylingPreference(PREFIX + "doc_comment",
		new RGB(65, 95, 185), false, false);
	
	TextStylingPreference KEYWORDS = new TextStylingPreference(PREFIX + "keyword",
		new RGB(127, 0, 85), true, false);
	TextStylingPreference KW_BASICTYPES = new TextStylingPreference(PREFIX + "basictypes",
		new RGB(150, 40, 210), false, false);
	TextStylingPreference KW_LITERALS = new TextStylingPreference(PREFIX + "kw_literals",
		new RGB(160, 120, 70), false, false);
	TextStylingPreference ANNOTATIONS = new TextStylingPreference(PREFIX + "annotations",
		new RGB(230, 75, 0), false, false);
	TextStylingPreference OPERATORS = new TextStylingPreference(PREFIX + "operators",
		new RGB(0, 0, 0), false, false); // Not supported yet  
	
	TextStylingPreference CHARACTER_LITERALS = new TextStylingPreference(PREFIX + "character",
		new RGB(0, 170, 10), false, false);
	TextStylingPreference STRING = new TextStylingPreference(PREFIX + "string",
		new RGB(126, 164, 0), false, false);
	TextStylingPreference DELIM_STRING = new TextStylingPreference(PREFIX + "delimstring",
		new RGB(175, 175, 0), false, false);
	
}