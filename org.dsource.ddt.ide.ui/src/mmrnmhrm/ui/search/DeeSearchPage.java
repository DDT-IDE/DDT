package mmrnmhrm.ui.search;

import mmrnmhrm.core.dltk.DeeLanguageToolkit;

import org.eclipse.dltk.core.IDLTKLanguageToolkit;
import org.eclipse.dltk.ui.search.ScriptSearchPage;

public class DeeSearchPage extends ScriptSearchPage {
	@Override
	protected IDLTKLanguageToolkit getLanguageToolkit() {
		return DeeLanguageToolkit.getDefault();
	}
}
