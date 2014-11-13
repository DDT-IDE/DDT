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

import static dtool.engine.CommonSemanticManagerTest.StaleState.CURRENT;
import static dtool.engine.CommonSemanticManagerTest.StaleState.MANIFEST_STALE;
import static dtool.engine.CommonSemanticManagerTest.StaleState.MODULES_STALE;
import static dtool.engine.CommonSemanticManagerTest.StaleState.MODULE_CONTENTS_STALE;
import static dtool.engine.CommonSemanticManagerTest.StaleState.MODULE_LIST_STALE;
import static dtool.engine.CommonSemanticManagerTest.StaleState.NO_BUNDLE_RESOLUTION;
import static dtool.tests.MockCompilerInstalls.DEFAULT_DMD_INSTALL_EXE_PATH;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.ExecutionException;

import melnorme.lang.utils.MiscFileUtils;
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

public class CommonSemanticManagerTest extends CommonDToolTest {
	
	public static final Path SEMMODEL_TEST_BUNDLES = DToolTestResources.getTestResourcePath("semanticModel");
	
	@BeforeClass
	public static void initDubRepositoriesPath() {
		CommonDubTest.dubRemovePath(WORKING_DIR); // workaround to cleanup abruptly-terminated tests
		CommonDubTest.dubAddPath(SEMMODEL_TEST_BUNDLES);
	}
	
	@AfterClass
	public static void cleanupDubRepositoriesPath() {
		CommonDubTest.dubRemovePath(SEMMODEL_TEST_BUNDLES);
	}
	
	public Path getDubRepositoryDir() {
		return SEMMODEL_TEST_BUNDLES;
	}
	
	public static BundlePath bundlePath(Path basePath, String other) {
		return BundlePath.create(basePath.resolve(other));
	}
	
	public final BundlePath BASIC_LIB = bundlePath(getDubRepositoryDir(), "basic_lib");
	public final BundlePath SMTEST = bundlePath(getDubRepositoryDir(), "smtest_foo");
	public final BundlePath BASIC_LIB2 = bundlePath(getDubRepositoryDir(), "basic_lib2");
	public final BundlePath COMPLEX_LIB = bundlePath(getDubRepositoryDir(), "complex_lib");
	public final BundlePath COMPLEX_BUNDLE = bundlePath(getDubRepositoryDir(), "complex_bundle");
	
	/* ----------------- working dir setup ----------------- */
	
	public static final Path WORKING_DIR = TestsWorkingDir.getWorkingDir().toPath().resolve("SemModel");
	
	public static void prepSMTestsWorkingDir() throws IOException {
		FileUtil.deleteDirContents(WORKING_DIR);
		MiscFileUtils.copyDirContentsIntoDirectory(SEMMODEL_TEST_BUNDLES, WORKING_DIR);
	}
	
	/* -----------------  ----------------- */
	
	public static class Tests_DToolServer extends DToolServer {
		
		public Tests_DToolServer() {
		}
		
		@Override
		protected SemanticManager createSemanticManager() {
			 // We use this to enable the instrumented compiler install location
			return new Tests_SemanticManager(this);
		}
		
		@Override
		public void logError(String message, Throwable throwable) {
			assertFail();
		}
	}
	
	public static class Tests_SemanticManager extends SemanticManager {
		
		public Tests_SemanticManager(DToolServer dtoolServer) {
			super(dtoolServer);
		}
		
		public Tests_SemanticManager() {
			super(new Tests_DToolServer());
		}
		
		@Override
		public ResolvedManifest getUpdatedManifest(BundlePath bundlePath) throws ExecutionException {
			ResolvedManifest manifest = super.getUpdatedManifest(bundlePath);
			assertTrue(checkIsManifestStale(bundlePath) == false);
			return manifest;
		}
		
		@Override
		public BundleResolution getUpdatedResolution(BundlePath bundlePath) throws ExecutionException {
			boolean manifestStale = checkIsManifestStale(bundlePath);
			ResolvedManifest previousManifest = getStoredManifest(bundlePath);
			
			BundleResolution bundleResolution = super.getUpdatedResolution(bundlePath);
			assertEquals(bundleResolution.bundlePath, bundlePath);
			
			assertEquals(bundleResolution.manifest == previousManifest, !manifestStale);
			
			assertTrue(checkIsManifestStale(bundlePath) == false);
			assertTrue(checkIsResolutionStale(bundlePath) == false);
			
			// test caching
			assertTrue(bundleResolution == super.getUpdatedResolution(bundlePath));
			
			return bundleResolution;
		}
		
