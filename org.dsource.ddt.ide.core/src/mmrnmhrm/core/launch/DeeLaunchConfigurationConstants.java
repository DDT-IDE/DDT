package mmrnmhrm.core.launch;

import mmrnmhrm.core.DeeCore;

import org.eclipse.dltk.launching.ScriptLaunchConfigurationConstants;

public class DeeLaunchConfigurationConstants extends ScriptLaunchConfigurationConstants {
	
	protected DeeLaunchConfigurationConstants() {
	}
	
	public static final String ID_DEE_SCRIPT = DeeCore.EXTENSIONS_IDPREFIX+"deeLaunchConfigurationType"; //$NON-NLS-1$
	
	public static final String ID_DEE_PROCESS_TYPE = "deeprocess"; //$NON-NLS-1$
}
