package mmrnmhrm.ui.preferences.pages;

import mmrnmhrm.dltk.internal.debug.ui.interpreters.ScriptInterpreterPreferencePage;
import mmrnmhrm.ui.DeeUIPlugin;
import mmrnmhrm.ui.preferences.DeeCompilersBlock;

public class DeeCompilersPreferencePage extends ScriptInterpreterPreferencePage {
	
	public final static String PAGE_ID = DeeUIPlugin.PLUGIN_ID + ".preferences.DeeCompilers";
	
	@Override
	public DeeCompilersBlock createInterpretersBlock() {
		return new DeeCompilersBlock();
	}
}
