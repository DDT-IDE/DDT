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
package dtool.engine.compiler_installs;

import java.io.File;
import java.nio.file.Path;

import melnorme.utilbox.misc.StringUtil;
import dtool.engine.compiler_installs.CompilerInstall.ECompilerType;

public class CompilerInstallDetector {
	
	public static final String SPECIAL_EMPTY_INSTALL = "__special_empty_install";

	public CompilerInstallDetector() {
	}
	
	public CompilerInstall detectInstallFromCompilerCommandPath(Path commandPath) {
		String fileName = commandPath.getFileName().toString();
		
		if(fileName.equals(SPECIAL_EMPTY_INSTALL)) {
			// Special compiler install with no modules. Useful for testing purposes.
			return new CompilerInstall(commandPath, ECompilerType.OTHER);
		}
		
		if(executableMatches(fileName, "dmd")) {
			return detectDMDInstall(commandPath);
		} else if(executableMatches(fileName, "gdc")) {
			return detectGDCInstall(commandPath);
		} else if(executableMatches(fileName, "ldc2") || executableMatches(fileName, "ldc")) {
			return detectLDCInstall(commandPath);
		}
		
		return null;
	}
	
	protected CompilerInstall detectDMDInstall(Path commandPath) {
		Path cmdDir = commandPath.getParent();
		
		if(cmdDir.resolve("../../src/druntime").toFile().exists()) {
			return new CompilerInstall(commandPath, ECompilerType.DMD, 
				cmdDir.resolve("../../src/druntime/import"),
				cmdDir.resolve("../../src/phobos"));
		}
		
		if(cmdDir.resolve("../include/dmd").toFile().exists()) {
			return new CompilerInstall(commandPath, ECompilerType.DMD, 
				cmdDir.resolve("../include/dmd/druntime/import"),
				cmdDir.resolve("../include/dmd/phobos"));
		}
		
		if(cmdDir.resolve("../../include/d/dmd").toFile().exists()) {
			return new CompilerInstall(commandPath, ECompilerType.DMD, 
				cmdDir.resolve("../../include/d/dmd/druntime/import"),
				cmdDir.resolve("../../include/d/dmd/phobos"));
		}
		return null;
	}
	
	protected CompilerInstall detectLDCInstall(Path commandPath) {
		Path cmdDir = commandPath.getParent();
		
		if(cmdDir.resolve("../import/core").toFile().exists()) {
			return new CompilerInstall(commandPath, ECompilerType.LDC,
				cmdDir.resolve("../import/core"),
				cmdDir.resolve("../import/ldc/"),
				cmdDir.resolve("../import"));
		}
		return null;
	}
	
	protected CompilerInstall detectGDCInstall(Path commandPath) {
		Path cmdDir = commandPath.getParent();
		
		CompilerInstall install = checkGDCLibrariesAt(cmdDir.resolve("../include/d"), commandPath);
		if(install != null) 
			return install;
		
		return checkGDCLibrariesAt(cmdDir.resolve("../include/d2"), commandPath);
	}
	
	protected CompilerInstall checkGDCLibrariesAt(Path includeD2Dir, Path commandPath) {
		if(includeD2Dir.toFile().exists()) {
			
			File[] d2entries = includeD2Dir.toFile().listFiles();
			if(d2entries == null) // Same as IOException
				return null;
			
			for (File d2entry : d2entries) {
				if(d2entry.isDirectory() && new File(d2entry, "object.di").exists()) {
					return new CompilerInstall(commandPath, ECompilerType.GDC, d2entry.toPath());
				}
			}
			
		}
		return null;
	}
	
	protected boolean executableMatches(String fileName, String executableName) {
		fileName = StringUtil.trimEnding(fileName, ".exe");
		return fileName.equals(executableName);
	}
	
}