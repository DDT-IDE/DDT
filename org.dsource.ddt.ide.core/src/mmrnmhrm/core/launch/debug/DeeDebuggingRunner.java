/*******************************************************************************
 * Copyright (c) 2013, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package mmrnmhrm.core.launch.debug;

import java.util.List;

import mmrnmhrm.core.launch.DeeNativeRunner;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IProcess;


public class DeeDebuggingRunner extends DeeNativeRunner {
	
	@Override
	public void prepareCommandLine(List<String> items) {
		items.add("gdb");
		items.add("--args");
		super.prepareCommandLine(items);
	}
	
	@Override
	protected IProcess launchProcess(ILaunch launch) throws CoreException {
		IProcess process = super.launchProcess(launch);
		
		GdbDebuggerHandler debuggerHandler = new GdbDebuggerHandler(process, sp);
		IDebugTarget target = new DeeDebugTarget(launch, process, debuggerHandler);
		launch.addDebugTarget(target);
		
		return process;
	}
	
}