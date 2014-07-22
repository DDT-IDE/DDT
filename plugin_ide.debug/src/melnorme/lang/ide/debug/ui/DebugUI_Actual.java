package melnorme.lang.ide.debug.ui;

import melnorme.lang.ide.ui.LangUIPlugin;

import org.dsource.ddt.debug.ui.DeeToggleBreakpointAdapter;
import org.eclipse.cdt.debug.internal.ui.actions.breakpoints.ToggleDynamicPrintfAdapter;

public class DebugUI_Actual {
	
	public static final String LANG_BREAKPOINT_FACTORY_ID = LangUIPlugin.PLUGIN_ID + "BreakpointFactory";
	
	public static DeeToggleBreakpointAdapter createToggleBreakPointAdapter() {
		return new DeeToggleBreakpointAdapter();
	}
	
	public static ToggleDynamicPrintfAdapter createDynamicPrintfBreakpoint() {
		return new ToggleDynamicPrintfAdapter();
	}
	
}