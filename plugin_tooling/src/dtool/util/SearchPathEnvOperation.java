/*******************************************************************************
 * Copyright (c) 2014, 2014 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package dtool.util;

import java.nio.file.Path;

import melnorme.utilbox.misc.MiscUtil;


public abstract class SearchPathEnvOperation {
	
	public SearchPathEnvOperation() {
	}
	
	public void searchEnvironmentVar(String envVarName) {
		String fullPathString = System.getenv(envVarName);
		if(fullPathString != null) {
			searchPathsString(fullPathString, envVarName);
		}
	}
	
	public void searchPathsString(String pathsString, String envVarName) {
		String separator = getPathsSeparator();
		String[] paths = pathsString.split(separator);
		
		searchPaths(paths, envVarName);
	}
	
	public static String getPathsSeparator() {
		if(MiscUtil.OS_IS_WINDOWS) {
			return ";";
		}
		return ":";
	}
	
	protected void searchPaths(String[] paths, String envVarName) {
		for (String pathString : paths) {
			
			Path path = MiscUtil.createPathOrNull(pathString);
			if(path == null) {
				handleWarning("Invalid path: " + pathString + " in env variable: " + envVarName);
				continue;
			}
			if(!path.toFile().isDirectory()) {
				continue;
			}
			searchPathEntry(path);
		}
	}
	
	protected abstract void handleWarning(String message);
	
	protected abstract void searchPathEntry(Path path);
	
}