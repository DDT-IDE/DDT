package mmrnmhrm.ui.editor;

import mmrnmhrm.ui.DeeUIPlugin;
import _org.eclipse.dltk.internal.ui.editor.ScriptOutlinePage;

public class DeeEditor extends DeeBaseEditor {
	
	@Override
	protected ScriptOutlinePage doCreateOutlinePage() {
		return new DeeOutlinePage(this, DeeUIPlugin.getInstance().getPreferenceStore());
	}
	
}