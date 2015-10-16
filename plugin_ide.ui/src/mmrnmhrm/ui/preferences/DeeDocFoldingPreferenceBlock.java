package mmrnmhrm.ui.preferences;

import _org.eclipse.dltk.ui.preferences.OverlayPreferenceStore;
import _org.eclipse.dltk.ui.text.folding.DocumentationFoldingPreferenceBlock;
import melnorme.lang.ide.ui.preferences.common.AbstractLangPreferencesPage;
import mmrnmhrm.ui.DeePreferencesMessages;

public class DeeDocFoldingPreferenceBlock extends DocumentationFoldingPreferenceBlock {
	
	public DeeDocFoldingPreferenceBlock(OverlayPreferenceStore store,
			AbstractLangPreferencesPage page) {
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
