package _org.eclipse.dltk.ui.text.folding;

import java.util.List;

import org.eclipse.dltk.ui.PreferenceConstants;
import org.eclipse.dltk.ui.preferences.PreferencesMessages;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

import _org.eclipse.dltk.ui.preferences.OverlayPreferenceStore;
import _org.eclipse.dltk.ui.preferences.OverlayPreferenceStore.OverlayKey;

public class DocumentationFoldingPreferenceBlock extends
		AbstractContributedFoldingPreferenceBlock {

	public DocumentationFoldingPreferenceBlock(OverlayPreferenceStore store,
			PreferencePage page) {
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
				OverlayPreferenceStore.BOOLEAN,
				PreferenceConstants.EDITOR_COMMENT_FOLDING_JOIN_NEWLINES));

		keys.add(new OverlayPreferenceStore.OverlayKey(
				OverlayPreferenceStore.BOOLEAN,
				PreferenceConstants.EDITOR_FOLDING_INIT_COMMENTS));

		keys.add(new OverlayPreferenceStore.OverlayKey(
				OverlayPreferenceStore.BOOLEAN,
				PreferenceConstants.EDITOR_FOLDING_INIT_HEADER_COMMENTS));

		keys.add(new OverlayPreferenceStore.OverlayKey(
				OverlayPreferenceStore.BOOLEAN,
				PreferenceConstants.EDITOR_FOLDING_INIT_DOCS));
	}

	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * Sub-classes overriding this method to add additional 'initially fold'
	 * options must also invoke this method.
	 * </p>
	 */
	@Override
	protected void addInitiallyFoldOptions(Group group) {
		createCheckBox(
				group,
				PreferencesMessages.FoldingConfigurationBlock_initiallyFoldComments,
				PreferenceConstants.EDITOR_FOLDING_INIT_COMMENTS);
		createCheckBox(
				group,
				PreferencesMessages.FoldingConfigurationBlock_initiallyFoldHeaderComments,
				PreferenceConstants.EDITOR_FOLDING_INIT_HEADER_COMMENTS);

		if (supportsDocFolding()) {
			createCheckBox(group, getInitiallyFoldDocsText(),
					PreferenceConstants.EDITOR_FOLDING_INIT_DOCS);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * Sub-classes overriding this method to add additional documentation
	 * folding options must also invoke this method.
	 * </p>
	 */
	@Override
	protected void createOptionsControl(Composite composite) {
		createCheckBox(composite,
				PreferencesMessages.FoldingConfigurationBlock_joinComments,
				PreferenceConstants.EDITOR_COMMENT_FOLDING_JOIN_NEWLINES);
	}

	protected String getInitiallyFoldDocsText() {
		return PreferencesMessages.FoldingConfigurationBlock_initiallyFoldDocs;
	}

	protected boolean supportsDocFolding() {
		return false;
	}
}
