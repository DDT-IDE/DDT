/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package mmrnmhrm.ui.internal.text;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import org.eclipse.dltk.ui.CodeFormatterConstants;
import org.eclipse.dltk.ui.PreferenceConstants;
import org.eclipse.dltk.ui.text.util.AutoEditUtils;
import org.eclipse.dltk.ui.text.util.TabStyle;
import org.eclipse.jface.preference.IPreferenceStore;

// Originall RubyPreferencesInterpreter
public class LangAutoEditsPreferencesAdapter {
	
	protected final IPreferenceStore store;
	
	public LangAutoEditsPreferencesAdapter(IPreferenceStore store) {
		this.store = store;
	}
	
	public boolean isSmartMode() {
		return store.getBoolean(PreferenceConstants.EDITOR_SMART_INDENT);
	}
	
	public boolean isSmartPaste() {
		return store.getBoolean(PreferenceConstants.EDITOR_SMART_PASTE);
	}
	
	public boolean closeBlocks() {
		return closeBraces();
	}

	public boolean closeBraces() {
		return store.getBoolean(PreferenceConstants.EDITOR_CLOSE_BRACES);
	}
	
	public TabStyle getTabStyle() {
		return TabStyle.forName(store
				.getString(CodeFormatterConstants.FORMATTER_TAB_CHAR),
				TabStyle.TAB);
	}

	public String getIndent() {
		if (getTabStyle() == TabStyle.SPACES) {
			return AutoEditUtils.getNSpaces(getIndentSize());
		} else
			return "\t"; //$NON-NLS-1$
	}
	
	public int getIndentSize() {
		return store.getInt(CodeFormatterConstants.FORMATTER_INDENTATION_SIZE);
	}
	
	public final String getIndent(int count) {
		return stringNTimes(getIndent(), count);
	}
	
	public static String stringNTimes(String indent, int count) {
		assertTrue(count >=0);
		StringBuffer result = new StringBuffer(indent.length() * count);
		for (int i = 0; i < count; i++) {
			result.append(indent);
		}
		return result.toString();
	}

	
}
