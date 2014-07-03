package mmrnmhrm.ui.actions;

import mmrnmhrm.ui.DeeUILanguageToolkit;

import org.eclipse.dltk.ui.IDLTKUILanguageToolkit;
import org.eclipse.dltk.ui.actions.OpenTypeInHierarchyAction;

public class DeeOpenTypeInHierarchyAction extends OpenTypeInHierarchyAction {
	
	@Override
	protected IDLTKUILanguageToolkit getLanguageToolkit() {
		return DeeUILanguageToolkit.getDefault();
	}
}
