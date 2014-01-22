/*******************************************************************************
 * Copyright (c) 2011, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package mmrnmhrm.core.launch;


import melnorme.ide.launching.AbstractLangLaunchConfigurationDelegate;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.Launch;
import org.eclipse.dltk.launching.ScriptLaunchConfigurationConstants;

/**
 * Abstract extension for running in non-debug mode, and cleanup some of DLTK behavior 
 */
public abstract class AbstractScriptLaunchConfigurationDelegateExtension 
	extends AbstractLangLaunchConfigurationDelegate {
	
	@Override
	protected String getProjectAttribute(ILaunchConfiguration config) throws CoreException {
		return config.getAttribute(ScriptLaunchConfigurationConstants.ATTR_PROJECT_NAME, (String) null);
	}
	
	@Override
	protected String getProcessRelativePath_Attribute(ILaunchConfiguration config) throws CoreException {
		return config.getAttribute(ScriptLaunchConfigurationConstants.ATTR_MAIN_SCRIPT_NAME, (String) null);
	}
	
	@Override
	protected String getProgramArguments_Attribute(ILaunchConfiguration config) throws CoreException {
		return config.getAttribute(ScriptLaunchConfigurationConstants.ATTR_SCRIPT_ARGUMENTS, "");
	}
	
	@Override
	protected String getWorkingDirectory_Attribute(ILaunchConfiguration config) throws CoreException {
		return config.getAttribute(ScriptLaunchConfigurationConstants.ATTR_WORKING_DIRECTORY, (String) null);
	}
	
	@Override
	protected ILaunch getLaunchForRunMode(ILaunchConfiguration configuration, String mode) throws CoreException {
		// Remove some DLTK attributes that affect how our launch runs
		ILaunchConfigurationWorkingCopy workingCopy = configuration.getWorkingCopy();
		cleanDLTKDebugConfig(workingCopy);
		workingCopy.doSave();
		// Note: DebugPlugin.ATTR_PROCESS_FACTORY_ID is also changed by CDT when launching in DEBUG mode
		
		return new Launch(configuration, mode, null);
	}
	
	public static void cleanDLTKDebugConfig(ILaunchConfigurationWorkingCopy workingCopy) {
		workingCopy.removeAttribute(DebugPlugin.ATTR_PROCESS_FACTORY_ID);
		workingCopy.setAttribute(ScriptLaunchConfigurationConstants.ATTR_DEBUG_CONSOLE, false);
		workingCopy.setAttribute(ScriptLaunchConfigurationConstants.ATTR_USE_INTERACTIVE_CONSOLE, false);
	}
	
	@Override
	protected ILaunch getLaunchForDebugMode(ILaunchConfiguration configuration, String mode) throws CoreException {
		throw abort_UnsupportedMode(mode);
	}
	
}