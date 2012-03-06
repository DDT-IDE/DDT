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
package mmrnmhrm.ui;

import org.dsource.ddt.lang.text.LangAutoEditPreferenceConstants;

public interface DeeUIPreferenceConstants extends LangAutoEditPreferenceConstants {
	
	String ELEMENT_ICONS_STYLE = "elementIcons.style";
	
	public static enum ElementIconsStyle { 
		DDTLEAN, DDT, JDTLIKE;
		
		public static ElementIconsStyle create(String stringValue, ElementIconsStyle defaultValue) {
			if(stringValue == null) {
				return defaultValue;
			}
			try {
				return valueOf(stringValue);
			} catch (IllegalArgumentException e) {
				return defaultValue;
			}
		} 
	}; 
	
}
