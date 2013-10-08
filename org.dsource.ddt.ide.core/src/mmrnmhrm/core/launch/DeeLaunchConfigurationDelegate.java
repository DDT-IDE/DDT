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


import mmrnmhrm.core.launch.debug.DeeDebuggingRunner;

import org.dsource.ddt.ide.core.DeeNature;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.dltk.launching.AbstractScriptLaunchConfigurationDelegate;
import org.eclipse.dltk.launching.IInterpreterRunner;
import org.eclipse.dltk.launching.InterpreterConfig;
import org.eclipse.dltk.launching.LaunchingMessages;
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
	protected void validateLaunchConfiguration(ILaunchConfiguration configuration, String mode, IProject project)
		throws CoreException {
		if(ILaunchManager.DEBUG_MODE.equals(mode)) {
			abort(LaunchingMessages.AbstractScriptLaunchConfigurationDelegate_debuggingEngineNotSelected,
					null,
					ScriptLaunchConfigurationConstants.ERR_NO_DEFAULT_DEBUGGING_ENGINE);
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
		super.runRunner(configuration, runner, config, launch, monitor);
	}
	
}