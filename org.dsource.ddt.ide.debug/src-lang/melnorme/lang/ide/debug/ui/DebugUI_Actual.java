package melnorme.lang.ide.debug.ui;

import org.dsource.ddt.debug.ui.DeeToggleBreakpointAdapter;
import org.dsource.ddt.ui.DeeUIPlugin;
import org.eclipse.core.runtime.Plugin;

public class DebugUI_Actual extends Plugin {
	
	public static String LANG_BREAKPOINT_FACTORY_ID = DeeUIPlugin.PLUGIN_ID + "BreakpointFactory";
	
	public static DeeToggleBreakpointAdapter createToggleBreakPointAdapter() {
		return new DeeToggleBreakpointAdapter();
	}
	
}