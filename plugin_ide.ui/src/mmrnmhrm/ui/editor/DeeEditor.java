package mmrnmhrm.ui.editor;

import melnorme.lang.ide.ui.editor.text.LangPairMatcher;
import mmrnmhrm.ui.DeeUIPlugin;
import _org.eclipse.dltk.internal.ui.editor.ScriptOutlinePage;

public class DeeEditor extends DeeBaseEditor {
	
	@Override
	protected ScriptOutlinePage doCreateOutlinePage() {
		return new DeeOutlinePage(this, DeeUIPlugin.getInstance().getPreferenceStore());
	}
	
	@Override
	protected LangPairMatcher init_createBracketMatcher() {
		return new LangPairMatcher("{}[]()".toCharArray());
	}
	
}