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

import java.io.IOException;
import java.nio.file.Path;

import melnorme.utilbox.misc.FileUtil;
import melnorme.utilbox.tests.TestsWorkingDir;
import dtool.tests.utils.MiscFileUtils;

public class MockCompilerInstalls {
	
	private static final String RESOURCE_CompilerInstalls = "compilerInstalls";
	
	public static final Path MOCK_COMPILERS_PATH = TestsWorkingDir.getWorkingDirPath().resolve("_compilerInstalls");
	
	public static final Path DEFAULT_DMD_INSTALL_LOCATION = MOCK_COMPILERS_PATH.resolve("DMD_archive");
	public static final Path DEFAULT_DMD_INSTALL_EXE_PATH = 
			DEFAULT_DMD_INSTALL_LOCATION.resolve("windows/bin/dmd.exe");
	public static final Path DEFAULT_GDC_INSTALL_EXE_PATH = 
			MOCK_COMPILERS_PATH.resolve("gdcInstall/bin/gdc");
	
	public static final Path DEFAULT_DMD_INSTALL_EXE_DIR = DEFAULT_DMD_INSTALL_EXE_PATH.getParent();
	
	
	public static void load() {
		// Not necessary due to static initialization, but mor readable.
	}
	
	static {
		try {
			setupMockCompilerInstalls();
		} catch (IOException e) {
			throw melnorme.utilbox.core.ExceptionAdapter.unchecked(e);
		}
	}
	
	protected static void setupMockCompilerInstalls() throws IOException {
		FileUtil.deleteDir(MOCK_COMPILERS_PATH);
		MiscFileUtils.copyDirContentsIntoDirectory(
			DToolTestResources.getTestResource(RESOURCE_CompilerInstalls).toPath(), MOCK_COMPILERS_PATH);
	}
	
}