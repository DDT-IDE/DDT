package mmrnmhrm.ui.preferences.pages;

import org.dsource.ddt.ide.core.DeeNature;

import melnorme.lang.ide.dltk.ui.interpreters.CompilersPreferencePage;
import mmrnmhrm.ui.DeeUIPlugin;
import mmrnmhrm.ui.preferences.DeeCompilersBlock;

public class DeeCompilersPreferencePage extends CompilersPreferencePage {
	
	public final static String PAGE_ID = DeeUIPlugin.PLUGIN_ID + ".preferences.DeeCompilers";
	
	@Override
	public DeeCompilersBlock createInterpretersBlock() {
		return new DeeCompilersBlock();
	}
	
	@Override
	protected String getNature() {
		return DeeNature.NATURE_ID;
	}
	
}
