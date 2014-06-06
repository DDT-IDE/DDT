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

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.nio.file.Path;
import java.util.Map;

import melnorme.utilbox.misc.MiscUtil;

import org.junit.Test;

import dtool.dub.BundlePath;
import dtool.model.SemanticManager.SemanticResolution;

public class SemanticManager_Test extends CommonSemanticModelTest {
	
	protected SemanticManager mgr;
	
	@Test
	public void testBasic() throws Exception { testBasic$(); }
	public void testBasic$() throws Exception {
		
		mgr = new SemanticManager(new Tests_DToolServer());
		
		SemanticContext sr = mgr.getSemanticResolution(BASIC_LIB);
		assertEquals(sr.getBundleId(), "basic_lib");
		new BundleFilesChecker(sr.getBundleModuleFiles()) {
			{
				checkEntry("basic_lib_foo", "source/basic_lib_foo.d");
				checkEntry("pack.basicFoo", "source/pack/basicFoo.d");			
			}
		}.run();
		
		SemanticContext smtestSR = mgr.getSemanticResolution(SMTEST);
		assertEquals(smtestSR.getBundleId(), "smtest_foo");
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
	
	protected static final Path BASIC_LIB_JSON = BASIC_LIB.path.resolve("dub.json");
	
	protected SemanticResolution smtestSR;
	protected SemanticResolution basicLibSR;
	protected SemanticResolution basicLib2SR;
	
	protected void getTestSemanticResolutions() {
		smtestSR = testGetSemanticResolution(SMTEST);
		basicLibSR = testGetSemanticResolution(BASIC_LIB);
		basicLib2SR = testGetSemanticResolution(BASIC_LIB2);
	}
	
	@Test
	public void testCaching() throws Exception { testCaching$(); }
	public void testCaching$() throws Exception {
		mgr = new SemanticManager(new Tests_DToolServer());
		
		getTestSemanticResolutions();
		
		// Test json modify
		mgr.notifyManifestFileChanged(BASIC_LIB2);
		assertChanged(basicLibSR, BASIC_LIB, false);
		assertChanged(basicLib2SR, BASIC_LIB2, true);
		
		
		// Test json modify - a dependee is affected
		getTestSemanticResolutions();
		mgr.notifyManifestFileChanged(BASIC_LIB);
		checkIsStale(BASIC_LIB, true, true);
		checkIsStale(BASIC_LIB2, false);
		checkIsStale(SMTEST, true); // Stale because it's a dep.
		
		testGetSemanticResolution(BASIC_LIB);
		checkIsStale(BASIC_LIB, false);
		checkIsStale(SMTEST, true); // Still stale because own copy of BASIC_LIB SR is stale
		
		// TODO: test cu modify
		
		// Test module add
//		writeStringToFileUnchecked(BASIC_LIB.path.resolve("source/a_new_module.d").toFile(), "module a_new_module;");
		
	}
	
	protected void assertChanged(SemanticResolution previousSR, BundlePath bundlePath, boolean changed) {
		assertChanged(previousSR, bundlePath, changed, changed);
	}
	
	protected void assertChanged(SemanticResolution previousSR, BundlePath bundlePath, boolean changed,
			boolean manifestChanged) {
		checkIsStale(bundlePath, changed, manifestChanged);
		SemanticResolution testGetSemanticResolution = testGetSemanticResolution(bundlePath);
		assertEquals(previousSR != testGetSemanticResolution, changed);
	}
	
	protected void checkManifestIsStale(BundlePath bundlePath, boolean changed) {
		assertTrue(mgr.getEntry(bundlePath).checkIsStale() == changed);
		assertTrue(mgr.getBundleManifestCache().getEntry(bundlePath).isInternallyStale() == changed);
	}
	protected void checkIsStale(BundlePath bundlePath, boolean changed) {
		checkIsStale(bundlePath, changed, false);
	}
	
	protected void checkIsStale(BundlePath bundlePath, boolean changed, boolean manifestChanged) {
		assertTrue(mgr.getEntry(bundlePath).checkIsStale() == changed);
		assertTrue(mgr.getBundleManifestCache().getEntry(bundlePath).isInternallyStale() == manifestChanged);
	}
	
	protected SemanticResolution testGetSemanticResolution(BundlePath bundlePath) {
		SemanticResolution sr = mgr.getSemanticResolution(bundlePath);
		assertTrue(mgr.getEntry(bundlePath).checkIsStale() == false);
		
		// Test instance is the same.
		assertTrue(sr == mgr.getSemanticResolution(bundlePath));
		return sr;
	}
	
}