		public void checkStaleStatus(BundlePath bundlePath, StaleState staleState) {

			assertEquals(getInfo(bundlePath).manifestEntry.isStale(), 
				staleState == MANIFEST_STALE);
			
			BundleResolution storedResolution = getStoredResolution(bundlePath);
			
			if(storedResolution == null) {
				assertTrue(staleState == MANIFEST_STALE || staleState == NO_BUNDLE_RESOLUTION);
			} else {
				assertEquals(storedResolution.checkIsModuleListStale(), 
					staleState == MODULES_STALE || staleState == MODULE_LIST_STALE);
				assertEquals(storedResolution.checkIsModuleContentsStale(), 
					staleState == MODULES_STALE || staleState == MODULE_CONTENTS_STALE);
			}
			
			assertEquals(checkIsResolutionStale(bundlePath), staleState != CURRENT);
		}
		
		@Override
		protected StandardLibraryResolution getUpdatedStdLibResolution(Path compilerPath) {
			if(compilerPath == null) {
				compilerPath = DEFAULT_DMD_INSTALL_EXE_PATH;
			}
			StandardLibraryResolution stdLibRes = super.getUpdatedStdLibResolution(compilerPath);
			
			// Test caching of resolution
			assertAreEqual(stdLibRes.compilerInstall, super.getUpdatedStdLibResolution(compilerPath).compilerInstall);
			assertTrue(stdLibRes == super.getUpdatedStdLibResolution(compilerPath));
			
			assertTrue(stdLibRes.checkIsModuleListStale() == false);
			assertTrue(stdLibRes.checkIsModuleContentsStale() == false);
			
			return stdLibRes;
		}
		
	}
	
	protected Tests_SemanticManager sm;
	
	protected Tests_SemanticManager ___initSemanticManager() throws IOException {
		return ___initSemanticManager(new Tests_SemanticManager());
	}
	
	protected Tests_SemanticManager ___initSemanticManager(Tests_SemanticManager tests_SemanticManager) {
		if(sm != null) {
			sm.shutdown();
		}
		return sm = tests_SemanticManager;
	}
	
	@After
	public void cleanSemanticManager() {
		sm.shutdown();
	}
	
	public enum StaleState { CURRENT, MANIFEST_STALE, 
		MODULE_LIST_STALE, MODULE_CONTENTS_STALE, MODULES_STALE, NO_BUNDLE_RESOLUTION,
		DEP_STALE }
	
	protected void checkStaleStatus(BundlePath bundlePath, StaleState staleState) {
		sm.checkStaleStatus(bundlePath, staleState);
	}
	
	protected BundleResolution getUpdatedResolution(BundlePath bundlePath) throws ExecutionException {
		assertTrue(sm.checkIsResolutionStale(bundlePath));
		return sm.getUpdatedResolution(bundlePath);
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
	
	protected void testFindResolvedModule(BundlePath bundlePath, String moduleNameStr, Path expectedPath) 
			throws ParseSourceException, ExecutionException {
		BundleResolution bundleRes = sm.getStoredResolution(bundlePath);
		testFindResolvedModule(bundleRes, moduleNameStr, expectedPath);
	}
	
	protected void testFindResolvedModule(AbstractBundleResolution bundleRes, String moduleNameStr, Path expectedPath)
			throws ParseSourceException {
		ModuleFullName moduleFullName = new ModuleFullName(moduleNameStr);
		ResolvedModule resolvedModule = bundleRes.findResolvedModule(moduleFullName);
		Path modulePath = resolvedModule == null ? null : resolvedModule.getModulePath();
		assertAreEqual(modulePath, expectedPath);
		
		if(expectedPath != null) {
			assertEquals(bundleRes.findModule(moduleFullName), resolvedModule.getModuleNode());
		}
	}
	
}