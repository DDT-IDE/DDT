package melnorme.lang.ide.ui;

import org.dsource.ddt.ui.DeeUIPlugin;

/**
 * Alias for the actual running plugin, used by Lang code. 
 */
public final class LangUIPlugin_Actual extends DeeUIPlugin {
	
	protected static DeeUIPlugin __getInstance() {
		return DeeUIPlugin.getInstance();
	}
	
	// ID to start the debug plugin automatically, if present
	protected static final String DEBUG_PLUGIN_ID = "org.dsource.ddt.ide.debug";
	
}
