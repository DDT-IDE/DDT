package mmrnmhrm.lang.ui;

import mmrnmhrm.ui.ActualPlugin;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.preference.IPreferenceStore;
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
	
	/** Gets the plugins preference store. */
	public static IPreferenceStore getPrefStore() {
		return ActualPlugin.getInstance().getPreferenceStore();
	}
}