package mmrnmhrm.ui.preferences.pages;

import mmrnmhrm.ui.DeePlugin;
import mmrnmhrm.ui.preferences.DeeSourceColoringConfigurationBlock;

import org.eclipse.dltk.ui.preferences.AbstractConfigurationBlockPreferencePage;
import org.eclipse.dltk.ui.preferences.IPreferenceConfigurationBlock;
import org.eclipse.dltk.ui.preferences.OverlayPreferenceStore;
import org.eclipse.dltk.ui.preferences.PreferencesMessages;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class DeeSourceColoringPreferencePage extends
		AbstractConfigurationBlockPreferencePage {
	
	public final static String PAGE_ID = DeePlugin.EXTENSIONS_IDPREFIX+"preferences.editor.SourceColoring";

	@Override
	protected String getHelpId() {
		return "";
	}

	@Override
	protected void setDescription() {
		String description = PreferencesMessages.DLTKEditorPreferencePage_colors;
		setDescription(description);
	}

	@Override
	protected Label createDescriptionLabel(Composite parent) {
		return null;
	}

	@Override
	protected void setPreferenceStore() {
		setPreferenceStore(DeePlugin.getDefault().getPreferenceStore());
	}

	@Override
	protected IPreferenceConfigurationBlock createConfigurationBlock(
			OverlayPreferenceStore overlayPreferenceStore) {
		return new DeeSourceColoringConfigurationBlock(overlayPreferenceStore);
	}
}