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

import static dtool.engine.StandardLibraryResolution.NULL_COMPILER_INSTALL_PATH;
import static dtool.tests.MockCompilerInstalls.DEFAULT_DMD_INSTALL_LOCATION;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;

import org.junit.Test;

import dtool.engine.StandardLibraryResolution.MissingStandardLibraryResolution;
import dtool.engine.compiler_installs.CompilerInstall;
import dtool.engine.compiler_installs.CompilerInstall.ECompilerType;
import dtool.engine.modules.ModuleFullName;

public class BundleResolution_ModuleListTest extends CommonSemanticManagerTest {
	
	public static class BundleFilesChecker {
		
		protected final BundleResolution bundleRes;
		protected final HashMap<ModuleFullName, Path> modules;
		protected final HashSet<Path> moduleFiles;
		
		public BundleFilesChecker(BundleResolution bundleRes) {
			modules = new HashMap<>(bundleRes.bundleModules.modules);
			moduleFiles = new HashSet<>(bundleRes.getBundleModuleFiles());
			this.bundleRes = bundleRes;
		}
		
		protected void checkEntry(String moduleFullName, String relFilePath) {
			checkEntry(moduleFullName, relFilePath, false);
		}
		
		protected void checkEntry(String moduleFullName, String relFilePath, boolean duplicateEntry) {
			Path filePath = bundleRes.getBundlePath().resolve(relFilePath);
			checkMapContains(modules, new ModuleFullName(moduleFullName), filePath);
			assertTrue(moduleFiles.remove(filePath) == !duplicateEntry);
		}
		
		protected void finalCheck() {
			assertTrue(modules.isEmpty());
			assertTrue(moduleFiles.isEmpty());
		}
		
	}
	
	public static final Path DEFAULT_DMD_INSTALL_LOCATION__StdStdio_Path = 
			DEFAULT_DMD_INSTALL_LOCATION.resolve("src/phobos/std/stdio.d");
	public static final Path DEFAULT_DMD_INSTALL_LOCATION__Object_Path = 
			DEFAULT_DMD_INSTALL_LOCATION.resolve("src/druntime/import/object.di");
	
	@Test
	public void testModuleResolving() throws Exception { testModuleResolving$(); }
	public void testModuleResolving$() throws Exception {
		
		___initSemanticManager();
		
		BundleResolution sr = sm.getUpdatedResolution(BASIC_LIB);
		assertEquals(sr.getBundleName(), "basic_lib");
		new BundleFilesChecker(sr) {
			{
				checkEntry("basic_lib_foo", "source/basic_lib_foo.d");
				checkEntry("basic_lib_pack.foo", "source/basic_lib_pack/foo.d");
				finalCheck();
			}
		};
		
		sm.getUpdatedResolution(COMPLEX_BUNDLE); // Tests optimization, run describe only once.
		
		BundleResolution smtestSR = sm.getUpdatedResolution(SMTEST);
		assertEquals(smtestSR.getBundleName(), "smtest_foo");
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
		
		testFindResolvedModule(SMTEST, "sm_test_foo", SMTEST.resolve("src/sm_test_foo.d"));
		testFindResolvedModule(SMTEST, "non_existing", null);
		
		assertEqualSet(smtestSR.findModules("test."), hashSet(
			"test.fooLib"
		));
		
		// Test dependency bundles module resolution
		testFindResolvedModule(SMTEST, "basic_lib_foo", BASIC_LIB.resolve("source/basic_lib_foo.d"));
		
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
		testFindResolvedModule(COMPLEX_BUNDLE, "basic_lib_foo", BASIC_LIB.resolve("source/basic_lib_foo.d"));
		
	}
	
	@Test
	public void testStdLibResolve() throws Exception { testStdLibResolve$(); }
	public void testStdLibResolve$() throws Exception {
		___initSemanticManager();
		BundleResolution sr = sm.getUpdatedResolution(BASIC_LIB);
		assertTrue(sr.stdLibResolution.getCompilerType() == ECompilerType.DMD);
		assertTrue(sr.stdLibResolution.getLibrarySourceFolders().get(0).startsWith(DEFAULT_DMD_INSTALL_LOCATION));
		
		testFindResolvedModule(BASIC_LIB, "object", DEFAULT_DMD_INSTALL_LOCATION__Object_Path);
		testFindResolvedModule(BASIC_LIB, "std.stdio", DEFAULT_DMD_INSTALL_LOCATION__StdStdio_Path);
		
		
		// Test when no StdLib install is found
		___initSemanticManager(new Tests_SemanticManager() {
			@Override
			protected StandardLibraryResolution getUpdatedStdLibResolution(Path compilerPath) {
				return assertCast(super.getUpdatedStdLibResolution(compilerPath), 
					MissingStandardLibraryResolution.class);
			}
			
			@Override
			protected CompilerInstall getCompilerInstallForNewResolution(Path compilerPath) {
				return MissingStandardLibraryResolution.NULL_COMPILER_INSTALL;
			}
		});
		sr = sm.getUpdatedResolution(BASIC_LIB);
		StandardLibraryResolution fallBackStdLibResolution = sr.stdLibResolution;
		assertTrue(fallBackStdLibResolution.getLibrarySourceFolders().size() == 0);
		assertTrue(fallBackStdLibResolution.checkIsModuleContentsStale() == false);
		assertTrue(fallBackStdLibResolution.checkIsModuleListStale() == false);
		
		testFindResolvedModule(BASIC_LIB, "object", NULL_COMPILER_INSTALL_PATH.resolve("object.di"));
		
		assertEqualSet(sr.findModules(""), hashSet(
			"basic_lib_pack.foo",
			"basic_lib_foo",
			"object"
		));
		
		StandardLibraryResolution fallbackStdLibResolution = sr.stdLibResolution;
		assertEqualSet(fallbackStdLibResolution.findModules(""), hashSet(
			"object"
		));
		
	}
	
}