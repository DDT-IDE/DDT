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

import static dtool.engine.CommonSemanticModelTest.StaleState.CURRENT;
import static dtool.engine.CommonSemanticModelTest.StaleState.MANIFEST_STALE;
import static dtool.engine.CommonSemanticModelTest.StaleState.MODULES_STALE;
import static dtool.engine.CommonSemanticModelTest.StaleState.MODULE_CONTENTS_STALE;
import static dtool.engine.CommonSemanticModelTest.StaleState.MODULE_LIST_STALE;
import static dtool.engine.CommonSemanticModelTest.StaleState.NO_BUNDLE_RESOLUTION;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.ExecutionException;

import melnorme.utilbox.misc.FileUtil;
import melnorme.utilbox.tests.TestsWorkingDir;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import dtool.dub.BundlePath;
import dtool.dub.CommonDubTest;
import dtool.dub.ResolvedManifest;
import dtool.engine.AbstractBundleResolution.ResolvedModule;
import dtool.engine.ModuleParseCache.ParseSourceException;
import dtool.engine.modules.ModuleFullName;
import dtool.tests.CommonDToolTest;
import dtool.tests.DToolTestResources;
import dtool.tests.utils.MiscFileUtils;

public class CommonSemanticModelTest extends CommonDToolTest {
	
	public static class Tests_DToolServer extends DToolServer {
		
		public Tests_DToolServer() {
		}
		
		@Override
		protected void logError(String message, Throwable throwable) {
			assertFail();
		}
	}
	
	public static final Path SEMMODEL_TEST_BUNDLES = DToolTestResources.getTestResourcePath("semanticModel");
	
	@BeforeClass
	public static void initDubRepositoriesPath() {
		CommonDubTest.dubRemovePath(WORKING_DIR); // workaround to remove terminated tests
		CommonDubTest.dubAddPath(SEMMODEL_TEST_BUNDLES);
	}
	
	@AfterClass
	public static void cleanupDubRepositoriesPath() {
		CommonDubTest.dubRemovePath(SEMMODEL_TEST_BUNDLES);
	}
	
	public Path getDubRepositoryDir() {
		return SEMMODEL_TEST_BUNDLES;
	}
	
	public final BundlePath BASIC_LIB = new BundlePath(getDubRepositoryDir().resolve("basic_lib"));
	public final BundlePath SMTEST = new BundlePath(getDubRepositoryDir().resolve("smtest_foo"));
	public final BundlePath BASIC_LIB2 = new BundlePath(getDubRepositoryDir().resolve("basic_lib2"));
	public final BundlePath COMPLEX_LIB = new BundlePath(getDubRepositoryDir().resolve("complex_lib"));
	public final BundlePath COMPLEX_BUNDLE = new BundlePath(getDubRepositoryDir().resolve("complex_bundle"));
	
	/* ----------------- working dir setup ----------------- */
	
	public static final Path WORKING_DIR = TestsWorkingDir.getWorkingDir().toPath().resolve("SemModel");
	
	public static void prepSMTestsWorkingDir() throws IOException {
		FileUtil.deleteDirContents(WORKING_DIR);
		MiscFileUtils.copyDirContentsIntoDirectory(SEMMODEL_TEST_BUNDLES, WORKING_DIR);
	}
	
	/* -----------------  ----------------- */
	
	protected SemanticManager sm;
	
	protected void __initSemanticManager() throws IOException {
		sm = new SemanticManager(new Tests_DToolServer());
	}
	
	@After
	public void cleanSemanticManager() {
		sm.shutdown();
	}
	
	public enum StaleState { CURRENT, MANIFEST_STALE, 
		MODULE_LIST_STALE, MODULE_CONTENTS_STALE, MODULES_STALE, NO_BUNDLE_RESOLUTION,
		DEP_STALE }
	
	protected void checkStaleStatus(BundlePath bundlePath, StaleState staleState) {

		assertEquals(sm.getInfo(bundlePath).manifestEntry.isStale(), 
			staleState == MANIFEST_STALE);
		
		BundleResolution storedResolution = sm.getStoredResolution(bundlePath);
		
		if(storedResolution == null) {
			assertTrue(staleState == MANIFEST_STALE || staleState == NO_BUNDLE_RESOLUTION);
		} else {
			assertEquals(storedResolution.checkIsModuleListStale(), 
				staleState == MODULES_STALE || staleState == MODULE_LIST_STALE);
			assertEquals(storedResolution.checkIsModuleContentsStale(), 
				staleState == MODULES_STALE || staleState == MODULE_CONTENTS_STALE);
		}
		
		assertEquals(sm.checkIsResolutionStale(bundlePath), staleState != CURRENT);
	}
	
	protected BundleResolution getUpdatedResolution(BundlePath bundlePath) throws ExecutionException {
		assertTrue(sm.checkIsResolutionStale(bundlePath));
		
		boolean manifestStale = sm.checkIsManifestStale(bundlePath);
		
		ResolvedManifest previousManifest = sm.getStoredManifest(bundlePath);
		
		BundleResolution bundleRes = sm.getUpdatedResolution(bundlePath);
		assertEquals(bundleRes.bundlePath, bundlePath);
		assertEquals(bundleRes.manifest == previousManifest, !manifestStale);
		checkStaleStatus(bundlePath, StaleState.CURRENT);
		
		// test Caching
		assertTrue(bundleRes == sm.getUpdatedResolution(bundlePath));
		return bundleRes;
	}
	
	protected void checkGetModule(BundlePath bundlePath, String moduleName) throws ParseSourceException {
		checkGetModule(sm.getStoredResolution(bundlePath), moduleName, moduleName);
	}
	protected void checkGetModule(BundlePath bundlePath, String moduleName, 
			String expectedModuleName) throws ParseSourceException {
		checkGetModule(sm.getStoredResolution(bundlePath), moduleName, expectedModuleName);
	}
	
	protected ResolvedModule checkGetModule(BundleResolution bundleRes, String moduleName, 
			String expectedModuleName) throws ParseSourceException {
		ResolvedModule resolvedModule = bundleRes.getBundleResolvedModule(moduleName);
		
		if(expectedModuleName != null) {
			assertNotNull(resolvedModule);
			assertTrue(resolvedModule.getModuleNode().getFullyQualifiedName().equals(expectedModuleName));
			if(!sm.checkIsResolutionStale(bundleRes.bundlePath)) {
				try {
					assertTrue(resolvedModule == sm.getUpdatedResolvedModule(resolvedModule.getModulePath()) );
				} catch (ExecutionException e) {
					assertFail();
				}
			}
		} else {
			assertTrue(resolvedModule  == null);
		}
		
		return resolvedModule;
	}
	
	protected void testFindModule(BundlePath bundlePath, String moduleNameStr, Path expectedPath) 
			throws ParseSourceException, ExecutionException {
		BundleResolution bundleRes = sm.getStoredResolution(bundlePath);
		
		ModuleFullName moduleFullName = new ModuleFullName(moduleNameStr);
		ResolvedModule resolvedModule = bundleRes.findResolvedModule(moduleFullName);
		Path modulePath = resolvedModule == null ? null : resolvedModule.getModulePath();
		assertAreEqual(modulePath, expectedPath);
		
		if(expectedPath != null) {
			assertEquals(bundleRes.findModule(moduleFullName.getPackages(), moduleFullName.getBaseName()), 
				resolvedModule.getModuleNode());
		}
	}
	
	/* ----------------- misc util ----------------- */
	
	public static BundlePath createBP(Path basePath, String other) {
		return BundlePath.create(basePath.resolve(other));
	}
	
}