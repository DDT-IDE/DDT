/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package melnorme.lang.jdt.ui;


/**
 * Preference constants used in the JDT-UI preference store. Clients should only read the
 * JDT-UI preference store using these values. Clients are not allowed to modify the 
 * preference store programmatically.
 * 
 * @since 2.0
  */
public class JDT_PreferenceConstants {

	private JDT_PreferenceConstants() {
	}
	
	/**
	 * The symbolic font name for the font used to display Javadoc 
	 * (value <code>"org.eclipse.jdt.ui.javadocfont"</code>).
	 * 
	 * @since 3.3
	 */
	public final static String APPEARANCE_JAVADOC_FONT= "org.eclipse.jdt.ui.javadocfont"; //$NON-NLS-1$

	
}

