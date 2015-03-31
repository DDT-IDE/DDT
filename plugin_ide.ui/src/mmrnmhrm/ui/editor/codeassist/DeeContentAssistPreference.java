package mmrnmhrm.ui.editor.codeassist;

import _org.eclipse.dltk.ui.text.completion.ContentAssistPreference;

public class DeeContentAssistPreference extends ContentAssistPreference {
	
	protected static DeeContentAssistPreference instance = new DeeContentAssistPreference();

	/** Returns the plugin instance. */
	public static DeeContentAssistPreference getDefault() {
		return instance;
	}

}