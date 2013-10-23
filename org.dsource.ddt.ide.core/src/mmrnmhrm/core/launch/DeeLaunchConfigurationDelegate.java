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
package mmrnmhrm.core.launch;


import static melnorme.utilbox.core.Assert.AssertNamespace.assertUnreachable;
import mmrnmhrm.core.launch.debug.DeeDebuggingRunner;

import org.dsource.ddt.ide.core.DeeNature;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.Launch;
import org.eclipse.dltk.launching.AbstractScriptLaunchConfigurationDelegate;
import org.eclipse.dltk.launching.IInterpreterRunner;
import org.eclipse.dltk.launching.InterpreterConfig;
import org.eclipse.dltk.launching.ScriptLaunchConfigurationConstants;

public class DeeLaunchConfigurationDelegate extends AbstractScriptLaunchConfigurationDelegate {
	
	@Override
	public String getLanguageId() {
		return DeeNature.NATURE_ID;
	}
	
	@Override
	public boolean buildForLaunch(ILaunchConfiguration configuration, String mode, IProgressMonitor monitor)
			throws CoreException {
		return super.buildForLaunch(configuration, mode, monitor);
	}
	
	@Override
	public ILaunch getLaunch(ILaunchConfiguration configuration, String mode) throws CoreException {
		
		// Remove some DLTK attributes that affect how our launch runs
		ILaunchConfigurationWorkingCopy workingCopy = configuration.getWorkingCopy();
		workingCopy.removeAttribute(DebugPlugin.ATTR_PROCESS_FACTORY_ID);
		workingCopy.setAttribute(ScriptLaunchConfigurationConstants.ATTR_DEBUG_CONSOLE, false);
		workingCopy.setAttribute(ScriptLaunchConfigurationConstants.ATTR_USE_INTERACTIVE_CONSOLE, false);
		workingCopy.doSave();
		
		final Launch launch = new Launch(configuration, mode, null);
		return launch;
	}
	
	@Override
	protected void setDebugConsoleAttributes(Launch launch, ILaunchConfiguration configuration) throws CoreException {
		throw assertUnreachable();
	}
	
	@Override
	protected void setDebugOptions(Launch launch, ILaunchConfiguration configuration) throws CoreException {
		throw assertUnreachable();
	}
	
	
	protected String savedAttrCaptureOutput;
	
	@Override
	public void launch(ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor)
			throws CoreException {
		
		// we need to save attribute DebugPlugin.ATTR_CAPTURE_OUTPUT because super.launch resets it
		savedAttrCaptureOutput = launch.getAttribute(DebugPlugin.ATTR_CAPTURE_OUTPUT);
		
		super.launch(configuration, mode, launch, monitor);
	}
	
	@Override
	protected void validateLaunchConfiguration(ILaunchConfiguration configuration, String mode, IProject project)
		throws CoreException {
		
		if(ILaunchManager.DEBUG_MODE.equals(mode)) {
			if(Platform.inDevelopmentMode()) {
				// Allow use of beta Debug engine
			} else 
			abort("Debugging not supported", null);
		}
	}
	
	@Override
	protected InterpreterConfig createInterpreterConfig(ILaunchConfiguration configuration, ILaunch launch)
			throws CoreException {
		return super.createInterpreterConfig(configuration, launch);
	}
	
	@Override
	public IInterpreterRunner getInterpreterRunner(ILaunchConfiguration configuration, String mode)
			throws CoreException {
		
		if(mode.equals(ILaunchManager.DEBUG_MODE)) {
			return new DeeDebuggingRunner();
		}
		
		return new DeeNativeRunner();
	}
	
	@Override
	protected void runRunner(ILaunchConfiguration configuration, IInterpreterRunner runner, InterpreterConfig config,
			ILaunch launch, IProgressMonitor monitor) throws CoreException {
		
		launch.setAttribute(DebugPlugin.ATTR_CAPTURE_OUTPUT, savedAttrCaptureOutput);
		
		super.runRunner(configuration, runner, config, launch, monitor);
	}
	
}