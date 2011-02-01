package mmrnmhrm.ui.actions;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;


public class SampleTest2Handler extends AbstractHandler {

	/*public static class TestDialog extends TrayDialog {
		private ProjectConfigBlock fProjCfg;
		
		protected TestDialog(Shell shell) {
			super(shell);
			setShellStyle(getShellStyle() | SWT.RESIZE | SWT.MAX);
			fProjCfg = new ProjectConfigBlock();
		}
		
		@Override
		protected Control createDialogArea(Composite parent) {
			fProjCfg.init(DeeModel.getRoot().getLangProjects()[0]);
			Control control = fProjCfg.createControl(parent); 
			control.setLayoutData(new GridData(GridData.FILL_BOTH));
			return control;
		}
		
		@Override
		public int open() {
			int ret = super.open();
			try {
				IWorkspaceRunnable op = new IWorkspaceRunnable() {
					public void run(IProgressMonitor monitor) throws CoreException {
						fProjCfg.applyConfig();
					}
				};
				DeeCore.run(op, null);
			} catch (CoreException e) {
				ExceptionHandler.handle(e, "D Project Config Error", "Error saving project settings.");
				return ret;
			}
			return ret;
		}
		
	}*/


	/*public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);

		TestDialog foo = new TestDialog(window.getShell());
		foo.open();
		return null;
	}*/
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		/*try {
			IWorkspaceRunnable op = new IWorkspaceRunnable() {
				public void run(IProgressMonitor monitor) throws CoreException {
					//DeeModel.getRoot().updateElementRecursive();
					//DeeModel.getRoot().updateElementLazily();
				}
			};
			DeeCore.runSimpleOp(op);
		} catch (CoreException ce) {
			throw new ExecutionException("RefreshModelHandler error", ce);
		}*/
		return null;
	}

}
