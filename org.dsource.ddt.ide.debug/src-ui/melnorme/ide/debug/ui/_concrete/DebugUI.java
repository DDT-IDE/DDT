package melnorme.ide.debug.ui._concrete;

import org.dsource.ddt.debug.ui.DeeToggleBreakpointAdapter;
import org.dsource.ddt.ui.DeeUIPlugin;

public class DebugUI {
	
	public static String LANG_BREAKPOINT_FACTORY_ID = DeeUIPlugin.PLUGIN_ID + "DeeBreakpointFactory";
	
	public static DeeToggleBreakpointAdapter createToggleBreakPointAdapter() {
		return new DeeToggleBreakpointAdapter();
	}
	
}