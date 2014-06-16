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
package dtool.model;

import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import melnorme.utilbox.misc.MiscUtil;

import org.junit.Test;

import dtool.dub.BundlePath;
import dtool.model.ModuleParseCache.ParseSourceException;
import dtool.parser.DeeParserResult.ParsedModule;

public class SemanticManager_ModulesTest extends CommonSemanticModelTest {
	
	public static class BundleFilesChecker extends MapChecker<ModuleFullName, Path> {
		
		public BundleFilesChecker(Map<ModuleFullName, Path> map) {
			super(map);
		}
		
		protected void checkEntry(final String moduleFullName, final String filePath) {
			entryChecks.add(new MapEntryChecker() {
				@Override
				public void run() {
					Path value = getExpectedEntry(new ModuleFullName(moduleFullName));
					assertAreEqual(value, MiscUtil.createPathOrNull(filePath));
				};
			});
		}
		
	}
	
	@Test
	public void testModuleResolving() throws Exception { testModuleResolving$(); }
	public void testModuleResolving$() throws Exception {
		
		sm = new SemanticManager(new Tests_DToolServer());
		sm.getUpdatedResolution(COMPLEX_BUNDLE); // Tests optimization, run describe only once.
		
		BundleSemanticResolution sr = sm.getUpdatedResolution(BASIC_LIB);
		assertEquals(sr.getBundleName(), "basic_lib");
		new BundleFilesChecker(sr.getBundleModuleFiles()) {
			{
				checkEntry("basic_lib_foo", "source/basic_lib_foo.d");
				checkEntry("basic_lib_pack.foo", "source/basic_lib_pack/foo.d");			
			}
		}.run();
		
		BundleSemanticResolution smtestSR = sm.getUpdatedResolution(SMTEST);
		assertEquals(smtestSR.getBundleName(), "smtest_foo");
		new BundleFilesChecker(smtestSR.getBundleModuleFiles()) {
			{
				checkEntry("sm_test_foo", "src/sm_test_foo.d");
				checkEntry("test.fooLib", "src2/test/fooLib.d");			
				checkEntry("modA_import_only", "src-import/modA_import_only.d");
				checkEntry("nested.mod_nested_import_only", "src-import/nested/mod_nested_import_only.d");	
				checkEntry("mod_nested_import_only", "src-import/nested/mod_nested_import_only.d");			
			}
		}.run();
		
		// Test Module resolver
		
		testFindModule(SMTEST, "sm_test_foo", SMTEST.resolve("src/sm_test_foo.d"));
		testFindModule(SMTEST, "non_existing", null);
		
		assertEqualSet(smtestSR.findModules2("test."), hashSet(
			"test.fooLib"
		));
		
		// Test dependency bundles module resolution
		testFindModule(SMTEST, "basic_lib_foo", BASIC_LIB.resolve("source/basic_lib_foo.d"));
		
		assertEqualSet(smtestSR.findModules2("basic_lib"), hashSet(
			"basic_lib_pack.foo",
			"basic_lib_foo"
		));
		
		BundleSemanticResolution complexLibSR = sm.getUpdatedResolution(COMPLEX_LIB);
		assertEqualSet(complexLibSR.findModules2(""), hashSet(
			"complex_lib",
			"basic_lib_pack.foo",
			"basic_lib_foo",
			"basic_lib2_pack.bar",
			"basic_lib2_foo"
		));
		
		BundleSemanticResolution complexBundleSR = sm.getUpdatedResolution(COMPLEX_BUNDLE);
		assertEqualSet(complexBundleSR.findModules2("basic_lib_pack"), hashSet(
			"basic_lib_pack.foo"
		));
		testFindModule(COMPLEX_BUNDLE, "basic_lib_foo", BASIC_LIB.resolve("source/basic_lib_foo.d"));
	}
	
	protected void testFindModule(BundlePath bundlePath, String bundleFullName, Path expectedPath) 
			throws ParseSourceException, ExecutionException {
		BundleSemanticResolution bundleSR = sm.getStoredResolution(bundlePath);
		ParsedModule parsedModule = bundleSR.getParsedModule(bundleFullName);
		Path modulePath = parsedModule == null ? null : parsedModule.modulePath;
		assertAreEqual(modulePath, expectedPath);
	}
	
}