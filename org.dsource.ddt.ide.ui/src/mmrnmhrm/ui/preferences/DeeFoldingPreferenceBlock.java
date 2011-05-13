package mmrnmhrm.ui.preferences;

import org.eclipse.dltk.ui.preferences.OverlayPreferenceStore;
import org.eclipse.dltk.ui.text.folding.SourceCodeFoldingPreferenceBlock;
import org.eclipse.jface.preference.PreferencePage;

public class DeeFoldingPreferenceBlock extends SourceCodeFoldingPreferenceBlock {
	
	public DeeFoldingPreferenceBlock(OverlayPreferenceStore store, PreferencePage page) {
		super(store, page);
	}
	
	@Override
	protected boolean supportsClassFolding() {
		return true;
	}
	
	@Override
	protected String getInitiallyFoldClassesText() {
		return DeePreferencesMessages.Folding_initFoldAggregates;
	}
	
	@Override
	protected String getInitiallyFoldMethodsText() {
		return DeePreferencesMessages.Folding_initFoldFunctions;
	}
	
}
