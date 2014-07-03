package mmrnmhrm.ui.preferences.pages;

import mmrnmhrm.ui.DeeUIPlugin;
import mmrnmhrm.ui.preferences.DeeDocFoldingPreferenceBlock;
import mmrnmhrm.ui.preferences.DeeSourceFoldingPreferenceBlock;

import org.eclipse.dltk.ui.preferences.AbstractConfigurationBlockPreferencePage;
import org.eclipse.dltk.ui.preferences.IPreferenceConfigurationBlock;
import org.eclipse.dltk.ui.preferences.OverlayPreferenceStore;
import org.eclipse.dltk.ui.text.folding.DefaultFoldingPreferenceConfigurationBlock;
import org.eclipse.dltk.ui.text.folding.IFoldingPreferenceBlock;
import org.eclipse.jface.preference.PreferencePage;

public class DeeFoldingPreferencePage extends AbstractConfigurationBlockPreferencePage {
	
	public final static String PAGE_ID = DeeUIPlugin.PLUGIN_ID + ".preferences.editor.folding";
	
	@Override
	protected void setDescription() {
		setDescription(null);
	}
	
	@Override
	protected void setPreferenceStore() {
		setPreferenceStore(DeeUIPlugin.getInstance().getPreferenceStore());
	}
	
	@Override
	protected IPreferenceConfigurationBlock createConfigurationBlock(
			OverlayPreferenceStore overlayPreferenceStore) {
		return new DefaultFoldingPreferenceConfigurationBlock(overlayPreferenceStore, this) {
			
			@Override
			protected IFoldingPreferenceBlock createDocumentationBlock(OverlayPreferenceStore store, PreferencePage page) {
				return new DeeDocFoldingPreferenceBlock(store, page);
			}
			
			@Override
			protected IFoldingPreferenceBlock createSourceCodeBlock(OverlayPreferenceStore store, PreferencePage page) {
				return new DeeSourceFoldingPreferenceBlock(store, page);
			}
			
		};
	}
	
}
