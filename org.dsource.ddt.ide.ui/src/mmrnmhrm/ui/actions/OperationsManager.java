package mmrnmhrm.ui.actions;

import mmrnmhrm.lang.ui.OperationExceptionHandler;

import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

public class OperationsManager {

	private static final String MSG_ERROR_EXECUTING_OPERATION 
		= "Error executing operation.";
	private static final String MSG_INTERNAL_ERROR_EXECUTING_OPERATION 
		= "Internal Error executing operation.";

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
				DLTKCore.run(action, null);
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
			OperationExceptionHandler.handle(ce, opName, MSG_ERROR_EXECUTING_OPERATION);
			opResult = IStatus.ERROR; 
			return false;
		} catch (RuntimeException re) {
			opResult = IStatus.ERROR;
			if(handleRuntimeExceptions) {
				OperationExceptionHandler.handle(re, opName, MSG_INTERNAL_ERROR_EXECUTING_OPERATION);
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
