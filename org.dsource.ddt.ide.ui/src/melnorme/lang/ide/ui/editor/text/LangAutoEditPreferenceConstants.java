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
package melnorme.lang.ide.ui.editor.text;

import org.eclipse.dltk.ui.PreferenceConstants;

public interface LangAutoEditPreferenceConstants {
	
	String AE_SMART_INDENT = PreferenceConstants.EDITOR_SMART_INDENT;
	String AE_SMART_DEINDENT = "autoedit.smart_deindent";
	String AE_PARENTHESES_AS_BLOCKS = "autoedit.parentheses_as_blocks";
	
	String AE_CLOSE_STRINGS = PreferenceConstants.EDITOR_CLOSE_STRINGS;
	String AE_CLOSE_BRACKETS = PreferenceConstants.EDITOR_CLOSE_BRACKETS;
	String AE_CLOSE_BRACES = PreferenceConstants.EDITOR_CLOSE_BRACES;
	
}