/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 * Contributors:
 *     ??? (DLTK) - initial API and implementation
 *     Bruno Medeiros - modifications     
 *******************************************************************************/
package melnorme.lang.launching;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
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
import org.eclipse.dltk.core.environment.IEnvironment;
import org.eclipse.dltk.core.environment.IExecutionEnvironment;
import org.eclipse.dltk.core.environment.IExecutionLogger;
import org.eclipse.dltk.internal.launching.DLTKLaunchingPlugin;
import org.eclipse.dltk.launching.EnvironmentVariable;
import org.eclipse.dltk.launching.IInterpreterRunner;
import org.eclipse.dltk.launching.InterpreterConfig;
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
public abstract class AbstractInterpreterRunner_Mod implements IInterpreterRunner {
	
	protected void abort(String message, Throwable exception)
		throws CoreException {
		throw new CoreException(new Status(IStatus.ERROR,
			DLTKLaunchingPlugin.PLUGIN_ID,
			ScriptLaunchConfigurationConstants.ERR_INTERNAL_ERROR, message,
			exception));
	}
	
	protected void abort(String message, Throwable exception, int code)
		throws CoreException {
		throw new CoreException(new Status(IStatus.ERROR,
			DLTKLaunchingPlugin.PLUGIN_ID, code, message, exception));
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
			alterConfig(launch, config);
			monitor.worked(1);
			monitor.subTask(LaunchingMessages.AbstractInterpreterRunner_running);
			rawRun(launch, config);
			monitor.worked(4);
			
		} finally {
			monitor.done();
		}
	}
	
	@SuppressWarnings("unused")
	protected void alterConfig(ILaunch launch, InterpreterConfig config) {
	}
	
	protected void traceExecution(String processLabel, String cmdLineLabel,
		IPath workingDirectory, String[] environment) {
		StringBuffer sb = new StringBuffer();
		sb.append("-----------------------------------------------\n"); //$NON-NLS-1$
		sb.append("Running ").append(processLabel).append('\n'); //$NON-NLS-1$
		sb.append("Command line: ").append(cmdLineLabel).append('\n'); //$NON-NLS-1$
		sb.append("Working directory: ").append(workingDirectory).append('\n'); //$NON-NLS-1$
		sb.append("Environment:\n"); //$NON-NLS-1$
		for (int i = 0; i < environment.length; i++) {
			sb.append('\t').append(environment[i]).append('\n');
		}
		sb.append("-----------------------------------------------\n"); //$NON-NLS-1$
		System.out.println(sb);
	}
	
	public static class LaunchLogger implements IExecutionLogger {
		
		protected final String fileName;
		
		public LaunchLogger() {
			fileName = new java.text.SimpleDateFormat("yyyy-MM-dd-HHmm").format(new Date()) + ".log";
		}
		
		@Override
		public void logLine(String line) {
			final File file = new File(System.getProperty("user.home"), fileName);
			try {
				final FileWriter writer = new FileWriter(file, true);
				try {
					writer.write(line);
					writer.write("\n");
				} finally {
					try {
						writer.close();
					} catch (IOException e) {
						// ignore
					}
				}
			} catch (IOException e) {
				// ignore?
			}
		}
	}
	
	protected IProcess rawRun(final ILaunch launch, InterpreterConfig config)
		throws CoreException {
		
		checkConfig(config);
		
		String[] cmdLine = renderCommandLine(config);
		IPath workingDirectory = config.getWorkingDirectoryPath();
		String[] environment = getEnvironmentVariablesAsStrings(config);
		
		final String cmdLineLabel = renderCommandLineLabel(cmdLine);
		final String baseProcessLabel = renderBaseProcessLabel(cmdLine);
		
		if (DLTKLaunchingPlugin.TRACE_EXECUTION) {
			traceExecution(baseProcessLabel, cmdLineLabel, workingDirectory, environment);
		}
		
		IExecutionEnvironment exeEnv = getExecEnvironment(config);
		IExecutionLogger logger = DLTKLaunchingPlugin.LOGGING_CATCH_OUTPUT.isEnabled() ? new LaunchLogger() : null;
		
		Process p = exeEnv.exec(cmdLine, workingDirectory, environment, logger);
		if (p == null) {
			abort(LaunchingMessages.AbstractInterpreterRunner_executionWasCancelled, null);
		}
		
		launch.setAttribute(DLTKLaunchingPlugin.LAUNCH_COMMAND_LINE, cmdLineLabel);
		
		return newProcessWithLabelUpdater(launch, cmdLineLabel, baseProcessLabel, p);
	}
	
	protected abstract void checkConfig(InterpreterConfig config) throws CoreException;
	
	protected abstract String[] renderCommandLine(InterpreterConfig config);
	
	protected String[] getEnvironmentVariablesAsStrings(InterpreterConfig config) {
		EnvironmentVariable[] additionalVars = null; // Default: no additional vars are added 
		return config.getEnvironmentAsStringsIncluding(additionalVars);
	}
	
	protected IExecutionEnvironment getExecEnvironment(InterpreterConfig config) {
		IEnvironment environment = config.getEnvironment();
		return (IExecutionEnvironment) environment.getAdapter(IExecutionEnvironment.class);
	}
	
	/**
	 * Returns a new process aborting if the process could not be created.
	 * 
	 * @param launch
	 *            the launch the process is contained in
	 * @param p
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
	protected IProcess newProcess(ILaunch launch, Process p, String label,
			Map<String, String> attributes) throws CoreException {
		IProcess process = DebugPlugin.newProcess(launch, p, label, attributes);
		if (process == null) {
			p.destroy();
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
		Process p) throws CoreException {
		final AtomicReference<IProcess> process = new AtomicReference<>(null);
		
		DebugPlugin.getDefault().addDebugEventListener(new ProcessLabelListener(launch, process));
		process.set(newProcess(launch, p, baseProcessLabel, getDefaultProcessMap()));
		process.get().setAttribute(IProcess.ATTR_CMDLINE, cmdLineLabel);
		updateProcessLabel(launch, process.get());
		return process.get();
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
					if (event.getKind() == DebugEvent.CHANGE
						|| event.getKind() == DebugEvent.TERMINATE) {
						updateProcessLabel(launch, process.get());
						if (event.getKind() == DebugEvent.TERMINATE) {
							DebugPlugin.getDefault().removeDebugEventListener(this);
						}
					}
				}
			}
		}
	}
	
	protected static void updateProcessLabel(final ILaunch launch, final IProcess process) {
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
		process.setAttribute(IProcess.ATTR_PROCESS_LABEL, buffer.toString());
	}
	
}