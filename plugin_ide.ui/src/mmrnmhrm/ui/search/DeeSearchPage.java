package mmrnmhrm.ui.search;


import org.dsource.ddt.ide.core.DeeLanguageToolkit;
import org.eclipse.dltk.core.IDLTKLanguageToolkit;
import org.eclipse.dltk.ui.search.ScriptSearchPage;

public class DeeSearchPage extends ScriptSearchPage {
	@Override
	protected IDLTKLanguageToolkit getLanguageToolkit() {
		return DeeLanguageToolkit.getDefault();
	}
}
