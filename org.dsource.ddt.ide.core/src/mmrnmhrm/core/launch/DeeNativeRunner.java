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

import melnorme.lang.launching.AbstractBinaryRunner;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.dltk.launching.LaunchingMessages;

public class DeeNativeRunner extends AbstractBinaryRunner {
	
	@Override
	protected String getProcessType() {
		return DeeLaunchConfigurationConstants.ID_DEE_PROCESS_TYPE;
	}
	
	public void run(ILaunch launch, IProgressMonitor monitor) throws CoreException {
		if (monitor == null) {
			monitor = new NullProgressMonitor();
		}
		
		try {
			monitor.beginTask(LaunchingMessages.AbstractInterpreterRunner_launching, 1);
			launchProcess(launch);
			monitor.worked(1);
		} finally {
			monitor.done();
		}
	}
	
}