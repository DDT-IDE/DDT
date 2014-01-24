/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 * Contributors:
 *     ??? (DLTK) - initial API and implementation
 *     Bruno Medeiros - modifications, removed DLTK dependencies
 *******************************************************************************/
package melnorme.ide.launching;


import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import melnorme.utilbox.misc.ArrayUtil;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IDebugEventSetListener;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.dltk.compiler.util.Util;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.launching.LaunchingMessages;
import org.eclipse.osgi.util.NLS;

//BM: Partially based on org.eclipse.dltk.launching.AbstractInterpreterRunner @ DLTK 5.0
/**
 * Helper class to launch an Eclipse IProcess.
 */
public class EclipseProcessLauncher {
	
	protected final IPath workingDir;
	protected final IPath processFile;
	protected final String[] processArguments;
	protected final Map<String, String> environment;
	protected final boolean appendEnvironment;
	
	protected String processType;
	
	public EclipseProcessLauncher(IPath workingDir, IPath processFile, String[] processArgs, 
			Map<String, String> environment, boolean appendEnvironment, String processType) {
		this.workingDir = workingDir;
		this.processFile = processFile;
		assertNotNull(processFile);
		this.processArguments = processArgs;
		this.environment = environment;
		this.appendEnvironment = appendEnvironment;
		
		this.processType = processType;
	}
	
	protected CoreException abort(String message, Throwable exception) throws CoreException {
		throw LaunchingCore.createCoreException(exception, LaunchingCore.LAUNCHING_CONFIG_ERROR, message);
	}
	
	protected CoreException fail(String messagePattern, Object... arguments) throws CoreException {
		throw abort(MessageFormat.format(messagePattern, arguments), null);
	}
	
	protected IProcess launchProcess(final ILaunch launch) throws CoreException {
		if(workingDir != null && !workingDir.toFile().exists()) {
			fail(LaunchMessages.errWorkingDirectoryDoesntExist, workingDir);
		}
		if(!processFile.toFile().exists()) {
			fail(LaunchMessages.errExecutableFileDoesntExist, processFile);
		}
		
		String[] cmdLine = getCommandLine();
		Process sp = newSystemProcess(cmdLine);
		
		final String cmdLineLabel = renderCommandLineLabel(cmdLine);
		final String baseProcessLabel = renderBaseProcessLabel(cmdLine);
		return newEclipseProcessWithLabelUpdater(launch, cmdLineLabel, baseProcessLabel, sp);
	}
	
	/** Create the {@link java.lang.Process}. */
	protected Process newSystemProcess(String[] cmdLine) throws CoreException {
		
		File workingDirectory = workingDir.toFile();
		Process sp= null;
		try {
			
			ProcessBuilder processBuilder = new ProcessBuilder(cmdLine).directory(workingDirectory);
			setupEnvironment(processBuilder);
			
			sp = processBuilder.start();
		} catch (IOException e) {
			abort(LaunchMessages.errNewJavaProcessFailed, e);
		}
		return sp;
	}
	
	protected void setupEnvironment(ProcessBuilder processBuilder) throws CoreException {
		try {
			// This is a non-standard map that can throw some exceptions, see doc
			Map<String, String> env = processBuilder.environment();
			if(!appendEnvironment) {
				env.clear();
			}
			
			if(environment != null) {
				for (String key : environment.keySet()) {
					String value = environment.get(key);
					env.put(key, value);
				}
			}
		} catch (UnsupportedOperationException | IllegalArgumentException e) {
			abort(LaunchMessages.errFailedToSetupProcessEnvironment, e);
		}
	}
	
	protected final String[] getCommandLine() {
		List<String> items = new ArrayList<String>();
		prepareCommandLine(items);
		return ArrayUtil.createFrom(items, String.class);
	}
	
	protected void prepareCommandLine(List<String> commandLine) {
		commandLine.add(processFile.toOSString());
		commandLine.addAll(Arrays.asList(processArguments));
	}
	
	public IProcess newEclipseProcessWithLabelUpdater(ILaunch launch, String cmdLineLabel, String baseProcessLabel, 
		Process sp) throws CoreException {
		final AtomicReference<IProcess> processRef = new AtomicReference<>(null);
		
		DebugPlugin.getDefault().addDebugEventListener(new ProcessLabelListener(launch, processRef));
		IProcess process = newEclipseProcess(launch, sp, baseProcessLabel);
		processRef.set(process);
		process.setAttribute(IProcess.ATTR_CMDLINE, cmdLineLabel);
		updateProcessLabel(launch, process);
		return process;
	}
	
