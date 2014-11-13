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

import static dtool.engine.BundleResolution_ModuleListTest.DEFAULT_DMD_INSTALL_LOCATION__Object_Path;
import static dtool.tests.MockCompilerInstalls.DEFAULT_DMD_INSTALL_EXE_PATH;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import melnorme.lang.tooling.bundles.ModuleFullName;
import melnorme.utilbox.misc.FileUtil;
import melnorme.utilbox.misc.MiscUtil;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import dtool.dub.BundlePath;
import dtool.dub.CommonDubTest;
import dtool.dub.ResolvedManifest;
import dtool.engine.util.FileCachingEntry;
import dtool.parser.DeeParserResult.ParsedModule;

public class SemanticManager_Test extends CommonSemanticManagerTest {
	
	@BeforeClass
	public static void initDubRepositoriesPath() {
		CommonDubTest.dubRemovePath(SEMMODEL_TEST_BUNDLES);
		CommonDubTest.dubAddPath(WORKING_DIR);
	}
	
	@AfterClass
	public static void cleanupDubRepositoriesPath() {
		CommonDubTest.dubRemovePath(WORKING_DIR);
	}
	
	@Override
	public Path getDubRepositoryDir() {
		return WORKING_DIR;
	}

	/* -----------------  ----------------- */
	
	protected HashMap<BundlePath, BundleResolution> previousSRs;
	
	protected BundleResolution storeCurrentInMap(BundlePath bundlePath) throws ExecutionException {
		BundleResolution bundleSR = sm.getStoredResolution(bundlePath);
		previousSRs.put(bundlePath, bundleSR);
		checkChanged(bundlePath, false);
		return bundleSR;
	}
	
	protected void checkChanged(BundlePath bundlePath, boolean expectedChanged) throws ExecutionException {
		BundleResolution previousManifest = previousSRs.get(bundlePath);
		if(previousManifest != null) {
			assertTrue(previousManifest.bundlePath.equals(bundlePath));
		}
		boolean changed = previousManifest != sm.getStoredResolution(bundlePath);
		assertTrue(changed == expectedChanged);
		if(expectedChanged) {
			checkStaleStatus(BASIC_LIB, StaleState.CURRENT);
		}
	}
	
	protected void __storeCurrentManifests__() throws ExecutionException {
		previousSRs = new HashMap<>();
		storeCurrentInMap(BASIC_LIB);
		storeCurrentInMap(BASIC_LIB2);
		storeCurrentInMap(SMTEST);
		storeCurrentInMap(COMPLEX_LIB);
		storeCurrentInMap(COMPLEX_BUNDLE);
	}
	
	/* -----------------  ----------------- */
	@Test
	public void testManifestUpdates() throws Exception { testManifestUpdates$(); }
	public void testManifestUpdates$() throws Exception {
		sm = ___initSemanticManager();
		
		// Test manifest only updates
		sm.getUpdatedManifest(BASIC_LIB);
		checkStaleStatus(BASIC_LIB, StaleState.NO_BUNDLE_RESOLUTION);
		checkStaleStatus(SMTEST, StaleState.MANIFEST_STALE);
		sm.getUpdatedManifest(SMTEST);
		checkStaleStatus(SMTEST, StaleState.NO_BUNDLE_RESOLUTION);
		
		// Test update resolution over current manifests
		getUpdatedResolution(BASIC_LIB);
		checkStaleStatus(BASIC_LIB, StaleState.CURRENT);
		checkStaleStatus(SMTEST, StaleState.NO_BUNDLE_RESOLUTION);
		
		// Test update resolution over partially current manifests
		sm = ___initSemanticManager();
		sm.getUpdatedManifest(BASIC_LIB);
		getUpdatedResolution(SMTEST);
		checkStaleStatus(SMTEST, StaleState.CURRENT);
		
		// -- Test update resolution --
		sm = ___initSemanticManager();
		
		__storeCurrentManifests__();
		getUpdatedResolution(BASIC_LIB);
		checkChanged(BASIC_LIB, true);
		checkChanged(SMTEST, false);
		assertAreEqual(sm.getStoredResolution(SMTEST), null);
		
		__storeCurrentManifests__();
		getUpdatedResolution(SMTEST);
		checkChanged(SMTEST, true);
		checkChanged(BASIC_LIB, true);
		checkChanged(BASIC_LIB2, false);
		
		__storeCurrentManifests__();
		getUpdatedResolution(COMPLEX_BUNDLE);
		checkChanged(COMPLEX_BUNDLE, true);
		checkChanged(SMTEST, true);
		checkChanged(BASIC_LIB, true);
		checkChanged(COMPLEX_LIB, true);
		checkChanged(BASIC_LIB2, true);
		
		
		sm.getUpdatedResolution(COMPLEX_BUNDLE); // reset
		// Test bundle invalidation
		__storeCurrentManifests__();
		invalidateBundleManifest(BASIC_LIB);
		checkStaleStatus(BASIC_LIB, StaleState.MANIFEST_STALE);
		checkStaleStatus(BASIC_LIB2, StaleState.CURRENT);
		checkStaleStatus(SMTEST, StaleState.DEP_STALE);
		
		getUpdatedResolution(SMTEST);
		checkChanged(SMTEST, true);
		checkChanged(BASIC_LIB, true);
		checkChanged(BASIC_LIB2, false);
		checkChanged(COMPLEX_BUNDLE, false);
		
		// Test effect of invalidation then update of bundle sub-tree only
		__storeCurrentManifests__();
		invalidateBundleManifest(BASIC_LIB);
		checkStaleStatus(BASIC_LIB, StaleState.MANIFEST_STALE);
		checkStaleStatus(SMTEST, StaleState.DEP_STALE);
		getUpdatedResolution(BASIC_LIB);
		checkChanged(BASIC_LIB, true);
		checkStaleStatus(BASIC_LIB, StaleState.CURRENT);
		checkStaleStatus(SMTEST, StaleState.DEP_STALE);
	}
	
