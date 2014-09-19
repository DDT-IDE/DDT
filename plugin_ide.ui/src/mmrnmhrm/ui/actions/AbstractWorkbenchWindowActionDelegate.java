package mmrnmhrm.ui.actions;


import org.eclipse.dltk.ui.DLTKUIPlugin;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

public abstract class AbstractWorkbenchWindowActionDelegate implements IWorkbenchWindowActionDelegate {
	
	protected IWorkbenchWindow window;
	
	public AbstractWorkbenchWindowActionDelegate() {
		super();
	}
	
	@Override
	public void init(IWorkbenchWindow window) {
		this.window = window;
	}
	
	@Override
	public abstract void run(IAction action);
	
	@Override
	public void selectionChanged(IAction action, ISelection selection) {
	}
	
	@Override
	public void dispose() {
		window = null;
	}

	public static void beep() {
		Shell shell = DLTKUIPlugin.getActiveWorkbenchShell();
		if (shell != null && shell.getDisplay() != null) {
			shell.getDisplay().beep();
		}
	}
	
}