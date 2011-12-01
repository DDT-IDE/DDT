/*******************************************************************************
 * Copyright (c) 2011, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package org.dsource.ddt.lang.text;

import melnorme.utilbox.core.Function;

public class LangTextUtil {
	
	public static final Function<String, char[]> STRING_to_CHAR_ARRAY = new Function<String, char[]>() {
		@Override
		public char[] evaluate(String obj) {
			return obj.toCharArray();
		}
	};
	
}
