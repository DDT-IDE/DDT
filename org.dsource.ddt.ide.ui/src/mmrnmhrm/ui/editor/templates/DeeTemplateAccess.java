package mmrnmhrm.ui.editor.templates;

import mmrnmhrm.ui.DeeUI;

import org.eclipse.dltk.ui.templates.ScriptTemplateAccess;
import org.eclipse.jface.preference.IPreferenceStore;

public class DeeTemplateAccess extends ScriptTemplateAccess {
	
	private static final String CUSTOM_TEMPLATES_KEY = "mmrnmhrm.Templates";
	
	private static DeeTemplateAccess instance;
	
	public static DeeTemplateAccess getInstance() {
		if(instance == null) {
			instance = new DeeTemplateAccess();
		}
		return instance;
	}
	
	@Override
	protected String getContextTypeId() {
		return DeeUniversalTemplateContextType.CONTEXT_TYPE_ID;
	}
	
	@Override
	protected String getCustomTemplatesKey() {
		return CUSTOM_TEMPLATES_KEY;
	}
	
	@Override
	protected IPreferenceStore getPreferenceStore() {
		return DeeUI.getDefault().getPreferenceStore();
	}
}