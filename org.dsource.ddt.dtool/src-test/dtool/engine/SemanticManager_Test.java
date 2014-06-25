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

import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import melnorme.utilbox.misc.FileUtil;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import dtool.dub.BundlePath;
import dtool.dub.CommonDubTest;
import dtool.dub.ResolvedManifest;
import dtool.engine.AbstractBundleResolution.ResolvedModule;
import dtool.engine.util.FileCachingEntry;
import dtool.parser.DeeParserResult.ParsedModule;

public class SemanticManager_Test extends CommonSemanticModelTest {
	
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
		sm = new SemanticManager(new Tests_DToolServer());
		
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
		sm = new SemanticManager(new Tests_DToolServer());
		sm.getUpdatedManifest(BASIC_LIB);
		getUpdatedResolution(SMTEST);
		checkStaleStatus(SMTEST, StaleState.CURRENT);
		
		// -- Test update resolution --
		sm = new SemanticManager(new Tests_DToolServer());
		
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
	
	public static BundlePath NON_EXISTANT = createBP(SEMMODEL_TEST_BUNDLES, "__NonExistant");
	public static BundlePath ERROR_BUNDLE__MISSING_DEP = createBP(SEMMODEL_TEST_BUNDLES, "ErrorBundle_MissingDep");
	
	@Test
	public void testInvalidInput() throws Exception { testInvalidInput$(); }
	public void testInvalidInput$() throws Exception {
		sm = new SemanticManager(new Tests_DToolServer());
		
		try {
			sm.getUpdatedResolution(NON_EXISTANT);
		} catch (ExecutionException e) {
			assertTrue(e.getCause() instanceof IOException);
		}
		
		try {
			BundleResolution bundleRes = sm.getUpdatedResolution(ERROR_BUNDLE__MISSING_DEP);
			assertTrue(bundleRes != null && bundleRes.bundleDubInfo.hasErrors());
		} catch (ExecutionException e) {
			throw assertFail();
		}
	}
	
	/* ----------------- module updates ----------------- */
	
	
	protected Path writeFile(Path newFile, String fileContents) throws IOException {
		Files.createDirectories(newFile.getParent());
		writeStringToFile(newFile, fileContents);
		return newFile;
	}
	
	public static void writeToFileAndUpdateMTime(Path file, String contents) throws IOException {
		FileTime lastModifiedTime = Files.getLastModifiedTime(file);
		writeStringToFile(file.toFile(), contents);
		// Make sure last modified time is different from before
		Files.setLastModifiedTime(file, FileTime.fromMillis(lastModifiedTime.toMillis() - 1_000));
	}
	
	protected void deleteFile(Path file) throws IOException {
		assertTrue(FileUtil.deleteIfExists(file));
		assertTrue(file.toFile().exists() == false);
	}
	
	@Test
	public void testModuleUpdates() throws Exception { testModuleUpdates$(); }
	public void testModuleUpdates$() throws Exception {
		prepSMTestsWorkingDir();
		__initSemanticManager();
		
		getUpdatedResolution(COMPLEX_LIB);
		
		
		// Test module-file add
		Path newModule = writeFile(BASIC_LIB.resolve("source/newModule.d"), "module newModule;");
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
		checkGetModule(BASIC_LIB, "basic_lib_pack.foo", "basic_lib_pack.foo");
		writeFile(BASIC_LIB.resolve("source/basic_lib_pack/foo.d"), "module basic_lib_pack.foo; /***/");
		checkStaleStatus(BASIC_LIB, StaleState.MODULE_CONTENTS_STALE);
		checkStaleStatus(COMPLEX_LIB, StaleState.DEP_STALE);
		getUpdatedResolution(COMPLEX_LIB);
		
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
		writeToFileAndUpdateMTime(modulePath, SOURCE2);
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
		return sm.updateWorkingCopyAndParse(filePath, contents);
	}
	
	protected void doDiscardWorkingCopy(Path filePath) {
		BundlePath bundlePath = BundlePath.findBundleForPath(filePath);
		assertTrue(sm.checkIsResolutionStale(bundlePath) == false);
		sm.discardWorkingCopy(filePath);
		
		assertTrue(sm.parseCache.getEntry(filePath).getParsedModuleIfNotStale() == null);
	}
	
	/* -----------------  ----------------- */
	
	protected ResolvedModule getUpdatedResolvedModule(Path complexLib_module, String fullName) 
			throws ExecutionException {
		ResolvedModule resolvedModule = getUpdatedResolvedModule(complexLib_module);
		assertTrue(resolvedModule == getUpdatedResolvedModule(complexLib_module)); // Check instance remains same.
		assertEquals(resolvedModule.getModuleNode().getFullyQualifiedName(), fullName);
		return resolvedModule;
	}
	
	protected ResolvedModule getUpdatedResolvedModule(Path complexLib_module) throws ExecutionException {
		return assertCast(sm.getUpdatedResolvedModule(complexLib_module), ResolvedModule.class);
	}
	
}