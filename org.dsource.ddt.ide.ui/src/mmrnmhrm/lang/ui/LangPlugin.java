package mmrnmhrm.lang.ui;

import mmrnmhrm.ui.ActualPlugin;
import mmrnmhrm.ui.DeePlugin;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.plugin.AbstractUIPlugin;


public abstract class LangPlugin extends AbstractUIPlugin {

	public static String getPluginId() {
		return ActualPlugin.PLUGIN_ID;
	}
	
	/** Logs the given status. */
	public static void log(IStatus status) {
		ActualPlugin.getInstance().getLog().log(status);
	}
	
	/** Logs the given Throwable, wrapping it in a Status. */
	public static void log(Throwable e) {
		e.printStackTrace();
		log(new Status(IStatus.ERROR, getPluginId(),
				ILangStatusConstants.INTERNAL_ERROR,
				LangUIMessages.LangPlugin_internal_error, e)); 
	}
	
	/** Gets the active workbench window. */
	public static IWorkbenchWindow getActiveWorkbenchWindow() {
		return DeePlugin.getInstance().getWorkbench().getActiveWorkbenchWindow();
	}

	/** Gets the active workbench shell. */
	public static Shell getActiveWorkbenchShell() {
		 IWorkbenchWindow window= getActiveWorkbenchWindow();
		 if (window != null) {
		 	return window.getShell();
		 }
		 return null;
	}
	
	/** Gets the active workbench page. */
	public static IWorkbenchPage getActivePage() {
		IWorkbenchWindow window = getActiveWorkbenchWindow();
		if (window == null)
			return null;
		return window.getActivePage();
	}

	/** Gets the plugins preference store. */
	public static IPreferenceStore getPrefStore() {
		return ActualPlugin.getInstance().getPreferenceStore();
	}
}