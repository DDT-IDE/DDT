package mmrnmhrm.ui.preferences.pages;

import mmrnmhrm.ui.DeePlugin;
import mmrnmhrm.ui.preferences.DeeCompilersBlock;

import org.eclipse.dltk.internal.debug.ui.interpreters.InterpretersBlock;
import org.eclipse.dltk.internal.debug.ui.interpreters.ScriptInterpreterPreferencePage;

public class DeeCompilersPreferencePage extends ScriptInterpreterPreferencePage {
	
	public final static String PAGE_ID = DeePlugin.EXTENSIONS_IDPREFIX+"preferences.DeeCompilers";

	@Override
	public InterpretersBlock createInterpretersBlock() {
		return new DeeCompilersBlock();
	}
}
