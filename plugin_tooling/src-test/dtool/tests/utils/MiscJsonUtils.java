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
package dtool.tests.utils;

import melnorme.utilbox.misc.StringUtil;

// stupid simple helpers
public class MiscJsonUtils {
	
	public static String jsStringEntry(String key, String value) {
		return "\""+key+"\" : \""+value+"\",";
	}
	
	public static String jsEntryValue(String key, Object value) {
		return "\""+key+"\" : "+value.toString()+",";
	}
	
	public static String jsDocument(String... entries) {
		return "{ " + StringUtil.collToString(entries, "\n") + "    \"dummyEndKey\" : null}";
	}
	
}