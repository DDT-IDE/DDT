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

import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;

import java.nio.file.Path;
import java.util.Map;

import melnorme.utilbox.misc.MiscUtil;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import dtool.dub.CommonDubTest;
import dtool.model.SemanticManager.SemanticContext;
import dtool.tests.DToolBaseTest;
import dtool.tests.DToolTestResources;

public class SemanticManager_Test extends DToolBaseTest {
	
	public static final Path SEMMODEL_TEST_BUNDLES = DToolTestResources.getTestResourcePath("semanticModel");
	
	public static final Path BASIC_BUNDLE_PATH = SEMMODEL_TEST_BUNDLES.resolve("basic_lib_foo");
	public static final Path FOO_LIB_BUNDLE_PATH = SEMMODEL_TEST_BUNDLES.resolve("sm_test_foo");
	
	
	@BeforeClass
	public static void initDubRepositoriesPath() {
		CommonDubTest.dubAddPath(SEMMODEL_TEST_BUNDLES);
	}
	@AfterClass
	public static void cleanupDubRepositoriesPath() {
		CommonDubTest.dubRemovePath(SEMMODEL_TEST_BUNDLES);
	}
	
	public static class Tests_DToolServer extends DToolServer {
		@Override
		protected void logError(String message, Throwable throwable) {
			assertFail();
		}
	}
	
	@Test
	public void testBasic() throws Exception { testBasic$(); }
	public void testBasic$() throws Exception {
		
		SemanticManager mgr = new SemanticManager(DToolServer.getProcessAgent(), new Tests_DToolServer());
		
		SemanticContext semanticContext = mgr.getSemanticContext(BASIC_BUNDLE_PATH);
		assertEquals(semanticContext.getBundleId(), "basic_lib_foo");
		new BundleFilesChecker(semanticContext.getBundleModuleFiles()) {
			{
				checkEntry("basic_lib_foo", "source/basic_lib_foo.d");
				checkEntry("pack.basicFoo", "source/pack/basicFoo.d");			
			}
		}.run();
		
		semanticContext = mgr.getSemanticContext(FOO_LIB_BUNDLE_PATH);
		assertEquals(semanticContext.getBundleId(), "sm_test_foo");
		new BundleFilesChecker(semanticContext.getBundleModuleFiles()) {
			{
				checkEntry("sm_test_foo", "src/sm_test_foo.d");
				checkEntry("test.fooLib", "src2/test/fooLib.d");			
				checkEntry("modA_import_only", "src-import/modA_import_only.d");
				checkEntry("nested.mod_nested_import_only", "src-import/nested/mod_nested_import_only.d");	
				checkEntry("mod_nested_import_only", "src-import/nested/mod_nested_import_only.d");			
			}
		}.run();
		
		
		// TODO: test caching
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