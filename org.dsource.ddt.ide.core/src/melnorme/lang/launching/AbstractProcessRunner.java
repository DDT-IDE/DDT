/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 * Contributors:
 *     ??? (DLTK) - initial API and implementation
 *     Bruno Medeiros - modifications, removed most DLTK dependencies
 *******************************************************************************/
package melnorme.lang.launching;


import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import melnorme.utilbox.misc.ArrayUtil;
import melnorme.utilbox.misc.StringUtil;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
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
import org.eclipse.dltk.internal.launching.DLTKLaunchingPlugin;
import org.eclipse.dltk.launching.IInterpreterRunner;
import org.eclipse.dltk.launching.LaunchingMessages;
import org.eclipse.dltk.launching.ScriptLaunchConfigurationConstants;
import org.eclipse.osgi.util.NLS;

//BM: Copied from org.eclipse.dltk.launching.AbstractInterpreterRunner @ DLTK 5.0
/**
 * Abstract implementation of a interpreter runner.
 * <p>
 * Clients implementing interpreter runners should subclass this class.
 * </p>
 * 
 * @see IInterpreterRunner
 * 
 */
public abstract class AbstractProcessRunner {
	
	protected void abort(String message, Throwable exception) throws CoreException {
		throw new CoreException(new Status(IStatus.ERROR,
			DLTKLaunchingPlugin.PLUGIN_ID, ScriptLaunchConfigurationConstants.ERR_INTERNAL_ERROR, message, exception));
	}
	
	protected void abort(String message, Throwable exception, int code) throws CoreException {
		throw new CoreException(new Status(IStatus.ERROR, 
			DLTKLaunchingPlugin.PLUGIN_ID, code, message, exception));
	}
	
	protected IPath workingDir;
	protected IPath processFile;
	protected List<String> processArguments;
	protected String[] environment;
	protected Process sp;
	
	protected void initConfiguration(IPath workingDir, IPath processFile, List<String> processArgs, String[] environment)
			throws CoreException {
		this.workingDir = workingDir;
		this.processFile = processFile;
		this.processArguments = processArgs;
		this.environment = environment;
		
		
		if (!workingDir.toFile().exists()) {
			abort(NLS.bind(LaunchMessages.errWorkingDirectoryDoesntExist, workingDir.toString()), null);
		}
		if (processFile == null) {
			abort(LaunchMessages.errExecutableFileNull, null);
		}
		
		if(!processFile.toFile().exists()) {
			abort(NLS.bind(LaunchMessages.errExecutableFileDoesntExist, processFile.toString()), null);
		}
	}
	
	protected IProcess launchProcess(final ILaunch launch) throws CoreException {
		String[] cmdLine = getCommandLine();
		Process sp = newSystemProcess(cmdLine);
		
		final String cmdLineLabel = renderCommandLineLabel(cmdLine);
		final String baseProcessLabel = renderBaseProcessLabel(cmdLine);
		return newProcessWithLabelUpdater(launch, cmdLineLabel, baseProcessLabel, sp);
	}
	
	protected Process newSystemProcess(String[] cmdLine) throws CoreException {
		
		if(DLTKLaunchingPlugin.TRACE_EXECUTION) {
			traceExecution(cmdLine, workingDir, environment);
		}
		
		Process sp = DebugPlugin.exec(cmdLine, workingDir.toFile(), environment);
		if (sp == null) {
			abort(LaunchingMessages.AbstractInterpreterRunner_executionWasCancelled, null);
		}
		return sp;
	}
	
	protected static void traceExecution(String[] cmdLine, IPath workingDirectory, String[] environment) {
		StringBuffer sb = new StringBuffer();
		sb.append("-----------------------------------------------\n");
		sb.append("Command line: ").append(StringUtil.collToString(cmdLine, "●")).append('\n');
		sb.append("Working directory: ").append(workingDirectory).append('\n');
		sb.append("Environment:\n");
		for (int i = 0; i < environment.length; i++) {
			sb.append('\t').append(environment[i]).append('\n');
		}
		sb.append("-----------------------------------------------\n");
		System.out.println(sb);
	}
	
	protected final String[] getCommandLine() {
		List<String> items = new ArrayList<String>();
		prepareCommandLine(items);
		return ArrayUtil.createFrom(items, String.class);
	}
	
	public void prepareCommandLine(List<String> items) {
		items.add(processFile.toOSString());
		
		List<String> scriptArgs = processArguments;
		items.addAll(scriptArgs);
	}
	
	/**
	 * Returns a new process aborting if the process could not be created.
	 * 
	 * @param launch
	 *            the launch the process is contained in
	 * @param sp
	 *            the system process to wrap
	 * @param label
	 *            the label assigned to the process
	 * @param attributes
	 *            values for the attribute map
	 * @return the new process
	 * @throws CoreException
	 *             problems occurred creating the process
	 * @since 2.0
	 * 
	 */
	protected IProcess newProcess(ILaunch launch, Process sp, String label, Map<String, String> attributes) 
			throws CoreException {
		
		this.sp = sp;
		IProcess process = DebugPlugin.newProcess(launch, sp, label, attributes);
		
		if (process == null) {
			sp.destroy();
			abort(LaunchingMessages.AbstractInterpreterRunner_0, null);
		}
		return process;
	}
	
	
	protected Map<String, String> getDefaultProcessMap() {
		Map<String, String> map = new HashMap<String, String>();
		map.put(IProcess.ATTR_PROCESS_TYPE, getProcessType());
		return map;
	}
	
	protected String getProcessType() {
		return ScriptLaunchConfigurationConstants.ID_SCRIPT_PROCESS_TYPE;
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
	
	public IProcess newProcessWithLabelUpdater(ILaunch launch, String cmdLineLabel, String baseProcessLabel, 
		Process sp) throws CoreException {
		final AtomicReference<IProcess> processRef = new AtomicReference<>(null);
		
		DebugPlugin.getDefault().addDebugEventListener(new ProcessLabelListener(launch, processRef));
		IProcess process = newProcess(launch, sp, baseProcessLabel, getDefaultProcessMap());
		processRef.set(process);
		process.setAttribute(IProcess.ATTR_CMDLINE, cmdLineLabel);
		updateProcessLabel(launch, process);
		return process;
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