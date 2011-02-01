package mmrnmhrm.ui.editor.templates;

import mmrnmhrm.ui.DeePlugin;

import org.eclipse.dltk.ui.templates.ScriptTemplateAccess;
import org.eclipse.jface.preference.IPreferenceStore;

// TODO: DLTK learn more
/**
 * Provides access to the Ruby template store.
 */
public class RubyTemplateAccess extends ScriptTemplateAccess {
	// Template
	private static final String CUSTOM_TEMPLATES_KEY = "mmrnmhrm.Templates"; //$NON-NLS-1$

	private static RubyTemplateAccess instance;

	public static RubyTemplateAccess getInstance() {
		if (instance == null) {
			instance = new RubyTemplateAccess();
		}
		return instance;
	}

	/*
	 * @see org.eclipse.dltk.ui.templates.ScriptTemplateAccess#getPreferenceStore()
	 */
	@Override
	protected IPreferenceStore getPreferenceStore() {
		return DeePlugin.getDefault().getPreferenceStore();
	}

	/*
	 * @see org.eclipse.dltk.ui.templates.ScriptTemplateAccess#getContextTypeId()
	 */
	@Override
	protected String getContextTypeId() {
		return DeeUniversalTemplateContextType.CONTEXT_TYPE_ID;
	}

	/*
	 * @see org.eclipse.dltk.ui.templates.ScriptTemplateAccess#getCustomTemplatesKey()
	 */
	@Override
	protected String getCustomTemplatesKey() {
		return CUSTOM_TEMPLATES_KEY;
	}
}
