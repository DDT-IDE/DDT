package mmrnmhrm.ui;

import org.eclipse.core.resources.IProject;

public class DeePluginPreferences {

	// XXX: TODO add project scope
	public static String getPreference(String key, @SuppressWarnings("unused") IProject project) {
		return DeePlugin.getPrefStore().getString(key);
	}

	/*
	 public static String getPreference(String key, IJavaProject project) {
		String val;
		if (project != null) {
			val= new ProjectScope(project.getProject()).getNode(JavaUI.ID_PLUGIN).get(key, null);
			if (val != null) {
				return val;
			}
		}
		val= new InstanceScope().getNode(JavaUI.ID_PLUGIN).get(key, null);
		if (val != null) {
			return val;
		}
		return new DefaultScope().getNode(JavaUI.ID_PLUGIN).get(key, null);
	}
	 */
}
