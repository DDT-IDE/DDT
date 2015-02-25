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

import static dtool.tests.MockCompilerInstalls.DEFAULT_DMD_INSTALL_BaseLocation;
import static dtool.tests.MockCompilerInstalls.DEFAULT_DMD_INSTALL_EXE_PATH;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.util.HashMap;
import java.util.HashSet;

import melnorme.lang.tooling.context.ModuleFullName;
import melnorme.lang.tooling.context.ModuleSourceException;
import melnorme.utilbox.core.CommonException;
import melnorme.utilbox.misc.Location;
import melnorme.utilbox.misc.PathUtil;

import org.junit.Test;

import dtool.dub.BundlePath;
import dtool.engine.StandardLibraryResolution.MissingStandardLibraryResolution;
import dtool.engine.compiler_installs.CompilerInstall.ECompilerType;

public class BundleResolution_ModuleListTest extends CommonSemanticManagerTest {
	
	public static class BundleFilesChecker {
		
		protected final BundleResolution bundleRes;
		protected final HashMap<ModuleFullName, Location> modules;
		protected final HashSet<Location> moduleFiles;
		
		public BundleFilesChecker(BundleResolution bundleRes) {
			modules = new HashMap<>(bundleRes.getBundleModulesMap());
			moduleFiles = new HashSet<>(bundleRes.getBundleModuleFiles());
			this.bundleRes = bundleRes;
		}
		
		protected void checkEntry(String moduleFullName, String relFilePath) {
			checkEntry(moduleFullName, relFilePath, false);
		}
		
		protected void checkEntry(String moduleFullName, String relFilePath, boolean duplicateNameEntry) {
			Location filePath = loc(bundleRes.getBundlePath(), relFilePath);
			ModuleFullName key = new ModuleFullName(moduleFullName);
			assertAreEqual(modules.get(key), filePath);
			
			if(!duplicateNameEntry) {
				assertTrue(moduleFiles.contains(findResolvedModule(filePath).getModulePath()));
			}
			
			assertTrue(moduleFiles.remove(filePath) == !duplicateNameEntry);
			modules.remove(key);
		}
		
		public ResolvedModule findResolvedModule(Location filePath) {
			try {
				return bundleRes.findResolvedModule(filePath);
			} catch (ModuleSourceException e) {
				throw melnorme.utilbox.core.ExceptionAdapter.unchecked(e);
			}
		}
		
		protected void finalCheck() {
			assertTrue(modules.isEmpty());
			assertTrue(moduleFiles.isEmpty());
		}
		
	}
	
	public static final Location DEFAULT_DMD_INSTALL_LOCATION__StdStdio_Path = 
			DEFAULT_DMD_INSTALL_BaseLocation.resolve_fromValid("src/phobos/std/stdio.d");
	public static final Location DEFAULT_DMD_INSTALL_LOCATION__Object_Path = 
			DEFAULT_DMD_INSTALL_BaseLocation.resolve_fromValid("src/druntime/import/object.di");
	
