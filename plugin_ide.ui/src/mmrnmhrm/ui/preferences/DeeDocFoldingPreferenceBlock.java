package mmrnmhrm.ui.preferences;

import mmrnmhrm.ui.DeePreferencesMessages;

import org.eclipse.jface.preference.PreferencePage;

import _org.eclipse.dltk.ui.preferences.OverlayPreferenceStore;
import _org.eclipse.dltk.ui.text.folding.DocumentationFoldingPreferenceBlock;

public class DeeDocFoldingPreferenceBlock extends DocumentationFoldingPreferenceBlock {
	
	public DeeDocFoldingPreferenceBlock(OverlayPreferenceStore store,
			PreferencePage page) {
		super(store, page);
	}
	
	@Override
	protected boolean supportsDocFolding() {
		return true;
	}
	
	@Override
	protected String getInitiallyFoldDocsText() {
		return DeePreferencesMessages.Folding_initFoldDoc;
	}
	
}
