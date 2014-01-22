package melnorme.ide.launching;

import mmrnmhrm.core.DeeCore;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

public class LaunchingCore {
	
	// TODO: truly make this melnorme.lang independent.
	public static final String PLUGIN_ID = DeeCore.PLUGIN_ID;
	
	public static final int LAUNCHING_CONFIG_ERROR = 101;
	
	public static CoreException createCoreException(Throwable exception, int code) {
		String message = exception.getMessage();
		return createCoreException(exception, code, message);
	}
	
	public static CoreException createCoreException(Throwable exception, int code, String message) {
		return new CoreException(new Status(IStatus.ERROR, PLUGIN_ID, code, message, exception));
	}
	
}