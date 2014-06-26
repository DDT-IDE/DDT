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
package dtool.engine;

import static dtool.tests.MockCompilerInstalls.MOCK_COMPILERS_PATH;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import dtool.engine.compiler_installs.CompilerInstall;
import dtool.engine.compiler_installs.CompilerInstall.ECompilerType;
import dtool.engine.compiler_installs.CompilerInstallDetector;
import dtool.engine.compiler_installs.SearchCompilersOnPathOperation;
import dtool.tests.CommonDToolTest;
import dtool.util.SearchPathEnvOperation;

public class CompilerInstallDetector_Test extends CommonDToolTest {
	
	public static final Path MOCK_DMD = MOCK_COMPILERS_PATH.resolve("DMD_archive");
	public static final Path MOCK_DMD_CMDPATH = MOCK_DMD.resolve("windows/bin/dmd.exe");
	public static final Path MOCK_DMD_LINUX = MOCK_COMPILERS_PATH.resolve("DMD-linux");
	public static final Path MOCK_DMD2_SYSTEM_CMDPATH = MOCK_DMD_LINUX.resolve("usr/bin/dmd");
	public static final Path MOCK_DMD_LINUX2 = MOCK_COMPILERS_PATH.resolve("DMD-linux2");
	public static final Path MOCK_DMD2_SYSTEM2_CMDPATH2 = MOCK_DMD_LINUX2.resolve("usr/bin/dmd");

	public static final Path MOCK_GDC = MOCK_COMPILERS_PATH.resolve("gdcInstall");
	public static final Path MOCK_GDC_CMDPATH = MOCK_GDC.resolve("bin/gdc");
	public static final Path MOCK_GDC_B = MOCK_COMPILERS_PATH.resolve("gdcInstallB");
	public static final Path MOCK_GDC_B_CMDPATH = MOCK_GDC_B.resolve("bin/gdc");

	public static final Path MOCK_LDC_ARCHIVE = MOCK_COMPILERS_PATH.resolve("ldc-archive");
	
	
	public static final Path MULTIPLE_IN_ONE_PATH = MOCK_COMPILERS_PATH.resolve("_multipleInSameLocation/bin");

	
	protected final String PATH_SEP = SearchPathEnvOperation.getPathsSeparator();
	protected CompilerInstallDetector detector;
	
	
	@Test
	public void testBasic() throws Exception { testBasic$(); }
	public void testBasic$() throws Exception {
		detector = new CompilerInstallDetector();
		
		testDetectInstall(MOCK_DMD, "windows/bin/dmd.exe", ECompilerType.DMD, list(
			"src/druntime/import",
			"src/phobos"
		));
		testDetectInstall(MOCK_DMD_LINUX, "usr/bin/dmd", ECompilerType.DMD, list(
			"usr/include/dmd/druntime/import",
			"usr/include/dmd/phobos"
		));
		testDetectInstall(MOCK_DMD_LINUX2, "usr/bin/dmd", ECompilerType.DMD, list(
			"include/d/dmd/druntime/import",
			"include/d/dmd/phobos"
		));
		
		
		testDetectInstall(MOCK_GDC, "bin/gdc", ECompilerType.GDC, list(
			"include/d2/4.5.2/"
		));
		testDetectInstall(MOCK_GDC_B, "bin/gdc", ECompilerType.GDC, list(
			"include/d/4.6.1/"
		));
		
		testDetectInstall(MOCK_LDC_ARCHIVE, "bin/ldc2", ECompilerType.LDC, list(
			"import/core",
			"import/ldc",
			"import/"
		));
	}
	
	protected void testDetectInstall(Path installPath, String compilerPathStr, ECompilerType type, 
			List<String> pathStrings) {
		Path compilerPath = installPath.resolve(compilerPathStr);
		CompilerInstall install = detector.detectInstallFromCompilerCommandPath(compilerPath);
		checkInstall(install, compilerPath, type, installPath, pathStrings);
	}
	
	protected void checkInstall(CompilerInstall install, Path compilerPath, ECompilerType compilerType, 
			Path installPath, List<String> pathStrings) {
		ArrayList<Path> paths = new ArrayList<>(pathStrings.size());
		for (String pathString : pathStrings) {
			paths.add(installPath.resolve(pathString));
		}
		assertEquals(install == null, compilerType == null);
		assertEquals(install.getCompilerPath(), compilerPath);
		assertEquals(install.getCompilerType(), compilerType);
		assertEquals(install.getLibrarySourceFolders(), paths);
	}
	
	@Test
	public void testSearchTask() throws Exception { testSearchTask$(); }
	public void testSearchTask$() throws Exception {
		testWithPathVar(MULTIPLE_IN_ONE_PATH.toString());
		
		testWithPathVar(workingDirPath("__NON_EXISTING___###__").toString() + PATH_SEP + 
			MULTIPLE_IN_ONE_PATH.toString());
	}
	
	protected void testWithPathVar(String pathsString) {
		SearchCompilersOnPathOperation compilerSearch = new SearchCompilersOnPathOperation() {
			@Override
			protected void handleWarning(String message) {
				assertFail();
			}
		};
		compilerSearch.searchPathsString(pathsString, "_dummy_");
		
		List<CompilerInstall> foundInstalls = compilerSearch.getFoundInstalls();
		assertTrue(foundInstalls.size() == 2);
		
		checkInstall(foundInstalls.get(0), MULTIPLE_IN_ONE_PATH.resolve("gdc"), ECompilerType.GDC, 
			MULTIPLE_IN_ONE_PATH.getParent(), 
			list("include/d/4.6.1/"));
		checkInstall(foundInstalls.get(1), MULTIPLE_IN_ONE_PATH.resolve("ldc2"), ECompilerType.LDC, 
			MULTIPLE_IN_ONE_PATH.getParent(), 
			list("import/core", "import/ldc", "import/"));
	}
	
}