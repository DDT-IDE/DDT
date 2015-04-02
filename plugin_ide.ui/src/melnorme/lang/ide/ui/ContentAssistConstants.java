/*******************************************************************************
 * Copyright (c) 2015, 2015 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package melnorme.lang.ide.ui;

import melnorme.lang.ide.ui.text.completion.ContentAssistConstants_Default;

import org.eclipse.dltk.ui.PreferenceConstants;

public interface ContentAssistConstants extends ContentAssistConstants_Default {
	
	// Override to use DLTK values
	
	String AUTOACTIVATION = PreferenceConstants.CODEASSIST_AUTOACTIVATION;
	String AUTOACTIVATION_DELAY = PreferenceConstants.CODEASSIST_AUTOACTIVATION_DELAY;
	String PROPOSALS_FOREGROUND = PreferenceConstants.CODEASSIST_PROPOSALS_FOREGROUND;
	String PROPOSALS_BACKGROUND = PreferenceConstants.CODEASSIST_PROPOSALS_BACKGROUND;
	String PARAMETERS_FOREGROUND = PreferenceConstants.CODEASSIST_PARAMETERS_FOREGROUND;
	String PARAMETERS_BACKGROUND = PreferenceConstants.CODEASSIST_PARAMETERS_BACKGROUND;
	String AUTOINSERT = PreferenceConstants.CODEASSIST_AUTOINSERT;
	
	String PREFIX_COMPLETION = PreferenceConstants.CODEASSIST_PREFIX_COMPLETION;
	
	String AUTOACTIVATION_TRIGGERS = PreferenceConstants.CODEASSIST_AUTOACTIVATION_TRIGGERS;
	
}