	@Test
	public void testModuleResolving() throws Exception { testModuleResolving$(); }
	public void testModuleResolving$() throws Exception {
		
		___initSemanticManager();
		
		BundleResolution sr = sm.getUpdatedResolution(BASIC_LIB);
		new BundleFilesChecker(sr) {
			{
				checkEntry("basic_lib_foo", "source/basic_lib_foo.d");
				checkEntry("basic_lib_pack.foo", "source/basic_lib_pack/foo.d");
				finalCheck();
			}
		};
		
		sm.getUpdatedResolution(COMPLEX_BUNDLE); // Tests optimization, run describe only once.
		
		BundleResolution smtestSR = sm.getUpdatedResolution(SMTEST);
		new BundleFilesChecker(smtestSR) {
			{
				checkEntry("sm_test_foo", "src/sm_test_foo.d");
				checkEntry("pack.import_pack_test", "src/pack/import_pack_test/package.d"); // Test package rule
				checkEntry("pack.import_pack_test.foo", "src/pack/import_pack_test/foo.d");
				
				checkEntry("test.fooLib", "src2/test/fooLib.d");
				
				checkEntry("modA_import_only", "src-import/modA_import_only.d");
				checkEntry("nested.mod_nested_import_only", "src-import/nested/mod_nested_import_only.d");	
				checkEntry("mod_nested_import_only", "src-import/nested/mod_nested_import_only.d", true);
				finalCheck();
			}
		};
		
		// Test Module resolver
		
		testFindResolvedModule(SMTEST, "sm_test_foo", loc(SMTEST, "src/sm_test_foo.d"));
		testFindResolvedModule(SMTEST, "non_existing", (Location) null);
		
		assertEqualSet(smtestSR.findModules("test."), hashSet(
			"test.fooLib"
		));
		
		// Test dependency bundles module resolution
		testFindResolvedModule(SMTEST, "basic_lib_foo", loc(BASIC_LIB, "source/basic_lib_foo.d"));
		
		assertEqualSet(smtestSR.findModules("basic_lib"), hashSet(
			"basic_lib_pack.foo",
			"basic_lib_foo"
		));
		
		BundleResolution complexLibSR = sm.getUpdatedResolution(COMPLEX_LIB);
		assertEqualSet(complexLibSR.findModules("b"), hashSet(
			"basic_lib_pack.foo",
			"basic_lib_foo",
			"basic_lib2_pack.bar",
			"basic_lib2_foo"
		));
		
		BundleResolution complexBundleSR = sm.getUpdatedResolution(COMPLEX_BUNDLE);
		assertEqualSet(complexBundleSR.findModules("basic_lib_pack"), hashSet(
			"basic_lib_pack.foo"
		));
		testFindResolvedModule(COMPLEX_BUNDLE, "basic_lib_foo", loc(BASIC_LIB, "source/basic_lib_foo.d"));
		
	}
	
	@Test
	public void testStdLibResolve() throws Exception { testStdLibResolve$(); }
	public void testStdLibResolve$() throws Exception {
		___initSemanticManager();
		BundleResolution sr = sm.getUpdatedResolution(BASIC_LIB);
		assertTrue(sr.stdLibResolution.getCompilerType() == ECompilerType.DMD);
		assertTrue(sr.stdLibResolution.getLibrarySourceFolders().get(0).path.startsWith(
			DEFAULT_DMD_INSTALL_BaseLocation.path));
		
		testFindResolvedModule(BASIC_LIB, "object", DEFAULT_DMD_INSTALL_LOCATION__Object_Path);
		testFindResolvedModule(BASIC_LIB, "std.stdio", DEFAULT_DMD_INSTALL_LOCATION__StdStdio_Path);
		
		
		// Test when no StdLib install is found
		___initSemanticManager(new Tests_DToolServer().getSemanticManager());
		ResolutionKey BASIC_LIB_NullCompilerInstall = resolutionKey(BASIC_LIB, 
			MissingStandardLibraryResolution.NULL_COMPILER_INSTALL);
		
		sr = sm.getUpdatedResolution(BASIC_LIB_NullCompilerInstall);
		StandardLibraryResolution fallBackStdLibResolution = sr.stdLibResolution;
		assertTrue(fallBackStdLibResolution.getLibrarySourceFolders().size() == 0);
		assertTrue(fallBackStdLibResolution.checkIsModuleContentsStale() == false);
		assertTrue(fallBackStdLibResolution.checkIsModuleListStale() == false);
		
		ResolvedModule objectModule = testFindResolvedModule(sr, "object", null);
		assertAreEqual(objectModule.getModuleNode().compilationUnitPath, null); 
		
		assertTrue(sr == sm.getStoredResolution(BASIC_LIB_NullCompilerInstall));
		
		assertEqualSet(sr.findModules(""), hashSet(
			"basic_lib_pack.foo",
			"basic_lib_foo",
			"object"
		));
		
		assertEqualSet(fallBackStdLibResolution.findModules(""), hashSet(
			"object"
		));
		
	}
	
	/* -----------------  ----------------- */
	
