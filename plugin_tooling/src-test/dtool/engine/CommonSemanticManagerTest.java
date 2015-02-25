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
import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import melnorme.lang.tooling.context.ModuleFullName;
import melnorme.lang.tooling.context.ModuleSourceException;
import melnorme.lang.utils.MiscFileUtils;
import melnorme.utilbox.core.CommonException;
import melnorme.utilbox.misc.FileUtil;
import melnorme.utilbox.misc.Location;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import dtool.ast.definitions.Module;
import dtool.dub.BundlePath;
import dtool.dub.CommonDubTest;
import dtool.dub.ResolvedManifest;
import dtool.engine.compiler_installs.CompilerInstall;

public class CommonSemanticManagerTest extends CommonSemanticsTest {
	
	public Location getDubRepositoryDir() {
		return BUNDLEMODEL_TEST_BUNDLES;
	}
	
	public final BundlePath BASIC_LIB = bundlePath(getDubRepositoryDir(), "basic_lib");
	public final BundlePath SMTEST = bundlePath(getDubRepositoryDir(), "smtest_foo");
	public final BundlePath BASIC_LIB2 = bundlePath(getDubRepositoryDir(), "basic_lib2");
	public final BundlePath COMPLEX_LIB = bundlePath(getDubRepositoryDir(), "complex_lib");
	public final BundlePath COMPLEX_BUNDLE = bundlePath(getDubRepositoryDir(), "complex_bundle");
	
	@BeforeClass
	public static void initDubRepositoriesPath() {
		CommonDubTest.dubRemovePath(SMTEST_WORKING_DIR_BUNDLES); // cleanup stale entries
		
		CommonDubTest.dubAddPath(BUNDLEMODEL_TEST_BUNDLES);
	}
	
	@AfterClass
	public static void cleanupDubRepositoriesPath() {
		CommonDubTest.dubRemovePath(BUNDLEMODEL_TEST_BUNDLES);
	}
	
	/* ----------------- working dir setup ----------------- */
	
	public static void prepSMTestsWorkingDir() throws IOException {
		prepSMTestsWorkingDir(BUNDLEMODEL_TEST_BUNDLES);
	}
	
	protected static void prepSMTestsWorkingDir(Location pathToCopy) throws IOException {
		FileUtil.deleteDirContents(SMTEST_WORKING_DIR_BUNDLES);
		MiscFileUtils.copyDirContentsIntoDirectory(pathToCopy, SMTEST_WORKING_DIR_BUNDLES);
	}
	
	/* -----------------  ----------------- */
	
	protected Tests_SemanticManager sm;
	
	protected Tests_SemanticManager ___initSemanticManager() throws IOException {
		return ___initSemanticManager(new Tests_DToolServer().getSemanticManager());
	}
	
	protected Tests_SemanticManager ___initSemanticManager(Tests_SemanticManager tests_SemanticManager) {
		if(sm != null) {
			sm.shutdown();
		}
		return sm = tests_SemanticManager;
	}
	
	@After
	public void cleanSemanticManager() {
		if(sm != null) {
			sm.shutdown();
		}
	}
	
	public static class Tests_DToolServer extends DToolServer {
		
		public Tests_DToolServer() {
		}
		
		@Override
		protected SemanticManager createSemanticManager() {
			 // We use this to enable the instrumented compiler install location
			return new Tests_SemanticManager(this);
		}
		
		@Override
		public Tests_SemanticManager getSemanticManager() {
			return (Tests_SemanticManager) super.getSemanticManager();
		}
		
		@Override
		public void logError(String message, Throwable throwable) {
			assertFail();
		}
		
	}
	
	public static class Tests_SemanticManager extends SemanticManager {
		
		private Tests_SemanticManager(DToolServer dtoolServer) {
			super(dtoolServer);
		}
		
		protected BundleResolution getStoredResolution(BundlePath bundlePath) {
			return getStoredResolution(resolutionKey(bundlePath));
		}
		
		public ResolvedManifest getUpdatedManifest(BundleKey bundleKey) throws CommonException {
			return getUpdatedManifest(bundleKey, defaultManifestUpdateOptions());
		}
		@Override
		public ResolvedManifest getUpdatedManifest(BundleKey bundleKey, ManifestUpdateOptions options) 
				throws CommonException {
			ResolvedManifest manifest = super.getUpdatedManifest(bundleKey, options);
			assertTrue(checkIsManifestStale(bundleKey) == false);
			return manifest;
		}
		
