package _org.eclipse.dltk.ui.text.folding;

import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

import _org.eclipse.dltk.ui.PreferenceConstants;
import _org.eclipse.dltk.ui.preferences.OverlayPreferenceStore;
import _org.eclipse.dltk.ui.preferences.OverlayPreferenceStore.OverlayKey;
import _org.eclipse.dltk.ui.preferences.PreferencesMessages;
import melnorme.lang.ide.ui.preferences.common.AbstractPreferencesEditorsPrefPage;

public abstract class SourceCodeFoldingPreferenceBlock extends
		AbstractContributedFoldingPreferenceBlock {

	public SourceCodeFoldingPreferenceBlock(OverlayPreferenceStore store,
			AbstractPreferencesEditorsPrefPage page) {
		super(store, page);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * Sub-classes overriding this method to add additional overlay keys must
	 * also invoke this method.
	 * </p>
	 */
	@Override
	protected void addOverlayKeys(List<OverlayKey> keys) {
		keys.add(new OverlayPreferenceStore.OverlayKey(
				OverlayPreferenceStore.BOOLEAN, getInitiallyFoldClassesKey()));
		keys.add(new OverlayPreferenceStore.OverlayKey(
				OverlayPreferenceStore.BOOLEAN, getInitiallyFoldMethodsKey()));
	}

	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * Sub-classes should override this method to add additional source block
	 * folding options.
	 * </p>
	 */
	@Override
	protected void createOptionsControl(Composite composite) {
		// empty implementation
	}

	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * Sub-classes overriding this method to add additional 'initially' fold
	 * options also invoke this method.
	 * </p>
	 */
	@Override
	protected void addInitiallyFoldOptions(Group group) {
		/*
		 * don't display the 'initally fold' classes checkbox if the language
		 * doesn't support the notion of a top level class (ie: shell scripts,
		 * javascript, etc)
		 */
		if (supportsClassFolding()) {
			createCheckBox(group, getInitiallyFoldClassesText(),
					getInitiallyFoldClassesKey());
		}

		createCheckBox(group, getInitiallyFoldMethodsText(),
				getInitiallyFoldMethodsKey());
	}

	/**
	 * Returns the preference key used to indicate if classes should be
	 * 'initially' folded.
	 * 
	 * <p>
	 * Sub-classes may override this method to provide an alternative preference
	 * key if they are not using the one in {@link PreferenceConstants}.
	 * </p>
	 */
	protected String getInitiallyFoldClassesKey() {
		return PreferenceConstants.EDITOR_FOLDING_INIT_CLASSES;
	}

	/**
	 * Returns the preference key used to indicate if methods should be
	 * 'initially' folded.
	 * 
	 * <p>
	 * Sub-classes may override this method to provide an alternative preference
	 * key if they are not using the one in {@link PreferenceConstants}.
	 * </p>
	 */
	protected String getInitiallyFoldMethodsKey() {
		return PreferenceConstants.EDITOR_FOLDING_INIT_METHODS;
	}

	protected String getInitiallyFoldMethodsText() {
		return PreferencesMessages.FoldingConfigurationBlock_initiallyFoldMethods;
	}

	protected abstract String getInitiallyFoldClassesText();

	protected boolean supportsClassFolding() {
		return true;
	}
}
