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


import melnorme.ide.debug.core.GdbLaunchDelegateExtension;
import mmrnmhrm.core.launch.AbstractScriptLaunchConfigurationDelegateExtension;

import org.dsource.ddt.ide.core.DeeNature;
import org.eclipse.cdt.debug.core.ICDTLaunchConfigurationConstants;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.dltk.launching.InterpreterConfig;
import org.eclipse.dltk.launching.ScriptLaunchConfigurationConstants;

public class DeeDebugLaunchConfigurationDelegate extends AbstractScriptLaunchConfigurationDelegateExtension {
	
	protected final GdbLaunchDelegateExtension gdbLaunchDelegate = new GdbLaunchDelegateExtension();
	
	@Override
	public String getLanguageId() {
		return DeeNature.NATURE_ID;
	}
	
	@Override
	public ILaunch getLaunch(ILaunchConfiguration configuration, String mode) throws CoreException {
		
		// Remove some DLTK attributes that affect how our launch runs
		ILaunchConfigurationWorkingCopy workingCopy = configuration.getWorkingCopy();
		AbstractScriptLaunchConfigurationDelegateExtension.cleanDLTKDebugConfig(workingCopy);
		
		String progName = workingCopy.getAttribute(ScriptLaunchConfigurationConstants.ATTR_MAIN_SCRIPT_NAME, 
				(String) null);
		String projectName = workingCopy.getAttribute(ScriptLaunchConfigurationConstants.ATTR_PROJECT_NAME, 
				(String) null);
		if(projectName == null) {
			abort("Project not specified", null);
		}
		IPath projectPath = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName).getLocation();
		IPath fullBinaryPath = projectPath.append(progName); 
		
		workingCopy.setAttribute(ICDTLaunchConfigurationConstants.ATTR_PROGRAM_NAME, fullBinaryPath.toString());
		
		String progArgs = workingCopy.getAttribute(ScriptLaunchConfigurationConstants.ATTR_SCRIPT_ARGUMENTS, 
				(String) null);
		workingCopy.setAttribute(ICDTLaunchConfigurationConstants.ATTR_PROGRAM_ARGUMENTS, progArgs);
		
		workingCopy.doSave();
		
		ILaunch launch = gdbLaunchDelegate.getLaunch(configuration, mode);
		return launch;
	}
	
	protected void setDefaultAttributeValue(ILaunchConfigurationWorkingCopy workingCopy, String attribute, 
			String value) throws CoreException {
		if(!workingCopy.hasAttribute(attribute)) {
			workingCopy.setAttribute(attribute, value);
		}
	}
	
	@Override
	protected void validateLaunchConfiguration(ILaunchConfiguration configuration, String mode, IProject project)
			throws CoreException {
		if(!mode.equals(ILaunchManager.DEBUG_MODE)) {
			abort("Internal error: can only use this delegate for debug launches", null);
		}
	}
	
	@Override
	protected void launch0(InterpreterConfig config, ILaunchConfiguration configuration, ILaunch launch,
			IProgressMonitor monitor) throws CoreException {
		String mode = launch.getLaunchMode();
		gdbLaunchDelegate.launch(configuration, mode, launch, monitor);
	}
	
}