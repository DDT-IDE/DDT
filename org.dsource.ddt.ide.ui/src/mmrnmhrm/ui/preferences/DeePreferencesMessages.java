/*******************************************************************************
 * Copyright (c) 2007, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package mmrnmhrm.ui.preferences;

import org.eclipse.osgi.util.NLS;

public final class DeePreferencesMessages extends NLS {
	
//	private static final String BUNDLE_NAME = DeePreferencesMessages.class.getSimpleName();
	
	
	
	public static final String EditorPreferencePage_AutoEdits = 
		"AutoEdits";
	
	public static final String LangSmartTypingConfigurationBlock_autoclose_title = 
		"Automatically close:";
	
	public static final String LangSmartTypingConfigurationBlock_closeBrackets =
		"(Parentheses),[square] brackets";
	public static final String LangSmartTypingConfigurationBlock_closeBraces =
		"{Braces}";
	public static final String LangSmartTypingConfigurationBlock_closeStrings =
		"\"Strings\"";
	
	public static final String EditorPreferencePage_smartIndent = 
		"Smart indent on newline";
	public static final String EditorPreferencePage_smartDeIndent= 
		"Smart indent deletion (delete full indent)";
	public static final String EditorPreferencePage_considerParenthesesAsBlocks = 
		"Consider (parentheses) the same as {braces} for block smart indent";
	
	
	private DeePreferencesMessages() {
		// Do not instantiate
	}
	
	/*static {
		NLS.initializeMessages(BUNDLE_NAME, DeePreferencesMessages.class);
	}*/
}