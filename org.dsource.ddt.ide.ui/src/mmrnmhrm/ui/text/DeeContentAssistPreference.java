package mmrnmhrm.ui.text;

import mmrnmhrm.ui.DeePlugin;

import org.eclipse.dltk.ui.text.ScriptTextTools;
import org.eclipse.dltk.ui.text.completion.ContentAssistPreference;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.contentassist.ContentAssistant;

public class DeeContentAssistPreference extends ContentAssistPreference {
	
	protected static DeeContentAssistPreference instance = new DeeContentAssistPreference();

	/** Returns the plugin instance. */
	public static DeeContentAssistPreference getDefault() {
		return instance;
	}

	@Override
	protected ScriptTextTools getTextTools() {
		return DeePlugin.getDefault().getTextTools();
	}
	
	@Override
	public void configure(ContentAssistant assistant, IPreferenceStore store) {
		super.configure(assistant, store);
	}

}
