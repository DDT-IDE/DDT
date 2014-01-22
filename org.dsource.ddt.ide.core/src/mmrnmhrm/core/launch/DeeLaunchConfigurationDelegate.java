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


import melnorme.ide.launching.ProcessSpawnInfo;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;

public class DeeLaunchConfigurationDelegate extends AbstractScriptLaunchConfigurationDelegateExtension {
	
	@Override
	protected void launchProcess(ProcessSpawnInfo config, ILaunchConfiguration configuration, ILaunch launch,
			IProgressMonitor monitor) throws CoreException {
		
		DeeNativeRunner deeNativeRunner = new DeeNativeRunner();
		deeNativeRunner.initConfiguration(
				config.workingDir,
				config.processFile,
				config.processArguments,
				null); // TODO: environment
		deeNativeRunner.run(launch, monitor);
	}
	
}