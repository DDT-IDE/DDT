/*******************************************************************************
 * Copyright (c) 2008 xored software, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Alex Panchenko)
 *******************************************************************************/
package _org.eclipse.dltk.ui.preferences;

/**
 * Preference value converter. Should be used when value should be converted
 * between text field and preference store.
 */
public interface ITextConverter {

	/**
	 * Convert value from the internal format to the format suitable to display
	 * in the text box
	 * 
	 * @param value
	 * @return
	 */
	String convertPreference(String value);

	/**
	 * Convert value entered into the text box to the internal format.
	 * 
	 * @param input
	 * @return
	 */
	String convertInput(String input);

}
