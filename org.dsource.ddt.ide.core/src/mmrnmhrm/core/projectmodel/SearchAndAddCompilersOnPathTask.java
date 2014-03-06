/*******************************************************************************
 * Copyright (c) 2014, 2014 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package mmrnmhrm.core.projectmodel;


import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import melnorme.lang.ide.core.LangCore;
import melnorme.utilbox.misc.MiscUtil;
import mmrnmhrm.core.DeeCore;
import mmrnmhrm.core.DeeCoreMessages;
import mmrnmhrm.core.compiler_installs.CommonInstallType;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.dltk.core.environment.IFileHandle;
import org.eclipse.dltk.launching.IInterpreterInstall;
import org.eclipse.dltk.launching.IInterpreterInstallType;
import org.eclipse.dltk.launching.InterpreterStandin;
import org.eclipse.dltk.launching.ScriptRuntime;

public class SearchAndAddCompilersOnPathTask {
	
	public static class SearchAndAddCompilersOnPathJob extends Job {
		
		public SearchAndAddCompilersOnPathJob() {
			super(DeeCoreMessages.SearchAndAddCompilersOnPath_JobName);
		}
		
		@Override
		protected IStatus run(IProgressMonitor monitor) {
			SearchAndAddCompilersOnPathTask task = new SearchAndAddCompilersOnPathTask(monitor);
			task.searchForCompilers();
			task.applyFoundInstalls();
			
			return LangCore.createStatus("ok");
		}
	}
	
	protected final IProgressMonitor monitor;
	protected List<InterpreterStandin> foundInstalls = new ArrayList<>();
	
	public SearchAndAddCompilersOnPathTask(IProgressMonitor monitor) {
		this.monitor = monitor;
		this.foundInstalls = new ArrayList<>();
	}
	
	public List<InterpreterStandin> getFoundInstalls() {
		return foundInstalls;
	}
	
	public void searchForCompilers() {
		searchEnvironmentVar("PATH");
	}
	
	public void applyFoundInstalls() {
		// TODO: There are some bugs related to this, in particular, saving prefs
		// Also, I'm not sure how safe it is to call this outside UI thread
		if(monitor.isCanceled()) 
			return;
		for (InterpreterStandin install : foundInstalls) {
			install.convertToRealInterpreter();
		}
	}
	
	protected void searchEnvironmentVar(String envVarName) {
		String fullPathString = System.getenv(envVarName);
		searchPathsString(fullPathString, envVarName);
	}
	
	protected void searchPathsString(String pathsString, String envVarName) {
		String separator = getPathsSeparator();
		String[] paths = pathsString.split(separator);
		
		searchPaths(paths, envVarName);
	}
	
	protected static String getPathsSeparator() {
		if(MiscUtil.OS_IS_WINDOWS) {
			return ";";
		}
		return ":";
	}
	
	protected void searchPaths(String[] paths, String envVarName) {
		for (String pathString : paths) {
			if(monitor.isCanceled()) {
				return;
			}
			
			Path path = MiscUtil.createValidPath(pathString);
			if(path == null) {
				DeeCore.logWarning("Invalid path: " + pathString + " in env variable: " + envVarName);
				continue;
			}
			if(!path.toFile().isDirectory()) {
				continue;
			}
			searchPath(path);
		}
	}
	
	protected void searchPath(Path path) {
		// Make sure we get the real install type instances, so we can get installs
		for (CommonInstallType compilerInstallType : getDeeInstallTypes()) {
			IFileHandle compilerInstallLocation = compilerInstallType.directoryHasCompilerPresent(path);
			if(compilerInstallLocation != null) {
				addCompilerInstall(compilerInstallType, compilerInstallLocation);
			}
		}
	}
	
	protected List<CommonInstallType> deeInstallTypes;
	
	protected List<CommonInstallType> getDeeInstallTypes() {
		if(deeInstallTypes == null) {
			deeInstallTypes = new ArrayList<>();
			for (IInterpreterInstallType installType : ScriptRuntime.getInterpreterInstallTypes(DeeCore.NATURE_ID)) {
				if(installType instanceof CommonInstallType) {
					CommonInstallType commonInstallType = (CommonInstallType) installType;
					deeInstallTypes.add(commonInstallType);
				}
			}
		}
		return deeInstallTypes;
	}
	
	protected void addCompilerInstall(IInterpreterInstallType installType, IFileHandle compilerLocation) {
		IInterpreterInstall existingInstall = getExistingInstall(installType, compilerLocation);
		if(existingInstall != null && !existingInstall.getName().startsWith("AUTO::")) {
			return;
		}
		// Note: there can be multiple installs in same location if all have different compiler type. 
		
		String installName = "AUTO::" + compilerLocation.getPath().toString();
		InterpreterStandin install = new InterpreterStandin(installType, installName);
		
		install.setInstallLocation(compilerLocation);
		install.setName(installName);
		install.setInterpreterArgs(null);
		install.setLibraryLocations(installType.getDefaultLibraryLocations(compilerLocation));
		
		foundInstalls.add(install);
	}
	
	protected IInterpreterInstall getExistingInstall(IInterpreterInstallType installType, 
			IFileHandle installLocation) {
		IInterpreterInstall[] interpreterInstalls = installType.getInterpreterInstalls();
		for (IInterpreterInstall interpreterInstall : interpreterInstalls) {
			if(interpreterInstall.getInstallLocation().equals(installLocation)) {
				return interpreterInstall;
			}
		}
		return null;
	}
	
}