	protected IProcess newEclipseProcess(ILaunch launch, Process sp, String label) throws CoreException {
		
		IProcess process = DebugPlugin.newProcess(launch, sp, label, getProcessAttributes());
		
		if (process == null) {
			sp.destroy();
			fail(LaunchMessages.errINTERNAL_newIProcessFailed);
		}
		return process;
	}
	
	protected Map<String, String> getProcessAttributes() {
		Map<String, String> map = new HashMap<String, String>();
		if(processType != null) {
			map.put(IProcess.ATTR_PROCESS_TYPE, processType);
		}
		return map;
	}
	
	protected static String renderBaseProcessLabel(String[] commandLine) {
		String format = LaunchingMessages.StandardInterpreterRunner;
		String timestamp = DateFormat.getDateTimeInstance(DateFormat.MEDIUM,
				DateFormat.MEDIUM).format(new Date(System.currentTimeMillis()));
		return NLS.bind(format, commandLine[0], timestamp);
	}

	/**
	 * String representation of the command line
	 * 
	 * @param commandLine
	 * @return
	 */
	protected static String renderCommandLineLabel(String[] commandLine) {
		if (commandLine.length == 0)
			return Util.EMPTY_STRING;
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < commandLine.length; i++) {
			if (i != 0) {
				buf.append(' ');
			}
			char[] characters = commandLine[i].toCharArray();
			StringBuffer command = new StringBuffer();
			boolean containsSpace = false;
			for (int j = 0; j < characters.length; j++) {
				char character = characters[j];
				if (character == '\"') {
					command.append('\\');
				} else if (character == ' ') {
					containsSpace = true;
				}
				command.append(character);
			}
			if (containsSpace) {
				buf.append('\"');
				buf.append(command.toString());
				buf.append('\"');
			} else {
				buf.append(command.toString());
			}
		}
		return buf.toString();
	}
	
	protected final class ProcessLabelListener implements IDebugEventSetListener {
		protected final ILaunch launch;
		protected final AtomicReference<IProcess> process;
		
		public ProcessLabelListener(ILaunch launch, AtomicReference<IProcess> process) {
			this.launch = launch;
			this.process = process;
		}
		
		@Override
		public void handleDebugEvents(DebugEvent[] events) {
			for (int i = 0; i < events.length; i++) {
				DebugEvent event = events[i];
				if (event.getSource().equals(process.get())) {
					if (event.getKind() == DebugEvent.CHANGE || event.getKind() == DebugEvent.TERMINATE) {
						updateProcessLabel(launch, process.get());
						if (event.getKind() == DebugEvent.TERMINATE) {
							DebugPlugin.getDefault().removeDebugEventListener(this);
						}
					}
				}
			}
		}
	}
	
	protected static String computeName(final ILaunch launch, final IProcess process) {
		StringBuffer buffer = new StringBuffer();
		int exitValue = 0;
		try {
			exitValue = process.getExitValue();
		} catch (DebugException e1) {
			// DLTKCore.error(e1);
			exitValue = 0;// Seems not available yet
		}
		if (exitValue != 0) {
			buffer.append("<abnormal exit code:" + exitValue + "> ");
		}
		String type = null;
		ILaunchConfiguration launchConfiguration = launch.getLaunchConfiguration();
		if (launchConfiguration != null) {
			try {
				ILaunchConfigurationType launchConfigType = launchConfiguration.getType();
				if (launchConfigType != null) {
					type = launchConfigType.getName();
				}
			} catch (CoreException e) {
				DLTKCore.error(e);
			}
			buffer.append(launchConfiguration.getName());
		}
		if (type != null) {
			buffer.append(" ["); //$NON-NLS-1$
			buffer.append(type);
			buffer.append("] "); //$NON-NLS-1$
		}
		buffer.append(process.getLabel());
		return buffer.toString();
	}
	
	protected static void updateProcessLabel(final ILaunch launch, final IProcess process) {
		String computedName = computeName(launch, process);
		process.setAttribute(IProcess.ATTR_PROCESS_LABEL, computedName);
	}
	
}