	protected void invalidateBundleManifest(BundlePath bundlePath) {
		FileCachingEntry<ResolvedManifest> manifestEntry = sm.getInfo(bundlePath).manifestEntry;
		manifestEntry.markStale();
	}
	
	public static BundlePath NON_EXISTANT = bundlePath(SEMMODEL_TEST_BUNDLES, "__NonExistant");
	public static BundlePath ERROR_BUNDLE__MISSING_DEP = bundlePath(SEMMODEL_TEST_BUNDLES, "ErrorBundle_MissingDep");
	
	@Test
	public void testInvalidInput() throws Exception { testInvalidInput$(); }
	public void testInvalidInput$() throws Exception {
		___initSemanticManager();
		
		try {
			sm.getUpdatedResolution(NON_EXISTANT);
		} catch (ExecutionException e) {
			assertTrue(e.getCause() instanceof IOException);
		}
		
		try {
			BundleResolution bundleRes = sm.getUpdatedResolution(ERROR_BUNDLE__MISSING_DEP);
			assertTrue(bundleRes != null && bundleRes.dubBundle.hasErrors());
		} catch (ExecutionException e) {
			throw assertFail();
		}
	}
	
	/* ----------------- module updates ----------------- */
	
	
	protected Path writeToFile(Path newFile, String contents) throws IOException {
		Files.createDirectories(newFile.getParent());
		writeStringToFile(newFile, contents);
		return newFile;
	}
	
	public void writeToFileAndUpdateMTime(Path file, String contents) throws IOException {
		writeToFileAndUpdateMTime(file, contents, true);
	}
	
	public void writeToFileAndUpdateMTime(Path file, String contents, boolean isCacheEntryStale) throws IOException {
		ModuleParseCache_Test.writeToFileAndUpdateMTime(file, contents);
		assertTrue(sm.parseCache.getEntry(file).isStale() == isCacheEntryStale);
	}
	
	protected void deleteFile(Path file) throws IOException {
		assertTrue(FileUtil.deleteIfExists(file));
		assertTrue(file.toFile().exists() == false);
	}
	
	protected final Path BASIC_LIB_FOO_MODULE = BASIC_LIB.resolve("source/basic_lib_pack/foo.d");
	protected final String BASIC_LIB_FOO_MODULE_Name = "basic_lib_pack.foo";
	
