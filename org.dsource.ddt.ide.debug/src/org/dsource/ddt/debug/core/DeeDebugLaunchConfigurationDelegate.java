/*******************************************************************************
 * Copyright (c) 2011, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package org.dsource.ddt.debug.core;


import melnorme.lang.ide.debug.core.GdbLaunchDelegateExtension;
import melnorme.lang.ide.launching.ProcessSpawnInfo;
import mmrnmhrm.core.launch.DeeLaunchConfigurationDelegate;

import org.eclipse.cdt.debug.core.ICDTLaunchConfigurationConstants;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;

public class DeeDebugLaunchConfigurationDelegate extends DeeLaunchConfigurationDelegate {
	
	protected final GdbLaunchDelegateExtension gdbLaunchDelegate = new GdbLaunchDelegateExtension();
	
	@Override
	protected ILaunch getLaunchForRunMode(ILaunchConfiguration configuration, String mode) throws CoreException {
		throw abort_UnsupportedMode(mode); // Run not supported
	}
	
	@Override
	public ILaunch getLaunchForDebugMode(ILaunchConfiguration configuration, String mode) throws CoreException {
		
		// Remove some DLTK attributes that affect how our launch runs
		ILaunchConfigurationWorkingCopy workingCopy = configuration.getWorkingCopy();
		cleanDLTKDebugConfig(workingCopy);
		
		// Setup CDT config parameters
		String fullProgramPath = getProgramFullPath(configuration).toString();
		String workingDirPath = getWorkingDirectoryOrDefault(configuration).toString();
		// Need to pass raw args, because CDT will reevaluate variables.
		String progArgs = getProgramArguments_Attribute(configuration);
		
		workingCopy.setAttribute(ICDTLaunchConfigurationConstants.ATTR_PROGRAM_NAME, fullProgramPath);
		workingCopy.setAttribute(ICDTLaunchConfigurationConstants.ATTR_PROGRAM_ARGUMENTS, progArgs);
		workingCopy.setAttribute(ICDTLaunchConfigurationConstants.ATTR_WORKING_DIRECTORY, workingDirPath);
		// Note, environment is already setup, because it uses standard attributes:
		// ILaunchManager.ATTR_ENVIRONMENT_VARIABLES and ILaunchManager.ATTR_APPEND_ENVIRONMENT_VARIABLES
		
		workingCopy.doSave();
		
		ILaunch launch = gdbLaunchDelegate.getLaunch(configuration, mode);
		return launch;
	}
	
	@Override
	protected void launchProcess(ProcessSpawnInfo processSpawnInfo, ILaunchConfiguration configuration, 
			ILaunch launch, IProgressMonitor monitor) throws CoreException {
		String mode = launch.getLaunchMode();
		gdbLaunchDelegate.launch(configuration, mode, launch, monitor);
	}
	
}