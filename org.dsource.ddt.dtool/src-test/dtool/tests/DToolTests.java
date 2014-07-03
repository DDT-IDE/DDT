/*******************************************************************************
 * Copyright (c) 2011, 2014 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package dtool.tests;

import melnorme.utilbox.core.DevelopmentCodeMarkers.Tests_HasExternalDependencies;

public class DToolTests implements Tests_HasExternalDependencies {
	
	public static final String DTOOL_PREFIX = "DTool.";
	public static final boolean TESTS_LITE_MODE = getSystemProperty("TestsLiteMode", false); 
	
	public static String getSystemProperty(String propName, String defaultValue) {
		String propValue = System.getProperty(DTOOL_PREFIX + propName);
		if(propValue == null) {
			return defaultValue;
		}
		return propValue;
	}
	
	public static boolean getSystemProperty(String propName, boolean defaultValue) {
		String propValue = System.getProperty(DTOOL_PREFIX + propName);
		if(propValue == null) {
			return defaultValue;
		}
		return propValue != null;
	}

}