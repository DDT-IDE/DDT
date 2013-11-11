/*******************************************************************************
 * Copyright (c) 2011, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package mmrnmhrm.core.launch;


import static melnorme.utilbox.core.Assert.AssertNamespace.assertUnreachable;

import java.text.MessageFormat;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.AssertionFailedException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.Launch;
import org.eclipse.dltk.internal.launching.DLTKLaunchingPlugin;
import org.eclipse.dltk.launching.AbstractScriptLaunchConfigurationDelegate;
import org.eclipse.dltk.launching.IInterpreterRunner;
import org.eclipse.dltk.launching.InterpreterConfig;
import org.eclipse.dltk.launching.LaunchingMessages;
import org.eclipse.dltk.launching.ScriptLaunchConfigurationConstants;
import org.eclipse.dltk.launching.ScriptRuntime;

/**
 * Abstract extension for running in non-debug mode, and cleanup some of DLTK behavior 
 */
public abstract class AbstractScriptLaunchConfigurationDelegateExtension 
	extends AbstractScriptLaunchConfigurationDelegate {
	
	public static CoreException createCoreException(Throwable exception, int code) {
		return new CoreException(
				new Status(
						IStatus.ERROR,
						DLTKLaunchingPlugin.PLUGIN_ID,
						code,
						exception.getMessage(), exception));
	}
	
	@Override
	public ILaunch getLaunch(ILaunchConfiguration configuration, String mode) throws CoreException {
		
		ILaunchConfigurationWorkingCopy workingCopy = configuration.getWorkingCopy();
		// Remove some DLTK attributes that affect how our launch runs
		cleanDLTKDebugConfig(workingCopy);
		
		final Launch launch = new Launch(configuration, mode, null);
		return launch;
	}
	
	public static void cleanDLTKDebugConfig(ILaunchConfigurationWorkingCopy workingCopy) {
		workingCopy.removeAttribute(DebugPlugin.ATTR_PROCESS_FACTORY_ID);
		workingCopy.setAttribute(ScriptLaunchConfigurationConstants.ATTR_DEBUG_CONSOLE, false);
		workingCopy.setAttribute(ScriptLaunchConfigurationConstants.ATTR_USE_INTERACTIVE_CONSOLE, false);
		workingCopy.setAttribute("org.eclipse.dltk.debug.debugConsole", false);
	}
	
	@Override
	protected final void setDebugConsoleAttributes(Launch launch, ILaunchConfiguration configuration) throws CoreException {
		throw assertUnreachable();
	}
	
	@Override
	protected final void setDebugOptions(Launch launch, ILaunchConfiguration configuration) throws CoreException {
		throw assertUnreachable();
	}
	
	@Override
	public void launch(ILaunchConfiguration configuration, String mode,
			ILaunch launch, IProgressMonitor monitor) throws CoreException {

		try {
			IProject project = ScriptRuntime.getScriptProject(configuration).getProject();

			if (monitor == null) {
				monitor = new NullProgressMonitor();
			}

			monitor.beginTask(
				MessageFormat.format(
					LaunchingMessages.AbstractScriptLaunchConfigurationDelegate_startingLaunchConfiguration,
					new Object[] { configuration.getName() }),
				10
			);
			if (monitor.isCanceled()) {
				return;
			}

			monitor.subTask(
				MessageFormat.format(LaunchingMessages.AbstractScriptLaunchConfigurationDelegate_validatingLaunchConfiguration,
					new Object[] { configuration.getName() })
			);
			
			validateLaunchConfiguration(configuration, mode, project);
			monitor.worked(1);
			if (monitor.isCanceled()) {
				return;
			}

			monitor.worked(1);
			monitor.worked(1);
			
			// Real run
			InterpreterConfig config = createInterpreterConfig(configuration, launch);
			monitor.subTask(LaunchingMessages.AbstractScriptLaunchConfigurationDelegate_executingRunner);
			launch0(config, configuration, launch, new SubProgressMonitor(monitor, 7));

		} catch (CoreException e) {
			tryHandleStatus(e, this);
		} catch (AssertionFailedException e) {
			tryHandleStatus(createCoreException(e, ScriptLaunchConfigurationConstants.ERR_INTERNAL_ERROR), this);
		} catch (IllegalArgumentException e) {
			tryHandleStatus(createCoreException(e, ScriptLaunchConfigurationConstants.ERR_INTERNAL_ERROR), this);
		} finally {
			monitor.done();
		}
	}
	
	@Override
	protected void validateLaunchConfiguration(ILaunchConfiguration configuration, String mode, IProject project)
			throws CoreException {
		if(ILaunchManager.DEBUG_MODE.equals(mode)) {
			abort("Debugging not supported", null);
		}
	}
	
	@Override
	protected InterpreterConfig createInterpreterConfig(ILaunchConfiguration configuration, ILaunch launch)
			throws CoreException {
		return super.createInterpreterConfig(configuration, launch);
	}
	
	@Override
	public final IInterpreterRunner getInterpreterRunner(ILaunchConfiguration configuration, String mode)
			throws CoreException {
		throw assertUnreachable();
	}
	
	@Override
	protected final void runRunner(ILaunchConfiguration configuration, IInterpreterRunner runner, 
			InterpreterConfig config, ILaunch launch, IProgressMonitor monitor) throws CoreException {
		throw assertUnreachable();
	}
	
	protected abstract void launch0(InterpreterConfig config, ILaunchConfiguration configuration, 
			ILaunch launch, IProgressMonitor monitor) 
			throws CoreException;
	
}