package mmrnmhrm.ui.actions;

import mmrnmhrm.lang.ui.AbstractWorkbenchWindowActionDelegate;
import mmrnmhrm.ui.DeeUIPlugin;

import org.eclipse.jface.action.IAction;
import org.eclipse.search.ui.NewSearchUI;

public class DeeOpenSearchPageAction extends AbstractWorkbenchWindowActionDelegate {
	
	private static final String DEE_SEARCH_PAGE_ID = DeeUIPlugin.EXTENSIONS_IDPREFIX+"DeeSearchPage";
	
	@Override
	public void run(IAction action) {
		if (window == null || window.getActivePage() == null) {
			beep();
			return;
		}
		
		NewSearchUI.openSearchDialog(window, DEE_SEARCH_PAGE_ID);
	}
	
}