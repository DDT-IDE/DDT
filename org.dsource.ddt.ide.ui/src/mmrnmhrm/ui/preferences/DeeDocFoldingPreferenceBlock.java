package mmrnmhrm.ui.preferences;

import org.eclipse.dltk.ui.preferences.OverlayPreferenceStore;
import org.eclipse.dltk.ui.text.folding.DocumentationFoldingPreferenceBlock;
import org.eclipse.jface.preference.PreferencePage;

public class DeeDocFoldingPreferenceBlock extends DocumentationFoldingPreferenceBlock {
	
	public DeeDocFoldingPreferenceBlock(OverlayPreferenceStore store,
			PreferencePage page) {
		super(store, page);
	}
	
	@Override
	protected String getInitiallyFoldDocsText() {
		return DeePreferencesMessages.Folding_initFoldDoc;
	}
	
	@Override
	protected boolean supportsDocFolding() {
		return true;
	}
	
}
