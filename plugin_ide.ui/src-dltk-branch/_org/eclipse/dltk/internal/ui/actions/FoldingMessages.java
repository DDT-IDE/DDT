/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package _org.eclipse.dltk.internal.ui.actions;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Class that gives access to the folding messages resource bundle.
 */
public class FoldingMessages {

	private static final ResourceBundle RESOURCE_BUNDLE= ResourceBundle.getBundle(
		FoldingMessages.class.getName());

	private FoldingMessages() {
		// no instance
	}

	/**
	 * Returns the resource string associated with the given key in the resource bundle. If there isn't 
	 * any value under the given key, the key is returned.
	 *
	 * @param key the resource key
	 * @return the string
	 */	
	public static String getString(String key) {
		try {
			return RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}
	
	/**
	 * Returns the resource bundle managed by the receiver.
	 * 
	 * @return the resource bundle
	 *
	 */
	public static ResourceBundle getResourceBundle() {
		return RESOURCE_BUNDLE;
	}
	
	/**
	 * Returns the formatted resource string associated with the given key in the resource bundle. 
	 * <code>MessageFormat</code> is used to format the message. If there isn't  any value 
	 * under the given key, the key is returned.
	 *
	 * @param key the resource key
	 * @param arg the message argument
	 * @return the string
	 */	
	public static String getFormattedString(String key, Object arg) {
		return getFormattedString(key, new Object[] { arg });
	}
	
	/**
	 * Returns the formatted resource string associated with the given key in the resource bundle. 
	 * <code>MessageFormat</code> is used to format the message. If there isn't  any value 
	 * under the given key, the key is returned.
	 *
	 * @param key the resource key
	 * @param args the message arguments
	 * @return the string
	 */	
	public static String getFormattedString(String key, Object[] args) {
		return MessageFormat.format(getString(key), args);	
	}	
}
