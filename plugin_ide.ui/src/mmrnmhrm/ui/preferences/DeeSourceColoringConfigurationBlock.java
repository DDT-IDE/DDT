/*******************************************************************************
 * Copyright (c) 2014 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package mmrnmhrm.ui.preferences;

import static melnorme.utilbox.core.CoreUtil.array;

import java.io.InputStream;

import melnorme.lang.ide.ui.text.coloring.AbstractSourceColoringConfigurationBlock;
import melnorme.util.swt.jface.LabeledTreeElement;
import mmrnmhrm.ui.text.DeeColorPreferences;

import org.eclipse.jface.preference.IPreferenceStore;

public class DeeSourceColoringConfigurationBlock extends AbstractSourceColoringConfigurationBlock {
	
	private static final String PREVIEW_FILE_NAME = "SourceColoringPreviewFile.d";
	
	protected static final LabeledTreeElement[] treeElements = array(
		new SourceColoringCategory("Source", array(
			new SourceColoringElement("Default", DeeColorPreferences.DEFAULT.key),
			new SourceColoringElement("Keywords", DeeColorPreferences.KEYWORDS.key),
			new SourceColoringElement("Keywords - Basic Types", DeeColorPreferences.BASICTYPES.key),
			new SourceColoringElement("Keywords - Literals", DeeColorPreferences.LITERALS.key),
			new SourceColoringElement("Character Literals", DeeColorPreferences.CHARACTER_LITERALS.key),
			new SourceColoringElement("Strings", DeeColorPreferences.STRING.key),
			new SourceColoringElement("Delimited String", DeeColorPreferences.DELIM_STRING.key),
			new SourceColoringElement("@Annotations", DeeColorPreferences.ANNOTATIONS.key)
		)),
		new SourceColoringCategory("Comments", array(
			new SourceColoringElement("Comment", DeeColorPreferences.COMMENT.key),
			new SourceColoringElement("Doc Comment", DeeColorPreferences.DOCCOMMENT.key)
		))
	);
	
	public DeeSourceColoringConfigurationBlock(IPreferenceStore store) {
		super(store);
	}
	
	@Override
	public void dispose() {
		super.dispose();
	}
	
	@Override
	protected LabeledTreeElement[] getTreeElements() {
		return treeElements;
	}
	
	@Override
	protected InputStream getPreviewContentAsStream() {
		return getClass().getResourceAsStream(PREVIEW_FILE_NAME);
	}
	
}