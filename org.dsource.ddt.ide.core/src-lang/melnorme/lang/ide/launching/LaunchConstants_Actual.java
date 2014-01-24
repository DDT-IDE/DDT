package melnorme.lang.ide.launching;

import mmrnmhrm.core.launch.DeeLaunchConstants;

import org.eclipse.dltk.launching.ScriptLaunchConfigurationConstants;

public interface LaunchConstants_Actual {
	
	public static final String LAUNCH_CONFIG_ID = DeeLaunchConstants.ID_DEE_LAUNCH_TYPE;
	
	public static final String ATTR_PROJECT_NAME = ScriptLaunchConfigurationConstants.ATTR_PROJECT_NAME;
	public static final String ATTR_PROG_FILE = ScriptLaunchConfigurationConstants.ATTR_MAIN_SCRIPT_NAME;
	public static final String ATTR_PROG_ARGUMENTS = ScriptLaunchConfigurationConstants.ATTR_SCRIPT_ARGUMENTS;
	public static final String ATTR_WORKING_DIRECTORY = ScriptLaunchConfigurationConstants.ATTR_WORKING_DIRECTORY;
	
	public static final String PROCESS_TYPE_ID = DeeLaunchConstants.ID_DEE_PROCESS_TYPE;

}