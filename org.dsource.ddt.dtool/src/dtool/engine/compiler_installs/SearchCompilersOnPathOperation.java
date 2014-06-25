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
package dtool.engine.compiler_installs;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import dtool.util.SearchPathEnvOperation;

public abstract class SearchCompilersOnPathOperation extends SearchPathEnvOperation {
	
	protected final CompilerInstallDetector detector = new CompilerInstallDetector();
	protected final List<CompilerInstall> foundInstalls = new ArrayList<>();
	
	public void searchForCompilersInPathEnvVars() {
		searchEnvironmentVar("DUB_COMPILERS_PATH");
		searchEnvironmentVar("PATH");
	}
	
	@Override
	protected void searchPathEntry(Path pathEntry) {
		if(executableExists(pathEntry, "dmd")) {
			addPossibleInstall(detector.detectDMDInstall(pathEntry));
		}
		if(executableExists(pathEntry, "gdc")) {
			addPossibleInstall(detector.detectGDCInstall(pathEntry));
		}
		if(executableExists(pathEntry, "ldc") || executableExists(pathEntry, "ldc2")) {
			addPossibleInstall(detector.detectLDCInstall(pathEntry));
		}
	}
	
	protected void addPossibleInstall(CompilerInstall install) {
		if(install != null) {
			foundInstalls.add(install);
		}
	}
	
	protected boolean executableExists(Path pathEntry, String executableFileName) {
		return pathEntry.resolve(executableFileName).toFile().exists() || 
				pathEntry.resolve(executableFileName + ".exe").toFile().exists();
	}
	
	public List<CompilerInstall> getFoundInstalls() {
		return foundInstalls;
	}
	
}