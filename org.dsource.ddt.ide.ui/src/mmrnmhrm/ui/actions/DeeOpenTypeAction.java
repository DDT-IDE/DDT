package mmrnmhrm.ui.actions;

import mmrnmhrm.ui.DeeUILanguageToolkit;

import org.eclipse.dltk.ui.IDLTKUILanguageToolkit;
import org.eclipse.dltk.ui.actions.OpenTypeAction;

public class DeeOpenTypeAction extends OpenTypeAction {
	@Override
	protected IDLTKUILanguageToolkit getUILanguageToolkit() {
		return DeeUILanguageToolkit.getDefault();
	}
}