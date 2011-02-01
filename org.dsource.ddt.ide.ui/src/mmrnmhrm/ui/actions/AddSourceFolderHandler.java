package mmrnmhrm.ui.actions;

import melnorme.utilbox.core.Assert;
import mmrnmhrm.core.DeeCore;
import mmrnmhrm.core.model.ModelUtil;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;


/**
 */
public class AddSourceFolderHandler extends AbstractHandler {
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		ISelection selection = HandlerUtil.getCurrentSelection(event);
		if(!(selection instanceof IStructuredSelection))
			return null;
		
		final IStructuredSelection sel = (IStructuredSelection) selection;
		Assert.isTrue(sel.size() >= 1);
		
		final IResource res = (IResource) sel.getFirstElement();
		
		final IWorkspaceRunnable op = new IWorkspaceRunnable() {
			@Override
			public void run(IProgressMonitor monitor) throws CoreException {
				ModelUtil.addLibraryEntry(res, monitor);
			}
		};
		
		OperationsManager.executeOperation(new IWorkspaceRunnable() {
			@Override
			public void run(IProgressMonitor monitor) throws CoreException {
				DeeCore.run(op, null);
			}
		}, "Add Folder Library To Build Path");
		
		return null;
	}
	
}
