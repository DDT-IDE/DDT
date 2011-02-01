package mmrnmhrm.tests;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import org.eclipse.core.runtime.ILogListener;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;

public final class ErrorLogListener implements ILogListener {
	
	protected boolean errorOccurred = false;
	protected Throwable exception = null;
	
	public static ErrorLogListener createAndInstall() {
		ErrorLogListener loglistener = new ErrorLogListener() ;
		Platform.addLogListener(loglistener);
		return loglistener;
	}
	
	@Override
	public void logging(IStatus status, String plugin) {
		System.err.println(status);
		if(status.getSeverity() == IStatus.ERROR && errorOccurred == false) {
			errorOccurred = true;
			exception = status.getException();
		}
	}
	
	public void checkErrors() throws Throwable {
		if(errorOccurred == true) {
			throw exception;
		}
		assertTrue(errorOccurred == false, "Assertion failed.");
	}
	
	public void uninstall() {
		Platform.removeLogListener(this);
	}
	
	public void checkErrorsAndUninstall() throws Throwable {
		uninstall();
		checkErrors();
	}

	public void reset() {
		errorOccurred = false;
	}
	
}