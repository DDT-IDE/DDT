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

import java.util.List;

import melnorme.lang.launching.AbstractBinaryRunner;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.dltk.launching.IInterpreterRunner;
import org.eclipse.dltk.launching.InterpreterConfig;
import org.eclipse.dltk.launching.LaunchingMessages;

public class DeeNativeRunner extends AbstractBinaryRunner 
	implements IInterpreterRunner 
{
	
	@Override
	protected String getProcessType() {
		return DeeLaunchConfigurationConstants.ID_DEE_PROCESS_TYPE;
	}
	
	@Override
	public void run(InterpreterConfig config, ILaunch launch, IProgressMonitor monitor) throws CoreException {
		if (monitor == null) {
			monitor = new NullProgressMonitor();
		}
		
		try {
			monitor.beginTask(LaunchingMessages.AbstractInterpreterRunner_launching, 5);
			if (monitor.isCanceled()) {
				return;
			}
			
			monitor.worked(1);
			monitor.subTask(LaunchingMessages.AbstractInterpreterRunner_running);
			initConfig(config);
			launchProcess(launch);
			monitor.worked(4);
			
		} finally {
			monitor.done();
		}
	}
	
	protected void initConfig(InterpreterConfig config) throws CoreException {
		IPath workingDirectoryPath = config.getWorkingDirectoryPath();
		IPath scriptFilePath = config.getScriptFilePath();
		List<String> scriptArgs = config.getScriptArgs();
		String[] environment = config.getEnvironmentAsStringsIncluding(null); // Default: no additional vars are added
		
		initConfiguration(workingDirectoryPath, scriptFilePath, scriptArgs, environment);
	}
	
}