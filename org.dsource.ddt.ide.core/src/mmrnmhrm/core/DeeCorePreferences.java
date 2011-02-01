package mmrnmhrm.core;


import org.eclipse.core.runtime.preferences.InstanceScope;

public class DeeCorePreferences {
	
	public static final String ADAPT_MALFORMED_DMD_AST = "adapt_malformed_ast";
	public static final String LANG_VERSION = "compiler_lang_version";
	
	
	public static boolean getBoolean(String key) {
		return DeeCore.getInstance().getPreferencesLookup().getBoolean(DeeCore.PLUGIN_ID, key);
	}
	
	public static int getInt(String key) {
		return DeeCore.getInstance().getPreferencesLookup().getInt(DeeCore.PLUGIN_ID, key);
	}
	
	public static String getString(String key) {
		return DeeCore.getInstance().getPreferencesLookup().getString(DeeCore.PLUGIN_ID, key);
	}
	
	public static void setBoolean(String key, boolean value) {
		(new InstanceScope()).getNode(DeeCore.PLUGIN_ID).putBoolean(key, value);
	}
	
	public static void setInt(String key, int value) {
		(new InstanceScope()).getNode(DeeCore.PLUGIN_ID).putInt(key, value);
	}
	
	public static void setString(String key, String value) {
		(new InstanceScope()).getNode(DeeCore.PLUGIN_ID).put(key, value);
	}
	
}