		public boolean checkIsResolutionStale(BundlePath bundlePath) {
			return checkIsResolutionStale(resolutionKey(bundlePath));
		}
		
		public BundleResolution getUpdatedResolution(BundlePath bundlePath) throws CommonException {
			return getUpdatedResolution(resolutionKey(bundlePath), defaultManifestUpdateOptions());
		}
		
		public BundleResolution getUpdatedResolution(ResolutionKey resKey) throws CommonException {
			return getUpdatedResolution(resKey, defaultManifestUpdateOptions());
		}
		@Override
		public BundleResolution getUpdatedResolution(ResolutionKey resKey, ManifestUpdateOptions options) 
				throws CommonException 
		{
			boolean manifestStale = checkIsManifestStale(resKey.bundleKey);
			ResolvedManifest previousManifest = getStoredManifest(resKey.bundleKey);
			
			DubBundleResolution bundleResolution = (DubBundleResolution) super.getUpdatedResolution(resKey, options);
			assertEquals(bundleResolution.resKey, resKey);
			
			assertEquals(bundleResolution.manifest == previousManifest, !manifestStale);
			
			assertTrue(checkIsManifestStale(resKey.bundleKey) == false);
			assertTrue(checkIsResolutionStale(resKey) == false);
			
			// test caching
			assertTrue(bundleResolution == super.getUpdatedResolution(resKey, options));
			
			return bundleResolution;
		}
		
		public void checkStaleStatus(ResolutionKey bundleKey, StaleState staleState) {
			
			assertEquals(manifestManager.getEntry(bundleKey.bundleKey).isStale(), 
				staleState == MANIFEST_STALE);
			
			BundleResolution storedResolution = getStoredResolution(bundleKey);
			
			if(storedResolution == null) {
				assertTrue(staleState == MANIFEST_STALE || staleState == NO_BUNDLE_RESOLUTION);
			} else {
				assertEquals(storedResolution.checkIsModuleListStale(), 
					staleState == MODULES_STALE || staleState == MODULE_LIST_STALE);
				assertEquals(storedResolution.checkIsModuleContentsStale(), 
					staleState == MODULES_STALE || staleState == MODULE_CONTENTS_STALE);
			}
			
			assertEquals(checkIsResolutionStale(bundleKey), staleState != CURRENT);
		}
		
		@Override
		public StandardLibraryResolution getUpdatedStdLibResolution(CompilerInstall foundInstall) {
			StandardLibraryResolution stdLibRes = super.getUpdatedStdLibResolution(foundInstall);
			
			// Test caching of resolution
			assertAreEqual(stdLibRes.compilerInstall, super.getUpdatedStdLibResolution(foundInstall).compilerInstall);
			assertTrue(stdLibRes == super.getUpdatedStdLibResolution(foundInstall));
			
			assertTrue(stdLibRes.checkIsModuleListStale() == false);
			assertTrue(stdLibRes.checkIsModuleContentsStale() == false);
			
			return stdLibRes;
		}
		
	}
	
	protected BundleResolution getUpdatedResolution(BundlePath bundlePath) throws CommonException {
		return getUpdatedResolution(resKey(bundlePath));
	}
	
	protected BundleResolution getUpdatedResolution(ResolutionKey resKey) throws CommonException {
		assertTrue(sm.checkIsResolutionStale(resKey));
		return sm.getUpdatedResolution(resKey, defaultManifestUpdateOptions());
	}
	
	
	public enum StaleState { CURRENT, MANIFEST_STALE, 
		MODULE_LIST_STALE, MODULE_CONTENTS_STALE, MODULES_STALE, NO_BUNDLE_RESOLUTION,
		DEP_STALE }
	
	protected void checkStaleStatus(BundlePath bundlePath, StaleState staleState) {
		checkStaleStatus(resKey(bundlePath), staleState);
	}
	
