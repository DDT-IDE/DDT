/*******************************************************************************
 * Copyright (c) 2014 Bruno Medeiros and other Contributors.
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

import java.util.ArrayList;
import java.util.List;

import melnorme.lang.utils.SearchPathEnvOperation;
import melnorme.utilbox.misc.Location;

import org.junit.Test;

import dtool.engine.compiler_installs.CompilerInstall;
import dtool.engine.compiler_installs.CompilerInstall.ECompilerType;
import dtool.engine.compiler_installs.CompilerInstallDetector;
import dtool.engine.compiler_installs.SearchCompilersOnPathOperation;
import dtool.tests.CommonDToolTest;

public class CompilerInstallDetector_Test extends CommonDToolTest {
	
	public static final Location MOCK_DMD = loc(MOCK_COMPILERS_PATH, "DMD_archive");
	public static final Location MOCK_DMD_CMDPATH = loc(MOCK_DMD, "windows/bin/dmd.exe");
	public static final Location MOCK_DMD_LINUX = loc(MOCK_COMPILERS_PATH, "DMD-linux");
	public static final Location MOCK_DMD2_SYSTEM_CMDPATH = loc(MOCK_DMD_LINUX, "usr/bin/dmd");
	public static final Location MOCK_DMD_LINUX2 = loc(MOCK_COMPILERS_PATH, "DMD-linux2");
	public static final Location MOCK_DMD2_SYSTEM2_CMDPATH2 = loc(MOCK_DMD_LINUX2, "usr/bin/dmd");

	public static final Location MOCK_GDC = loc(MOCK_COMPILERS_PATH, "gdcInstall");
	public static final Location MOCK_GDC_CMDPATH = loc(MOCK_GDC, "bin/gdc");
	public static final Location MOCK_GDC_B = loc(MOCK_COMPILERS_PATH, "gdcInstallB");
	public static final Location MOCK_GDC_B_CMDPATH = loc(MOCK_GDC_B, "bin/gdc");

	public static final Location MOCK_LDC_ARCHIVE = loc(MOCK_COMPILERS_PATH, "ldc-archive");
	
	
	public static final Location MULTIPLE_IN_ONE_PATH = loc(MOCK_COMPILERS_PATH, "_multipleInSameLocation/bin");

	
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
		
		final Location MOCK_DMD_MACOSX = MOCK_COMPILERS_PATH.resolve_fromValid("DMD-macosx/share/dmd");
		testDetectInstall(MOCK_DMD_MACOSX, "bin/dmd", ECompilerType.DMD, list(
			"src/druntime/import",
			"src/phobos"
		));
		testDetectInstall(MOCK_DMD_MACOSX, "../../bin/dmd", "bin/dmd", 
			ECompilerType.DMD, list(
			"src/druntime/import",
			"src/phobos"
		));
		
		
		testDetectInstall(MOCK_GDC, "bin/gdc", ECompilerType.GDC, list(
			"include/d2/4.5.2/"
		));
		testDetectInstall(MOCK_GDC_B, "bin/gdc", ECompilerType.GDC, list(
			"include/d/4.6.1/"
		));
		
		testDetectInstall(MOCK_LDC_ARCHIVE, "bin/ldc2", ECompilerType.LDC, list(
			"import/",
			"import/ldc"
		));
		
		// Arch Linux
		testDetectInstall(MOCK_COMPILERS_PATH.resolve_fromValid("archLinux/usr"), "bin/dmd", ECompilerType.DMD, list(
			"include/dlang/dmd"
		));
		testDetectInstall(MOCK_COMPILERS_PATH.resolve_fromValid("archLinux/usr"), "bin/ldc2", ECompilerType.LDC, list(
			"include/dlang/ldc"
		));
		testDetectInstall(MOCK_COMPILERS_PATH.resolve_fromValid("archLinux/usr"), "bin/gdc", ECompilerType.GDC, list(
			"include/dlang/gdc"
		));
	}
	
	protected void testDetectInstall(Location installPath, String compilerPathStr, ECompilerType type, 
			List<String> pathStrings) {
		testDetectInstall(installPath, compilerPathStr, compilerPathStr, type, pathStrings);
	}
	
	protected void testDetectInstall(Location installPath, String compilerPathStr, String resolvedCompilerPathStr,
			ECompilerType type, List<String> pathStrings) {
		Location compilerPath = installPath.resolve_fromValid(compilerPathStr);
		CompilerInstall install = detector.detectInstallFromCompilerCommandPath(compilerPath);
		Location resolvedCompilerPath = installPath.resolve_fromValid(resolvedCompilerPathStr);
		checkInstall(install, resolvedCompilerPath, type, installPath, pathStrings);
	}
	
	protected void checkInstall(CompilerInstall install, Location compilerPath, ECompilerType compilerType, 
			Location installPath, List<String> pathStrings) {
		ArrayList<Location> paths = new ArrayList<>(pathStrings.size());
		for (String pathString : pathStrings) {
			paths.add(installPath.resolve_fromValid(pathString));
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
		
		testWithPathVar(workingDirLoc("__NON_EXISTING___###__").toString() + PATH_SEP + 
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
		
		checkInstall(foundInstalls.get(0), MULTIPLE_IN_ONE_PATH.resolve_fromValid("gdc"), ECompilerType.GDC, 
			MULTIPLE_IN_ONE_PATH.getParent(), 
			list("include/d/4.6.1/"));
		checkInstall(foundInstalls.get(1), MULTIPLE_IN_ONE_PATH.resolve_fromValid("ldc2"), ECompilerType.LDC, 
			MULTIPLE_IN_ONE_PATH.getParent(), 
			list("import/", "import/ldc"));
	}
	
}