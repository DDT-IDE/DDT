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

import melnorme.lang.ide.ui.text.coloring.TextStyling;
import melnorme.lang.ide.ui.text.coloring.ThemedTextStylingPreference;

public interface DeeColorPreferences {
	
	String PREFIX = "editor.coloring2."; 
	
	ThemedTextStylingPreference DEFAULT = new ThemedTextStylingPreference(PREFIX + "default",
		new TextStyling(new RGB(  0,   0, 0), false, false),
		new TextStyling(new RGB(230,230,230), false, false));
	
	ThemedTextStylingPreference COMMENT = new ThemedTextStylingPreference(PREFIX + "comment",
		new TextStyling(new RGB(144, 144, 144), false, false),
		new TextStyling(new RGB(165, 165, 165), false, false));
	ThemedTextStylingPreference DOC_COMMENT = new ThemedTextStylingPreference(PREFIX + "doc_comment",
		new TextStyling(new RGB( 65,  95, 185), false, false),
		new TextStyling(new RGB(110, 135, 205), false, false));
	
	ThemedTextStylingPreference KEYWORDS = new ThemedTextStylingPreference(PREFIX + "keyword",
		new TextStyling(new RGB(127, 0,  85), true, false),
		new TextStyling(new RGB(210, 0, 140), true, false));
	ThemedTextStylingPreference KW_LITERALS = new ThemedTextStylingPreference(PREFIX + "kw_literals",
		new TextStyling(new RGB(127,   0,  85), false, false),
		new TextStyling(new RGB(255, 150,  55), false, false));
	ThemedTextStylingPreference KW_NATIVE_TYPES = new ThemedTextStylingPreference(PREFIX + "basictypes",
		new TextStyling(new RGB(160,  90, 170), false, false),
		new TextStyling(new RGB(170, 130, 190), false, false));
	ThemedTextStylingPreference ANNOTATIONS = new ThemedTextStylingPreference(PREFIX + "annotations",
		new TextStyling(new RGB(200, 40, 20), false, false),
		new TextStyling(new RGB(240, 80, 80), false, false));
	ThemedTextStylingPreference OPERATORS = new ThemedTextStylingPreference(PREFIX + "operators",
		new TextStyling(new RGB(  0,  0,  0), false, false),
		new TextStyling(new RGB(230,230,230), false, false)); // Not supported yet  
	
	ThemedTextStylingPreference NUMBER = new ThemedTextStylingPreference(PREFIX + "number",
		new TextStyling(new RGB(  0,   0, 0), false, false),
		new TextStyling(new RGB(230,230,230), false, false));
	ThemedTextStylingPreference CHARACTER = new ThemedTextStylingPreference(PREFIX + "character",
		new TextStyling(new RGB(140, 175, 0), false, false),
		new TextStyling(new RGB(150, 190, 0), false, false));
	ThemedTextStylingPreference STRING = new ThemedTextStylingPreference(PREFIX + "string",
		new TextStyling(new RGB(150, 150, 0), false, false),
		new TextStyling(new RGB(220, 220, 0), false, false));
	ThemedTextStylingPreference DELIM_STRING = new ThemedTextStylingPreference(PREFIX + "delimstring",
		new TextStyling(new RGB(150, 150, 0), false, false),
		new TextStyling(new RGB(220, 220, 0), false, false));
	
}