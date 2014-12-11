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

import melnorme.utilbox.misc.Location;
import melnorme.utilbox.misc.StringUtil;
import dtool.engine.compiler_installs.CompilerInstall.ECompilerType;

public class CompilerInstallDetector {
	
	public CompilerInstallDetector() {
	}
	
	public CompilerInstall detectInstallFromCompilerCommandPath(Location commandPath) {
		String fileName = commandPath.path.getFileName().toString();
		
		if(executableMatches(fileName, "dmd")) {
			return detectDMDInstall(commandPath);
		} else if(executableMatches(fileName, "gdc")) {
			return detectGDCInstall(commandPath);
		} else if(executableMatches(fileName, "ldc2") || executableMatches(fileName, "ldc")) {
			return detectLDCInstall(commandPath);
		}
		
		return null;
	}
	
	protected CompilerInstall detectDMDInstall(Location commandPath) {
		Location cmdDir = commandPath.getParent();
		
		if(cmdDir.resolve("../../src/druntime").toFile().exists()) {
			return new CompilerInstall(commandPath, ECompilerType.DMD, 
				cmdDir.resolve("../../src/druntime/import"),
				cmdDir.resolve("../../src/phobos"));
		}
		// a MacOSX layout:
		if(cmdDir.resolve("../src/druntime").toFile().exists()) {
			return new CompilerInstall(commandPath, ECompilerType.DMD, 
				cmdDir.resolve("../src/druntime/import"),
				cmdDir.resolve("../src/phobos"));
		}
		// another MacOSX layout
		Location resolvedCmdPath = cmdDir.resolve("../share/dmd/bin/dmd");
		if(resolvedCmdPath.toFile().exists()) {
			Location resolvedCmdDir = resolvedCmdPath.getParent();
			if(resolvedCmdDir.resolve("../src/druntime").toFile().exists()) {
				return new CompilerInstall(resolvedCmdPath, ECompilerType.DMD, 
					resolvedCmdDir.resolve("../src/druntime/import"),
					resolvedCmdDir.resolve("../src/phobos"));
			}
		}
		
		if(cmdDir.resolve("../include/dlang/dmd").toFile().exists()) {
			return new CompilerInstall(commandPath, ECompilerType.DMD, 
				cmdDir.resolve("../include/dlang/dmd"));
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
	
	protected CompilerInstall detectLDCInstall(Location commandPath) {
		Location cmdDir = commandPath.getParent();
		
		if(cmdDir.resolve("../include/dlang/ldc").toFile().exists()) {
			return new CompilerInstall(commandPath, ECompilerType.LDC, 
				cmdDir.resolve("../include/dlang/ldc"));
		}
		
		if(cmdDir.resolve("../import/core").toFile().exists()) {
			return new CompilerInstall(commandPath, ECompilerType.LDC,
				cmdDir.resolve("../import/ldc"),
				cmdDir.resolve("../import"));
		}
		return null;
	}
	
	protected CompilerInstall detectGDCInstall(Location commandPath) {
		Location cmdDir = commandPath.getParent();
		
		if(cmdDir.resolve("../include/dlang/gdc").toFile().exists()) {
			return new CompilerInstall(commandPath, ECompilerType.GDC, 
				cmdDir.resolve("../include/dlang/gdc"));
		}
		
		CompilerInstall install = checkGDCLibrariesAt(cmdDir.resolve("../include/d"), commandPath);
		if(install != null) 
			return install;
		
		return checkGDCLibrariesAt(cmdDir.resolve("../include/d2"), commandPath);
	}
	
	protected CompilerInstall checkGDCLibrariesAt(Location includeD2Dir, Location commandPath) {
		if(includeD2Dir.toFile().exists()) {
			
			File[] d2entries = includeD2Dir.toFile().listFiles();
			if(d2entries == null) // Same as IOException
				return null;
			
			for (File d2entry : d2entries) {
				if(d2entry.isDirectory() && new File(d2entry, "object.di").exists()) {
					return new CompilerInstall(commandPath, ECompilerType.GDC, 
						Location.create_fromValid(d2entry.toPath())
					);
				}
			}
			
		}
		return null;
	}
	
	protected boolean executableMatches(String fileName, String executableName) {
		fileName = StringUtil.trimEnd(fileName, ".exe");
		return fileName.equals(executableName);
	}
	
}