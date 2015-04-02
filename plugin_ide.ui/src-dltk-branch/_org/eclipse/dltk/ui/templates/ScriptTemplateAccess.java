package _org.eclipse.dltk.ui.templates;

import java.io.IOException;

import org.eclipse.dltk.compiler.CharOperation;
import org.eclipse.dltk.ui.DLTKUIPlugin;
import org.eclipse.dltk.ui.templates.TemplateMessages;
import org.eclipse.dltk.ui.text.templates.ITemplateAccess;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.templates.ContextTypeRegistry;
import org.eclipse.jface.text.templates.persistence.TemplateStore;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.editors.text.templates.ContributionContextTypeRegistry;
import org.eclipse.ui.editors.text.templates.ContributionTemplateStore;

public abstract class ScriptTemplateAccess implements ITemplateAccess {

	private ContextTypeRegistry fRegistry;
	private TemplateStore fStore;

	@Override
	public TemplateStore getTemplateStore() {
		if (fStore == null) {
			fStore = new ContributionTemplateStore(getContextTypeRegistry(),
					getPreferenceStore(), getCustomTemplatesKey());
			loadTemplates();
		}
		return fStore;
	}

	@Override
	public ContextTypeRegistry getContextTypeRegistry() {
		if (fRegistry == null) {
			fRegistry = createContextTypeRegistry();
		}
		return fRegistry;
	}

	protected ContextTypeRegistry createContextTypeRegistry() {
		final ContributionContextTypeRegistry registry = new ContributionContextTypeRegistry();
		for (String id : getContextTypeIds()) {
			registry.addContextType(id);
		}
		return registry;
	}

	/**
	 * Used to return the only context type id, now it is deprecated in favour
	 * of {@link #getContextTypeIds()}.
	 */
	@Deprecated
	protected String getContextTypeId() {
		return null;
	}

	/**
	 * This function should be overridden to return the list of context type
	 * ids.
	 */
	protected String[] getContextTypeIds() {
		final String contextTypeId = getContextTypeId();
		return contextTypeId != null ? new String[] { contextTypeId }
				: CharOperation.NO_STRINGS;
	}

	protected abstract String getCustomTemplatesKey();

	protected abstract IPreferenceStore getPreferenceStore();

	@Override
	public IPreferenceStore getTemplatePreferenceStore() {
		return getPreferenceStore();
	}

	private void loadTemplates() {
		try {
			fStore.load();
		} catch (IOException e) {
			final String msg = NLS
					.bind(TemplateMessages.ScriptTemplateAccess_unableToLoadTemplateStore,
							e);
			DLTKUIPlugin.logErrorMessage(msg, e);
		}
	}
	
}