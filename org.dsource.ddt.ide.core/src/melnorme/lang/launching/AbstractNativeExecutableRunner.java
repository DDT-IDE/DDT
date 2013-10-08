/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - modifications     
 *******************************************************************************/
package melnorme.lang.launching;

import java.util.ArrayList;
import java.util.List;

import melnorme.utilbox.misc.ArrayUtil;
import mmrnmhrm.core.launch.LaunchMessages;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.dltk.core.environment.IEnvironment;
import org.eclipse.dltk.core.environment.IFileHandle;
import org.eclipse.dltk.internal.launching.InterpreterMessages;
import org.eclipse.dltk.launching.InterpreterConfig;
import org.eclipse.osgi.util.NLS;


/**
 * Abstract implementation of a native executable runner.
 * 
 */
public abstract class AbstractNativeExecutableRunner extends AbstractInterpreterRunner_Mod {
	
	protected AbstractNativeExecutableRunner() {
	}
	
	@Override
	protected void checkConfig(InterpreterConfig config) throws CoreException {
		IEnvironment environment = config.getEnvironment();
		IPath workingDirectoryPath = config.getWorkingDirectoryPath();
		IFileHandle dir = environment.getFile(workingDirectoryPath);
		if (!dir.exists()) {
			abort(NLS.bind(InterpreterMessages.errDebuggingEngineWorkingDirectoryDoesntExist, dir.toString()), null);
		}
		if (config.getScriptFilePath() == null || config.isNoFile()) {
			abort(LaunchMessages.errDebuggingEngineExecutableFileNull, null);
		}
		
		final IFileHandle exeFile = environment.getFile(config.getScriptFilePath());
		if(!exeFile.exists()) {
			abort(NLS.bind(LaunchMessages.errDebuggingEngineExecutableFileDoesntExist, exeFile.toString()), null);
		}
	}
	
	@Override
	protected final String[] renderCommandLine(InterpreterConfig config) {
		List<String> items = new ArrayList<String>();
		renderCommandLineForCompiledExecutable(config, items);
		return ArrayUtil.createFrom(items, String.class);
	}
	
	public void renderCommandLineForCompiledExecutable(InterpreterConfig config, List<String> items) {
		items.add(config.getScriptFilePath().toString());
		
		// application arguments arguments
		List<String> scriptArgs = config.getScriptArgs();
		items.addAll(scriptArgs);
	}
	
}