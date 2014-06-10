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

import melnorme.utilbox.misc.MiscUtil;

import org.junit.Test;

public class SemanticManager_ModulesTest extends CommonSemanticModelTest {
	
	protected SemanticManager mgr;
	
	@Test
	public void testModuleResolving() throws Exception { testModuleResolving$(); }
	public void testModuleResolving$() throws Exception {
		
		mgr = new SemanticManager(new Tests_DToolServer());
		
		BundleSemanticResolution sr = mgr.getUpdatedResolution(BASIC_LIB);
		assertEquals(sr.getBundleName(), "basic_lib");
		new BundleFilesChecker(sr.getBundleModuleFiles()) {
			{
				checkEntry("basic_lib_foo", "source/basic_lib_foo.d");
				checkEntry("pack.basicFoo", "source/pack/basicFoo.d");			
			}
		}.run();
		
		BundleSemanticResolution smtestSR = mgr.getUpdatedResolution(SMTEST);
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
		
		assertEqualArrays(smtestSR.findModules("test."), 
			array("test.fooLib"));
		
		assertEquals(smtestSR.getParsedModule("sm_test_foo").modulePath, 
			SMTEST.path.resolve("src/sm_test_foo.d"));
		
		assertAreEqual(smtestSR.getParsedModule("non_existing"), null); 
		
		// Test dependency bundles module resolution
		assertEqualArrays(smtestSR.findModules("basic_lib"), 
			array("basic_lib_foo"));
		assertEquals(smtestSR.getParsedModule("basic_lib_foo").modulePath, 
			BASIC_LIB.path.resolve("source/basic_lib_foo.d"));
		
	}
	
	public static class BundleFilesChecker extends MapChecker<ModuleFullName, Path> {
		
		public BundleFilesChecker(Map<ModuleFullName, Path> map) {
			super(map);
		}
		
		protected void checkEntry(final String moduleFullName, final String filePath) {
			entryChecks.add(new MapEntryChecker() {
				@Override
				public void run() {
					Path value = getExpectedEntry(new ModuleFullName(moduleFullName));
					assertAreEqual(value, MiscUtil.createValidPath(filePath));
				};
			});
		}
		
	}
	
}