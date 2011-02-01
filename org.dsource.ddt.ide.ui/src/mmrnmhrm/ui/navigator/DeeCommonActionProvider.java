package mmrnmhrm.ui.navigator;

import mmrnmhrm.core.model.ModelUtil;
import mmrnmhrm.ui.actions.OperationsManager;
import mmrnmhrm.ui.editor.DeeEditor;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.navigator.CommonActionProvider;
import org.eclipse.ui.navigator.ICommonActionConstants;
import org.eclipse.ui.navigator.ICommonActionExtensionSite;
import org.eclipse.ui.navigator.ICommonMenuConstants;
import org.eclipse.ui.navigator.ICommonViewerWorkbenchSite;

public class DeeCommonActionProvider extends CommonActionProvider {
	
	public static class OpenFromExplorerAction extends Action {
		private IWorkbenchPage page;
		private ISelectionProvider selProvider;
		private IFile file;
		
		public OpenFromExplorerAction(IWorkbenchPage page, ISelectionProvider selProvider) {
			this.page = page;
			this.selProvider = selProvider;
		}
		
		@Override
		public boolean isEnabled() {
			ISelection selection = selProvider.getSelection();
			if(selection.isEmpty())
				return false;
			
			IStructuredSelection sel = (IStructuredSelection) selection;
			if(sel.size() == 1 && sel.getFirstElement() instanceof ISourceModule) {
				file = ModelUtil.getSourceModuleFile((ISourceModule) sel.getFirstElement()); 
				return true;
			}
			return false;
		}
		
		@Override
		public void run() {
			OperationsManager.executeOperation(new IWorkspaceRunnable() {
				@Override
				public void run(IProgressMonitor monitor) throws CoreException {
					IDE.openEditor(page, file, DeeEditor.EDITOR_ID);
				}
			}, "Open Element");
		}
	}
	
	private OpenFromExplorerAction openAction;

	@Override
	public void init(ICommonActionExtensionSite site) {
		
		if (site.getViewSite() instanceof ICommonViewerWorkbenchSite) {
			ICommonViewerWorkbenchSite workbenchSite;
			workbenchSite = (ICommonViewerWorkbenchSite) site.getViewSite();
			
			openAction = new OpenFromExplorerAction(workbenchSite.getPage(),
					workbenchSite.getSelectionProvider());
		}

	}
	
	@Override
	public void fillActionBars(IActionBars actionBars) {
		if(openAction.isEnabled())
			actionBars.setGlobalActionHandler(ICommonActionConstants.OPEN,
					openAction);
	}
	
	@Override
	public void fillContextMenu(IMenuManager menu) {
		if(openAction.isEnabled())
			menu.appendToGroup(ICommonMenuConstants.GROUP_OPEN, openAction);
	}
	
}
