package mmrnmhrm.ui.actions;

import melnorme.utilbox.core.Assert;
import mmrnmhrm.core.DeeCore;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;


public class SampleTest1Handler extends AbstractHandler {
	
	
	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		ISelection selection = HandlerUtil.getCurrentSelection(event);
		if(!(selection instanceof IStructuredSelection))
			return null;
		
		final IStructuredSelection sel = (IStructuredSelection) selection;
		Assert.isTrue(sel.size() >= 1);
		
		final IResource res = (IResource) sel.getFirstElement();
		
		final IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		final IWorkspaceRunnable op = new IWorkspaceRunnable() {
			@Override
			public void run(IProgressMonitor monitor) throws CoreException {
				MessageDialog.openInformation(
						window.getShell(),
						"Test Plug-in",
						"Hello\n" + res.toString());

			}
		};
		
		OperationsManager.executeOperation(new IWorkspaceRunnable() {
			@Override
			public void run(IProgressMonitor monitor) throws CoreException {
				DeeCore.run(op, null);
			}
		}, "blah");
		
		return null;
	}
	
}
