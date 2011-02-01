package mmrnmhrm.ui.actions;

import mmrnmhrm.core.DeeCore;
import mmrnmhrm.lang.ui.ExceptionHandler;

import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

public class OperationsManager {

	public static OperationsManager instance = new OperationsManager();
	
	public static OperationsManager get() {
		return instance;
	}	
	
	public boolean unitTestMode = false;
	
	public String opName;
	public IStatus opStatus;
	public int opResult;
	public String opMessage;
	
	
	public void aboutToDoOperation() {
		opMessage = null;
		opStatus = null;
		opResult = IStatus.OK;
	}
	
	
	public static boolean executeOperation(final IWorkspaceRunnable action, String opName) {
		ISimpleRunnable op = new ISimpleRunnable() {
			@Override
			public void run() throws CoreException {
				DeeCore.run(action, null);
			}
		};
		return get().doOperation(opName, op);
	}
	
	public static boolean executeSimple(ISimpleRunnable op, String opName) {
		return get().doOperation(opName, op);
	}


	public void instanceDoOperation(String opName, ISimpleRunnable op) {
		doOperation(opName, op);
	}
	
	public boolean doOperation(String opName, ISimpleRunnable op) {
		return doOperation(opName, op, true);
	}
	
	public boolean doOperation(String opName, ISimpleRunnable op, boolean handleRuntimeExceptions) {
		this.opName = opName;
		aboutToDoOperation();
		
		try {
			op.run();
		} catch (CoreException ce) {
			ExceptionHandler.handle(ce, opName, "Execution Error");
			opResult = IStatus.ERROR; 
			return false;
		} catch (RuntimeException re) {
			opResult = IStatus.ERROR;
			if(handleRuntimeExceptions) {
				ExceptionHandler.handle(re, opName, 
						"Program Error (see log for more details)");
				return false;
			}
			throw re;
		} 
		
		return true;
	}

	
	private void setError(String msg) {
		opResult = IStatus.ERROR;
		opMessage = msg;
	}

	public void setWarning(String msg) {
		opResult = IStatus.WARNING;
		opMessage = msg;
	}
	
	public void setInfo(String msg) {
		opResult = IStatus.INFO;
		opMessage = msg;
	}

	public static void openWarning(Shell shell, String title, String message) {
		get().setWarning(message);
		if(get().unitTestMode)
			return;
		
		MessageDialog.openWarning(shell, title, message);
	}
	
	public static void openInfo(Shell shell, String title, String message) {
		get().setInfo(message);
		if(get().unitTestMode)
			return;
		
		MessageDialog.openInformation(shell, title, message);
	}


	public static void openError(Shell shell, String title, String message) {
		get().setError(message);
		if(get().unitTestMode)
			return;
		
		MessageDialog.openError(shell, title, message);
	}



	
}
