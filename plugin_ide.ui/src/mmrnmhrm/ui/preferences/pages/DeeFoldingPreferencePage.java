/*******************************************************************************
 * Copyright (c) 2015 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package mmrnmhrm.ui.preferences.pages;

import static melnorme.utilbox.core.CoreUtil.array;

import _org.eclipse.dltk.ui.preferences.OverlayPreferenceStore;
import _org.eclipse.dltk.ui.text.folding.DefaultFoldingPreferenceConfigurationBlock;
import _org.eclipse.dltk.ui.text.folding.IFoldingPreferenceBlock;
import melnorme.lang.ide.ui.preferences.common.AbstractLangPreferencesPage;
import melnorme.lang.ide.ui.preferences.common.AbstractPreferencesBlockPrefPage;
import mmrnmhrm.ui.DeeUIPlugin;
import mmrnmhrm.ui.preferences.DeeDocFoldingPreferenceBlock;
import mmrnmhrm.ui.preferences.DeeSourceFoldingPreferenceBlock;

public class DeeFoldingPreferencePage extends AbstractPreferencesBlockPrefPage {
	
	public final static String PAGE_ID = DeeUIPlugin.PLUGIN_ID + ".PreferencePages.Editor.Folding";
	
	public DeeFoldingPreferencePage() {
		super();
	}
	
	@Override
	protected String getHelpId() {
		return null;
	}
	
	@Override
	protected DefaultFoldingPreferenceConfigurationBlock init_createPreferencesBlock() {
		setPreferenceStore(DeeUIPlugin.getInstance().getPreferenceStore());
		
		/*FIXME: BUG here*/
		OverlayPreferenceStore overlayStore = new OverlayPreferenceStore(getPreferenceStore(), array());
		
		return new DefaultFoldingPreferenceConfigurationBlock(overlayStore, this) {
			
			@Override
			protected IFoldingPreferenceBlock createDocumentationBlock(OverlayPreferenceStore store, 
					AbstractLangPreferencesPage page) {
				return new DeeDocFoldingPreferenceBlock(store, page);
			}
			
			@Override
			protected IFoldingPreferenceBlock createSourceCodeBlock(OverlayPreferenceStore store, 
					AbstractLangPreferencesPage page) {
				return new DeeSourceFoldingPreferenceBlock(store, page);
			}
			
		};
	}
	
}