	protected void checkStaleStatus(ResolutionKey resKey, StaleState staleState) {
		sm.checkStaleStatus(resKey, staleState);
	}
	
	
	protected void checkGetModule(BundlePath bundlePath, String moduleName) throws ModuleSourceException {
		checkGetModule(sm.getStoredResolution(bundlePath), moduleName, moduleName);
	}
	protected void checkGetModule(BundlePath bundlePath, String moduleName, 
			String expectedModuleName) throws ModuleSourceException {
		checkGetModule(sm.getStoredResolution(bundlePath), moduleName, expectedModuleName);
	}
	
	protected ResolvedModule checkGetModule(BundleResolution bundleRes, String moduleName, 
			String expectedModuleName) throws ModuleSourceException {
		ResolvedModule resolvedModule = bundleRes.getBundleResolvedModule(moduleName);
		
		if(expectedModuleName != null) {
			assertNotNull(resolvedModule);
			assertTrue(resolvedModule.getModuleNode().getFullyQualifiedName().equals(expectedModuleName));
			if(!sm.checkIsResolutionStale(bundleRes.resKey)) {
				try {
					assertTrue(resolvedModule == sm.getUpdatedResolvedModule(
							resolvedModule.getModulePath(),
							bundleRes.getStdLibResolution().getCompilerInstall(),
							null)
					);
				} catch (CommonException e) {
					assertFail();
				}
			}
		} else {
			assertTrue(resolvedModule  == null);
		}
		
		return resolvedModule;
	}
	
	protected void testFindResolvedModule(BundlePath bundlePath, String moduleNameStr, Location expectedPath) 
			throws ModuleSourceException, ExecutionException {
		BundleResolution bundleRes = sm.getStoredResolution(bundlePath);
		testFindResolvedModule(bundleRes, moduleNameStr, expectedPath);
	}
	
	protected ResolvedModule testFindResolvedModule(AbstractBundleResolution bundleContext, String moduleNameStr, 
			Location expectedPath) throws ModuleSourceException {
		assertNotNull(bundleContext);
		ModuleFullName moduleFullName = new ModuleFullName(moduleNameStr);
		ResolvedModule resolvedModule = bundleContext.findResolvedModule(moduleFullName);
		Location modulePath = resolvedModule == null ? null : resolvedModule.getModulePath();
		assertAreEqual(modulePath, expectedPath);
		
		if(modulePath != null) {
			assertTrue(bundleContext.findResolvedModule(modulePath) == resolvedModule);
		}
		Module moduleNode = resolvedModule == null ? null : resolvedModule.getModuleNode();
		assertAreEqual(bundleContext.findModuleNode(moduleFullName), moduleNode);
		
		return resolvedModule;
	}
	
	/* -----------------  ----------------- */
	
	protected ResolvedModule getUpdatedResolvedModule(Location filePath, String fullName) 
			throws CommonException {
		ResolvedModule resolvedModule = getUpdatedResolvedModule(filePath);
		assertTrue(resolvedModule == getUpdatedResolvedModule(filePath)); // Check instance remains same.
		assertEquals(resolvedModule.getModuleNode().getFullyQualifiedName(), fullName);
		return resolvedModule;
	}
	
	protected ResolvedModule getUpdatedResolvedModule(Location filePath) throws CommonException {
		return sm.getUpdatedResolvedModule(filePath, DEFAULT_TestsCompilerInstall, null);
	}
	
	/* ----------------- some common files ----------------- */
	
	public static ResolutionKey resolutionKey(BundlePath bundlePath) {
		return resolutionKey(bundlePath, DEFAULT_TestsCompilerInstall);
	}
	
	public static ResolutionKey resolutionKey(BundlePath bundlePath, Location compilerPath) {
		CompilerInstall compilerInstall = DToolServer.getCompilerInstallForPath(compilerPath);
		return resolutionKey(bundlePath, compilerInstall);
	}
	
	public static ResolutionKey resolutionKey(BundlePath bundlePath, CompilerInstall compilerInstall) {
		return new ResolutionKey(new BundleKey(bundlePath), compilerInstall);
	}

	protected final Location BASIC_LIB_FOO_MODULE = loc(BASIC_LIB, "source/basic_lib_pack/foo.d");
	protected final String BASIC_LIB_FOO_MODULE_Name = "basic_lib_pack.foo";
	
}