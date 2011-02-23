/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.eclipse.dltk.ruby.internal.ui.text;

import mmrnmhrm.ui.internal.text.LangAutoEditsPreferencesAdapter;

import org.eclipse.dltk.ui.CodeFormatterConstants;
import org.eclipse.dltk.ui.text.util.AutoEditUtils;
import org.eclipse.dltk.ui.text.util.ITabPreferencesProvider;
import org.eclipse.dltk.ui.text.util.TabStyle;
import org.eclipse.jface.preference.IPreferenceStore;

public class RubyPreferenceInterpreter extends LangAutoEditsPreferencesAdapter implements ITabPreferencesProvider {
	
	public RubyPreferenceInterpreter(IPreferenceStore store) {
		super(store);
	}
	
	public int getTabSize() {
		return store.getInt(CodeFormatterConstants.FORMATTER_TAB_SIZE);
	}

	public String getIndentByVirtualSize(int size) {
		if (getTabStyle() == TabStyle.SPACES) {
			return AutoEditUtils.getNSpaces(size);
		} else {
			int tabs = size / getTabSize();
			int leftover = size - tabs * getTabSize();
			return AutoEditUtils.getNChars(tabs, '\t')
					+ AutoEditUtils.getNSpaces(leftover);
		}
	}

}