	@Test
	public void testModuleUpdates() throws Exception { testModuleUpdates$(); }
	public void testModuleUpdates$() throws Exception {
		prepSMTestsWorkingDir();
		___initSemanticManager();
		
		getUpdatedResolution(COMPLEX_LIB);
		
		
		// Test module-file add
		Path newModule = writeToFile(BASIC_LIB.resolve("source/newModule.d"), "module newModule;");
		checkStaleStatus(BASIC_LIB, StaleState.MODULE_LIST_STALE);
		checkStaleStatus(COMPLEX_LIB, StaleState.DEP_STALE);
		checkGetModule(BASIC_LIB, "newModule", null);
		getUpdatedResolution(COMPLEX_LIB);
		checkStaleStatus(BASIC_LIB, StaleState.CURRENT);
		checkGetModule(BASIC_LIB, "newModule");
		 
		// Test module-file delete
		checkStaleStatus(COMPLEX_LIB, StaleState.CURRENT);
		deleteFile(newModule);
		checkStaleStatus(BASIC_LIB, StaleState.MODULES_STALE);
		checkStaleStatus(COMPLEX_LIB, StaleState.DEP_STALE);
		getUpdatedResolution(COMPLEX_LIB);
		checkGetModule(BASIC_LIB, "newModule", null);
		
		
		// Test module-file modification
		checkStaleStatus(COMPLEX_LIB, StaleState.CURRENT);
		writeToFileAndUpdateMTime(BASIC_LIB_FOO_MODULE, "module basic_lib_pack.foo; /*A*/");
		// The resolution will still be current, because the module is lazy loaded/parsed.
		// And if it wasn't actually loaded, the SM can still be considered current.
		checkStaleStatus(BASIC_LIB, StaleState.CURRENT);
		checkStaleStatus(COMPLEX_LIB, StaleState.CURRENT);
		// Now actually parse the module.
		checkGetModule(BASIC_LIB, BASIC_LIB_FOO_MODULE_Name);
		writeToFileAndUpdateMTime(BASIC_LIB_FOO_MODULE, "module basic_lib_pack.foo; /*B*/");
		checkStaleStatus(BASIC_LIB, StaleState.MODULE_CONTENTS_STALE);
		checkStaleStatus(COMPLEX_LIB, StaleState.DEP_STALE);
		getUpdatedResolution(COMPLEX_LIB);
		
		
		// Test optimization: module-file modification with same source as before.
		checkStaleStatus(COMPLEX_LIB, StaleState.CURRENT);
		checkGetModule(BASIC_LIB, BASIC_LIB_FOO_MODULE_Name, BASIC_LIB_FOO_MODULE_Name);
		writeToFileAndUpdateMTime(BASIC_LIB_FOO_MODULE, readStringFromFile(BASIC_LIB_FOO_MODULE));
		assertTrue(sm.parseCache.getEntry(BASIC_LIB_FOO_MODULE).isStale() == true);
		// This stale check will make the parse cache entry no longer stale
		sm.getStoredResolution(COMPLEX_LIB).checkIsStale();
		assertTrue(sm.parseCache.getEntry(BASIC_LIB_FOO_MODULE).isStale() == false);
		checkStaleStatus(BASIC_LIB, StaleState.CURRENT);
		checkStaleStatus(COMPLEX_LIB, StaleState.CURRENT);
		
		// Test WC
		testWorkingCopyModifications();
	}
	
	protected static final String SOURCE1 = "module change1;";
	protected static final String SOURCE2 = "module change2; /* */";
	protected static final String SOURCE3 = "/* */ module  change3;";
	
	protected void testWorkingCopyModifications() throws ExecutionException, IOException {
		Path modulePath = COMPLEX_LIB.path.resolve("source/complex_lib.d");
		
		writeToFileAndUpdateMTime(modulePath, "module change0;");
		getUpdatedResolution(COMPLEX_BUNDLE);
		getUpdatedResolvedModule(modulePath, "change0");
		
		// Test initial working copy modification
		doUpdateWorkingCopy(modulePath, SOURCE1);
		checkStaleStatus(COMPLEX_LIB, StaleState.MODULE_CONTENTS_STALE);
		checkStaleStatus(COMPLEX_BUNDLE, StaleState.DEP_STALE);
		getUpdatedResolvedModule(modulePath, "change1");
		
		// Do modification with same source: no change in staleness
		doUpdateWorkingCopy(modulePath, SOURCE1); 
		checkStaleStatus(COMPLEX_LIB, StaleState.CURRENT);
		getUpdatedResolvedModule(modulePath, "change1");
		
		// Do second modification
		doUpdateWorkingCopy(modulePath, SOURCE2);
		checkStaleStatus(COMPLEX_LIB, StaleState.MODULE_CONTENTS_STALE);
		getUpdatedResolvedModule(modulePath, "change2");
		
		// Test discardWorkingCopy to previous CURRENT state
		doDiscardWorkingCopy(modulePath);
		checkStaleStatus(COMPLEX_LIB, StaleState.MODULE_CONTENTS_STALE);
		getUpdatedResolvedModule(modulePath, "change0");
		
		
		// Test file update over a working copy
		doUpdateWorkingCopy(modulePath, SOURCE1);
		writeToFileAndUpdateMTime(modulePath, SOURCE2, false);
//		checkStaleStatus(COMPLEX_LIB, StaleState.MODULES_STALE);
//		getUpdatedResolvedModule(modulePath, SOURCE2);
//		// Then discard
//		sm.discardWorkingCopy(modulePath);
//		checkStaleStatus(COMPLEX_LIB, StaleState.CURRENT);
		
		// Test delete over a working copy
		
	}

