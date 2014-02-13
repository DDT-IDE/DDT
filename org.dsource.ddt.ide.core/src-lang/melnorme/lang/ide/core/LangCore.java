package melnorme.lang.ide.core;

import melnorme.lang.ide.core.utils.EclipseUtils;
import mmrnmhrm.core.DeeCore;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;

public abstract class LangCore extends Plugin {
	
	public static class ILangConstants {
		
		public static int INTERNAL_ERROR = 1;
		
	}
	
	public static final String PLUGIN_ID = LangCore_Actual.PLUGIN_ID;
	public static final String NATURE_ID = LangCore_Actual.NATURE_ID;
	
	public static Plugin getInstance() {
		return LangCore_Actual.getInstance();
	}
	
	/** Convenience method to get the WorkspaceRoot. */
	public static IWorkspaceRoot getWorkspaceRoot() {
		return EclipseUtils.getWorkspaceRoot();
	}
	
	/** Convenience method to get the Workspace. */
	public static IWorkspace getWorkspace() {
		return ResourcesPlugin.getWorkspace();
	}
	
	/** Creates an OK status with given message. */
	public static Status createStatus(String message) {
		return new Status(IStatus.OK, DeeCore.PLUGIN_ID, message); 
	}
	
	/** Creates a status describing an error in this plugin, with given message. */
	public static IStatus createErrorStatus(String message) {
		return createErrorStatus(message, null);
	}
	
	/** Creates a status describing an error in this plugin, with give message and exception. */
	public static Status createErrorStatus(String message, Throwable throwable) {
		return new Status(IStatus.ERROR, DeeCore.PLUGIN_ID, ILangConstants.INTERNAL_ERROR, message, throwable); 
	}
	
	/** Creates a CoreException describing an error in this plugin. */
	public static CoreException createCoreException(String msg, Exception e) {
		return new CoreException(createErrorStatus(msg, e));
	}
	
	public static void log(Exception e) {
		logError(e);
	}
	
	/** Logs an error status with given exception and given message. */
	public static void logError(Throwable throwable, String message) {
		getInstance().getLog().log(createErrorStatus(message, throwable));
	}
	
	/** Logs given error status. */
	public static void logError(IStatus status) {
		getInstance().getLog().log(status);
	}
	
	/** Logs an error status with given message. */
	public static void logError(String message) {
		getInstance().getLog().log(createErrorStatus(message, null));
	}
	
	/** Logs an error status with given exception. */
	public static void logError(Throwable throwable) {
		getInstance().getLog().log(createErrorStatus(LangCoreMessages.LangCore_internal_error, throwable));
	}
	
	/** Logs the given message, creating a new warning status for this plugin. */
	public static void logWarning(String message) {
		getInstance().getLog().log(
				new Status(IStatus.WARNING, PLUGIN_ID, ILangConstants.INTERNAL_ERROR, message, null));
	}
	
}