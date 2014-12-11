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
package dtool.tests;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;

import java.io.IOException;

import melnorme.lang.utils.MiscFileUtils;
import melnorme.utilbox.misc.FileUtil;
import melnorme.utilbox.misc.Location;
import melnorme.utilbox.tests.TestsWorkingDir;
import dtool.engine.DToolServer;
import dtool.engine.compiler_installs.CompilerInstall;

public class MockCompilerInstalls {
	
	private static final String RESOURCE_CompilerInstalls = "compilerInstalls";
	
	public static final Location MOCK_COMPILERS_PATH = TestsWorkingDir.getWorkingDir("_compilerInstalls");
	
	public static final Location DEFAULT_DMD_INSTALL_BaseLocation = 
			MOCK_COMPILERS_PATH.resolve_fromValid("DMD_archive");
	public static final Location DEFAULT_DMD_INSTALL_EXE_PATH = 
			DEFAULT_DMD_INSTALL_BaseLocation.resolve_fromValid("windows/bin/dmd.exe");
	public static final Location DEFAULT_GDC_INSTALL_EXE_PATH = 
			MOCK_COMPILERS_PATH.resolve_fromValid("gdcInstall/bin/gdc");
	
	public static final Location DMD_CompilerLocation = DEFAULT_DMD_INSTALL_EXE_PATH; 
	public static final Location GDC_CompilerLocation = DEFAULT_GDC_INSTALL_EXE_PATH;
	
	public static final CompilerInstall DMD_CompilerInstall;
	
	static {
		try {
			setupMockCompilerInstalls();
		} catch (IOException e) {
			throw melnorme.utilbox.core.ExceptionAdapter.unchecked(e);
		}
		DMD_CompilerInstall = assertNotNull(
			DToolServer.getCompilerInstallForPath(DEFAULT_DMD_INSTALL_EXE_PATH));
	}
	
	protected static void setupMockCompilerInstalls() throws IOException {
		FileUtil.deleteDir(MOCK_COMPILERS_PATH);
		MiscFileUtils.copyDirContentsIntoDirectory(
			DToolTestResources.getTestResourceFile(RESOURCE_CompilerInstalls).toPath(), MOCK_COMPILERS_PATH.path);
	}
	
}