	protected ParsedModule doUpdateWorkingCopy(Path filePath, String contents) {
		BundlePath bundlePath = BundlePath.findBundleForPath(filePath);
		assertTrue(sm.checkIsResolutionStale(bundlePath) == false);
		return sm.setWorkingCopyAndParse(filePath, contents);
	}
	
	protected void doDiscardWorkingCopy(Path filePath) {
		BundlePath bundlePath = BundlePath.findBundleForPath(filePath);
		assertTrue(sm.checkIsResolutionStale(bundlePath) == false);
		sm.discardWorkingCopy(filePath);
		
		assertTrue(sm.parseCache.getEntry(filePath).getParsedModuleIfNotStale(true) == null);
	}
	
	/* -----------------  ----------------- */
	
	protected ResolvedModule getUpdatedResolvedModule(Path filePath, String fullName) 
			throws ExecutionException {
		ResolvedModule resolvedModule = getUpdatedResolvedModule(filePath);
		assertTrue(resolvedModule == getUpdatedResolvedModule(filePath)); // Check instance remains same.
		assertEquals(resolvedModule.getModuleNode().getFullyQualifiedName(), fullName);
		return resolvedModule;
	}
	
	protected ResolvedModule getUpdatedResolvedModule(Path complexLib_module) throws ExecutionException {
		return sm.getUpdatedResolvedModule(complexLib_module);
	}
	
	public final BundlePath NOT_A_BUNDLE = bundlePath(getDubRepositoryDir(), "not_a_bundle");
	
	@Test
	public void testGetResolvedModule() throws Exception { testGetResolvedModule$(); }
	public void testGetResolvedModule$() throws Exception {
		prepSMTestsWorkingDir();
		sm = ___initSemanticManager();
		sm.getUpdatedResolution(COMPLEX_LIB);
		
		ResolvedModule resolvedModule = getUpdatedResolvedModule(BASIC_LIB_FOO_MODULE);
		assertTrue(resolvedModule.bundleRes == sm.getStoredResolution(BASIC_LIB));
		
		BundleResolution complexLibSR = sm.getUpdatedResolution(COMPLEX_LIB);
		assertTrue(resolvedModule == complexLibSR.findResolvedModule(new ModuleFullName(BASIC_LIB_FOO_MODULE_Name)));
		assertTrue(complexLibSR.getCompilerPath().equals(DEFAULT_DMD_INSTALL_EXE_PATH));
		
		// Test getResolvedModule for missing file - must throw
		try {
			resolvedModule = getUpdatedResolvedModule(NOT_A_BUNDLE.resolve("_does_not_exist.d"));
			assertFail();
		} catch (ExecutionException e) {
		}
		
		// Test getResolvedModule for module that is not in bundle import folders.
		resolvedModule = getUpdatedResolvedModule(BASIC_LIB.resolve("not_source/not_source_foo.d"));
		assertTrue(resolvedModule.bundleRes == sm.getStoredResolution(BASIC_LIB)); // Not that important
		assertEqualSet(resolvedModule.bundleRes.findModules("o"), hashSet(
			"object"
		));
		
		// Test getResolvedModule for module that is not in a bundle at all.
		resolvedModule = getUpdatedResolvedModule(NOT_A_BUNDLE.resolve("not_a_bundle_foo.d"));
		assertEqualSet(resolvedModule.bundleRes.findModules("o"), hashSet(
			"object"
		));
		testFindResolvedModule(resolvedModule.bundleRes, "object", DEFAULT_DMD_INSTALL_LOCATION__Object_Path);
		
		
		// Test getResolvedModule for a relative path.
		Path specialPath = MiscUtil.createValidPath(("###special/relative_bundle.d"));
		sm.parseCache.parseModuleWithNewSource(specialPath, "module relative_bundle;");
		resolvedModule = getUpdatedResolvedModule(specialPath);
		assertEqualSet(resolvedModule.bundleRes.findModules("o"), hashSet(
			"object"
		));
		testFindResolvedModule(resolvedModule.bundleRes, "object", DEFAULT_DMD_INSTALL_LOCATION__Object_Path);
		
	}
	
}