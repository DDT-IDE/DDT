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

import melnorme.lang.ide.ui.preferences.common.PreferencesPageContext;
import melnorme.lang.ide.ui.text.coloring.AbstractSourceColoringConfigurationBlock;
import melnorme.util.swt.jface.LabeledTreeElement;
import mmrnmhrm.ui.text.DeeColorPreferences;

public class DeeSourceColoringConfigurationBlock extends AbstractSourceColoringConfigurationBlock {
	
	private static final String PREVIEW_FILE_NAME = "SourceColoringPreviewFile.d";
	
	public DeeSourceColoringConfigurationBlock(PreferencesPageContext prefContext) {
		super(prefContext);
	}
	
	@Override
	protected LabeledTreeElement[] createTreeElements() {
		return array(
			new SourceColoringCategory("Source", array(
				new SourceColoringElement("Default", DeeColorPreferences.DEFAULT),
				new SourceColoringElement("Keywords", DeeColorPreferences.KEYWORDS),
				new SourceColoringElement("Keywords - Primitives", DeeColorPreferences.KW_NATIVE_TYPES),
				new SourceColoringElement("Keywords - true/false/null", DeeColorPreferences.KW_LITERALS),
				new SourceColoringElement("Characters", DeeColorPreferences.CHARACTER),
				new SourceColoringElement("Strings", DeeColorPreferences.STRING),
				new SourceColoringElement("Delimited Strings", DeeColorPreferences.DELIM_STRING),
				new SourceColoringElement("Numbers", DeeColorPreferences.NUMBER),
				new SourceColoringElement("@Annotations", DeeColorPreferences.ANNOTATIONS)
			)),
			new SourceColoringCategory("Comments", array(
				new SourceColoringElement("Comment", DeeColorPreferences.COMMENT),
				new SourceColoringElement("Doc Comment", DeeColorPreferences.DOC_COMMENT)
			))
		);
	}
	
	@Override
	protected InputStream getPreviewContentAsStream() {
		return getClass().getResourceAsStream(PREVIEW_FILE_NAME);
	}
	
}