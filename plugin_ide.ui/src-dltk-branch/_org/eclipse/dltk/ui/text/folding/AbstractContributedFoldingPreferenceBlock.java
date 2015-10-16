package _org.eclipse.dltk.ui.text.folding;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;

import _org.eclipse.dltk.ui.preferences.ImprovedAbstractConfigurationBlock;
import _org.eclipse.dltk.ui.preferences.OverlayPreferenceStore;
import _org.eclipse.dltk.ui.preferences.OverlayPreferenceStore.OverlayKey;
import _org.eclipse.dltk.ui.preferences.PreferencesMessages;
import _org.eclipse.dltk.ui.util.SWTFactory;
import melnorme.lang.ide.ui.preferences.common.AbstractLangPreferencesPage;

/**
 * Abstract base class that may be used to create
 * <code>IFoldingPreferenceBlock</code> implenentations.
 */
public abstract class AbstractContributedFoldingPreferenceBlock extends
		ImprovedAbstractConfigurationBlock {

	public AbstractContributedFoldingPreferenceBlock(
			OverlayPreferenceStore store, AbstractLangPreferencesPage page) {
		super(store, page);
	}
	
	@Override
	public int getPreferredLayoutColumns() {
		return 1;
	}
	
	@Override
	protected void doCreateContents(Composite topControl) {
		createControl(topControl);
	}
	
	public Control createControl(Composite parent) {
		// TODO: remove redundant topControl creation.
		Composite composite = SWTFactory.createComposite(parent, parent.getFont(), 1, 1, GridData.FILL);

		createOptionsControl(composite);

		SWTFactory.createHorizontalSpacer(composite, 1);

		Group initFoldGroup = SWTFactory.createGroup(composite,
				PreferencesMessages.FoldingConfigurationBlock_initiallyFold, 1,
				1, GridData.FILL_HORIZONTAL);

		addInitiallyFoldOptions(initFoldGroup);

		return composite;
	}

	/**
	 * Create language speific folding options
	 * 
	 * @param composite
	 *            composite the option controls should be added to
	 */
	protected abstract void createOptionsControl(Composite composite);

	/**
	 * Adds the checkboxes that will be used to control 'initially fold'
	 * options.
	 * 
	 * @param group
	 *            composite the checkboxes will be added to
	 */
	protected abstract void addInitiallyFoldOptions(Group group);

	/**
	 * Adds the folding option preference overlay keys.
	 */
	protected abstract void addOverlayKeys(List<OverlayKey> keys);

	@Override
	protected final List<OverlayKey> createOverlayKeys() {
		ArrayList<OverlayKey> keys = new ArrayList<OverlayKey>();
		addOverlayKeys(keys);
		return keys;
	}

	/**
	 * Convienence method to create and bind a checkbox control
	 * 
	 * @param parent
	 *            parent composite
	 * @param text
	 *            checkbox text
	 * @param key
	 *            preference key
	 */
	protected Button createCheckBox(Composite parent, String text, String key) {
		Button button = SWTFactory.createCheckButton(parent, text, 1);
		bindControl(button, key);
		return button;
	}

	/**
	 * Convienence method to create and bind a radio button control
	 * 
	 * @param parent
	 *            parent composite
	 * @param text
	 *            radio button text
	 * @param key
	 *            preference key
	 * @param value
	 *            value that will be saved to the preference store if the radio
	 *            button is enabled
	 */
	protected Button createRadioButton(Composite parent, String text,
			String key, Object value) {
		Button button = SWTFactory.createRadioButton(parent, text);
		bindControl(button, key, value);
		return button;
	}
}
