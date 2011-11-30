package mmrnmhrm.core.launch;


import mmrnmhrm.core.DeeCore;

import org.eclipse.dltk.launching.ScriptLaunchConfigurationConstants;

public class DeeLaunchConfigurationConstants extends ScriptLaunchConfigurationConstants {
	
	protected DeeLaunchConfigurationConstants() {
	}
	
	public static final String ID_DEE_EXECUTABLE = DeeCore.EXTENSIONS_IDPREFIX+"deeLaunchConfigurationType";
	
	public static final String ID_DEE_PROCESS_TYPE = "deeNative";
}
