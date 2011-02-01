package mmrnmhrm.ui.preferences;

import org.eclipse.dltk.ui.preferences.EditorConfigurationBlock;
import org.eclipse.dltk.ui.preferences.OverlayPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class DeeEditorConfigurationBlock extends EditorConfigurationBlock {

	public DeeEditorConfigurationBlock(PreferencePage mainPreferencePage,
			OverlayPreferenceStore store, boolean disableSmart, boolean tabAlwaysIndent) {
		super(mainPreferencePage, store, FLAG_TAB_POLICY
				| (disableSmart ? 0 : FLAG_EDITOR_SMART_NAVIGATION)
				| (tabAlwaysIndent ? FLAG_TAB_ALWAYS_INDENT : 0));
	}
	
	public DeeEditorConfigurationBlock(PreferencePage mainPreferencePage, OverlayPreferenceStore store, int flags) {
		super(mainPreferencePage, store, flags);
	}
	
	@Override
	public Control createControl(Composite parent) {
		return super.createControl(parent);
	}

}