	public final BundlePath NOT_A_BUNDLE = bundlePath(getDubRepositoryDir(), "not_a_bundle");
	
	@Test
	public void testGetResolvedModule() throws Exception { testGetResolvedModule$(); }
	public void testGetResolvedModule$() throws Exception {
		prepSMTestsWorkingDir();
		sm = ___initSemanticManager();
		sm.getUpdatedResolution(CommonSemanticManagerTest.resolutionKey(COMPLEX_LIB));
		
		ResolvedModule rm = getUpdatedResolvedModule(BASIC_LIB_FOO_MODULE);
		assertTrue(rm.semanticContext == sm.getStoredResolution(resKey(BASIC_LIB)));
		
		BundleResolution complexLibSR = sm.getUpdatedResolution(COMPLEX_LIB);
		assertTrue(rm == complexLibSR.findResolvedModule(new ModuleFullName(BASIC_LIB_FOO_MODULE_Name)));
		assertTrue(complexLibSR.getCompilerPath().equals(DEFAULT_DMD_INSTALL_EXE_PATH));
		
		
		// Test getResolvedModule for module that is not in bundle import folders.
		Location NOT_IN_SOURCE__MODULE = loc(BASIC_LIB, "not_source/not_source_foo.d");
		rm = getUpdatedResolvedModule(NOT_IN_SOURCE__MODULE);
		assertTrue(rm.semanticContext != sm.getStoredResolution(resKey(BASIC_LIB)));
		assertEqualSet(rm.semanticContext.getBundleModuleFiles(), hashSet(NOT_IN_SOURCE__MODULE));
		assertEqualSet(rm.semanticContext.findModules("o"), hashSet("object"));
		assertEqualSet(rm.semanticContext.findModules("basic_lib"), hashSet()); // Test not find basic lib files
		assertEqualSet(rm.semanticContext.findModules("not"), hashSet("not_source_foo")); // Test find self
		
		
		// Test getResolvedModule for module that is not in a bundle at all.
		rm = getUpdatedResolvedModule(loc(NOT_A_BUNDLE, "not_a_bundle_foo.d"));
		assertEqualSet(rm.semanticContext.findModules("o"), hashSet("object"));
		assertEqualSet(rm.semanticContext.findModules("not"), hashSet("not_a_bundle_foo"));
		testFindResolvedModule(rm.semanticContext, "object", DEFAULT_DMD_INSTALL_LOCATION__Object_Path);
		
		
		// Test getResolvedModule for module in StandardLibrary
		rm = getUpdatedResolvedModule(DEFAULT_DMD_INSTALL_LOCATION__Object_Path);
		assertEqualSet(rm.semanticContext.findModules("o"), hashSet("object"));
		assertTrue(rm.semanticContext instanceof StandardLibraryResolution);
		
		
		// Test getResolvedModule for missing file - must throw
		try {
			rm = getUpdatedResolvedModule(loc(NOT_A_BUNDLE, "_does_not_exist.d"));
			assertFail();
		} catch (CommonException e) {
		}
		
		boolean SUPPORT_RELATIVE_PATHS = false; // Disabled because relative paths are no long supported.
		if(SUPPORT_RELATIVE_PATHS) {
			// Test getResolvedModule for a relative path.
			Location specialPath = loc(PathUtil.createValidPath(("###special/relative_bundle.d")));
			sm.parseCache.parseModuleWithNewSource(specialPath.path, "module relative_bundle;");
			rm = getUpdatedResolvedModule(specialPath);
			assertEqualSet(rm.semanticContext.findModules("o"), hashSet(
				"object"
			));
			testFindResolvedModule(rm.semanticContext, "object", DEFAULT_DMD_INSTALL_LOCATION__Object_Path);
			// Test same, when no parse source exists for that relative path
			try {
				rm = getUpdatedResolvedModule(loc(PathUtil.createValidPath(("###special/non_existent.d"))));
				assertFail();
			} catch (CommonException ce) {
				assertTrue(ce.getCause() instanceof ModuleSourceException);
			}
		}
	}
	
}