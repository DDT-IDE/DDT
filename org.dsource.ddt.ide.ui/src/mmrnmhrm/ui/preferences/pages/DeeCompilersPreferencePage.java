package mmrnmhrm.ui.preferences.pages;

import mmrnmhrm.ui.DeeUIPlugin;
import mmrnmhrm.ui.preferences.DeeCompilersBlock;

import org.eclipse.dltk.internal.debug.ui.interpreters.InterpretersBlock;
import org.eclipse.dltk.internal.debug.ui.interpreters.ScriptInterpreterPreferencePage;

public class DeeCompilersPreferencePage extends ScriptInterpreterPreferencePage {
	
	public final static String PAGE_ID = DeeUIPlugin.PLUGIN_ID + ".preferences.DeeCompilers";
	
	@Override
	public InterpretersBlock createInterpretersBlock() {
		return new DeeCompilersBlock();
	}
}
