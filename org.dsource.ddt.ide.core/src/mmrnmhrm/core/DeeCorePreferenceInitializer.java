package mmrnmhrm.core;


import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;

import descent.internal.compiler.parser.Parser;


public class DeeCorePreferenceInitializer extends AbstractPreferenceInitializer {
	
	public DeeCorePreferenceInitializer() {
	}
	
	@Override
	public void initializeDefaultPreferences() {
		IEclipsePreferences defaults = (new DefaultScope()).getNode(DeeCore.PLUGIN_ID);
		
		defaults.putBoolean(DeeCorePreferences.ADAPT_MALFORMED_DMD_AST, true);
		defaults.putInt(DeeCorePreferences.LANG_VERSION, Parser.D2);
	}
	
//	private static IPreferenceStore preferenceStore = null;
//	
//	public static IPreferenceStore getPreferenceStore() {
//        // Create the preference store lazily.
//        if (preferenceStore == null) {
//            preferenceStore = new ScopedPreferenceStore(new InstanceScope(), DeeCore.getInstance().getBundle().getSymbolicName());
//
//        }
//        return preferenceStore;
//    }
	
}
