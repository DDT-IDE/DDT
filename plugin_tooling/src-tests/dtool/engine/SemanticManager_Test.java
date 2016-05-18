/*******************************************************************************
 * Copyright (c) 2014 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package dtool.engine;

import static dtool.tests.MockCompilerInstalls.DEFAULT_DMD_INSTALL_BaseLocation;
import static dtool.tests.MockCompilerInstalls.GDC_CompilerLocation;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import dtool.dub.CommonDubTest;
import dtool.dub.DubDescribeParserTest;
import dtool.dub.ResolvedManifest;
import dtool.engine.compiler_installs.CompilerInstall;
import dtool.parser.DeeParserResult.ParsedModule;
import melnorme.lang.tooling.BundlePath;
import melnorme.lang.utils.FileCachingEntry;
import melnorme.lang.utils.MiscFileUtils;
import melnorme.utilbox.core.CommonException;
import melnorme.utilbox.misc.FileUtil;
import melnorme.utilbox.misc.Location;

public class SemanticManager_Test extends CommonSemanticManagerTest {
	
	@BeforeClass
	public static void initDubRepositoriesPath() {
		CommonDubTest.dubRemovePath(BUNDLEMODEL_TEST_BUNDLES);
		CommonSemanticsTest.removeSemanticsBundlesDubPath();
		
		CommonDubTest.dubAddPath(SMTEST_WORKING_DIR_BUNDLES);
	}
	
	@AfterClass
	public static void cleanupDubRepositoriesPath() {
		CommonDubTest.dubRemovePath(SMTEST_WORKING_DIR_BUNDLES);
	}
	
	@Before
	public void prepWorkingDir() throws IOException {
		FileUtil.deleteDirContents(SMTEST_WORKING_DIR_BUNDLES); // Make sure state is reset
	}
	
	@Override
	public Location getDubRepositoryDir() {
		return SMTEST_WORKING_DIR_BUNDLES;
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
		ResolutionKey resKey = resKey(bundlePath);
		
		BundleResolution previousResolution = previousSRs.get(bundlePath);
		if(previousResolution != null) {
			assertTrue(previousResolution.resKey.equals(resKey));
		}
		boolean changed = previousResolution != sm.getStoredResolution(resKey);
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
		prepSMTestsWorkingDir();
		___initSemanticManager();
		
		checkStaleStatus(BASIC_LIB, StaleState.MANIFEST_STALE);
		
		// Test manifest only updates
		sm.getUpdatedManifest(bundleKey(BASIC_LIB));
		checkStaleStatus(BASIC_LIB, StaleState.NO_BUNDLE_RESOLUTION);
		checkStaleStatus(SMTEST, StaleState.MANIFEST_STALE);
		sm.getUpdatedManifest(bundleKey(SMTEST));
		checkStaleStatus(SMTEST, StaleState.NO_BUNDLE_RESOLUTION);
		
		{
			FileCachingEntry<ResolvedManifest> entry = sm.manifestManager.infos.getEntry(bundleKey(BASIC_LIB));
			assertTrue(entry.isStale() == false);
			
			// This tests for a case we actually had as a bug undetected for a long time.  
			Location manifestFilePath = BASIC_LIB.getLocation().resolve_fromValid(BundlePath.DUB_MANIFEST_NAME_JSON);
			assertTrue(entry.getFileLocation().equals(manifestFilePath));
			
			makeManifestFileStale(manifestFilePath);
			assertTrue(entry.isStale() == true);
		}
		
		// Test update resolution over current manifests
		getUpdatedResolution(BASIC_LIB);
		checkStaleStatus(BASIC_LIB, StaleState.CURRENT);
		checkStaleStatus(SMTEST, StaleState.NO_BUNDLE_RESOLUTION);
		
		// Test update resolution over partially current manifests
		sm = ___initSemanticManager();
		sm.getUpdatedManifest(bundleKey(BASIC_LIB));
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
	
	public void makeManifestFileStale(Location fileLoc) throws IOException {
		String contents = readStringFromFile(fileLoc);
		writeToFileAndUpdateMTime(fileLoc, contents + "  ", true);
	}
	
	protected void invalidateBundleManifest(BundlePath bundlePath) {
		FileCachingEntry<ResolvedManifest> manifestEntry = sm.manifestManager.getEntry(bundleKey(bundlePath));
		manifestEntry.markStale();
	}
	
	public static BundlePath NON_EXISTANT = 
			bundlePath(SMTEST_WORKING_DIR_BUNDLES, "__NonExistant");
	public static BundlePath ERROR_BUNDLE__MISSING_DEP = 
			bundlePath(SMTEST_WORKING_DIR_BUNDLES, "ErrorBundle_MissingDep");
	
	@Test
	public void testInvalidInput() throws Exception { testInvalidInput$(); }
	public void testInvalidInput$() throws Exception {
		prepSMTestsWorkingDir();
		___initSemanticManager();
		
		try {
			sm.getUpdatedResolution(NON_EXISTANT);
		} catch (CommonException e) {
			assertTrue(e.getCause() instanceof IOException);
		}
		
		ResolvedManifest manifest = null;
		try {
			manifest = sm.super_getUpdatedManifest(bundleKey(ERROR_BUNDLE__MISSING_DEP), 
				defaultManifestUpdateOptions());
			assertTrue(manifest != null && manifest.bundle.hasErrors());
			assertTrue(manifest.bundle.error.getMessage().contains("Unknown dependency: NonExistantDep"));
			
			sm.getUpdatedResolution(ERROR_BUNDLE__MISSING_DEP);
			throw assertFail();
		} catch (CommonException ce) {
			assertTrue(ce.getMessage().equals(SemanticManager.ERROR_UNRESOLVED_DUB_MANIFEST));
			assertTrue(ce.getCause().getMessage().contains(manifest.bundle.error.getMessage()));
		}
		
		// Test that a DUB error makes manifest remain stale
		checkStaleStatus(ERROR_BUNDLE__MISSING_DEP, StaleState.MANIFEST_STALE);
	}
	
	@Test
	public void testSubpackages() throws Exception { testSubpackages$(); }
	public void testSubpackages$() throws Exception {
		prepSMTestsWorkingDir(DubDescribeParserTest.DUB_TEST_BUNDLES);
		___initSemanticManager();
		
		BundlePath SP_TEST = bundlePath(getDubRepositoryDir(), "SubPackagesTest");
		BundlePath SP_FOO = bundlePath(getDubRepositoryDir(), "subpackages_foo");
		BundlePath SP_FOO2 = bundlePath(getDubRepositoryDir(), "subpackages_foo2");
		
		
		sm.getUpdatedResolution(SP_TEST);
		checkStaleStatus(resKey(SP_TEST, "sub_x"), StaleState.CURRENT);
		checkStaleStatus(resKey(SP_TEST, "sub_a"), StaleState.CURRENT);
		checkStaleStatus(resKey(SP_TEST, "sub_b"), StaleState.CURRENT);
		checkStaleStatus(resKey(SP_TEST, "doesn't exists"), StaleState.MANIFEST_STALE);
		
		sm.getUpdatedResolution(SP_FOO);
		
		___initSemanticManager();
		checkStaleStatus(resKey(SP_TEST, "sub_a"), StaleState.MANIFEST_STALE);
		BundleResolution bundleRes;
		bundleRes = sm.getUpdatedResolution(SP_FOO2);
		assertTrue(bundleRes.getDirectDependencies().size() == 1);
		
		checkStaleStatus(resKey(SP_TEST, "sub_a"), StaleState.CURRENT);
	}
	
	/* ----------------- module updates ----------------- */
	
	
	protected Location writeToFile(Location newFile, String contents) throws IOException {
		Files.createDirectories(newFile.getParent().path);
		writeStringToFile(newFile, contents);
		return newFile;
	}
	
	public void writeToFileAndUpdateMTime(Location file, String contents) throws IOException {
		writeToFileAndUpdateMTime(file, contents, true);
	}
	
	public void writeToFileAndUpdateMTime(Location file, String contents, boolean isCacheEntryStale) 
			throws IOException {
		ModuleParseCache_Test.writeToFileAndUpdateMTime(file.path, contents);
		assertTrue(sm.parseCache.getEntry(file.path).isStale() == isCacheEntryStale);
	}
	
	protected void deleteFile(Path file) throws IOException {
		assertTrue(FileUtil.deleteIfExists(file));
		assertTrue(file.toFile().exists() == false);
	}
	
	@Test
	public void testModuleUpdates() throws Exception { testModuleUpdates$(); }
	public void testModuleUpdates$() throws Exception {
		prepSMTestsWorkingDir();
		___initSemanticManager();
		
		getUpdatedResolution(COMPLEX_LIB);
		
		
		// Test module-file add
		Path newModule = writeToFile(loc(BASIC_LIB, "source/newModule.d"), "module newModule;").path;
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
		assertTrue(sm.parseCache.getEntry(BASIC_LIB_FOO_MODULE.path).isStale() == true);
		// This stale check will make the parse cache entry no longer stale
		sm.getStoredResolution(COMPLEX_LIB).checkIsStale();
		assertTrue(sm.parseCache.getEntry(BASIC_LIB_FOO_MODULE.path).isStale() == false);
		checkStaleStatus(BASIC_LIB, StaleState.CURRENT);
		checkStaleStatus(COMPLEX_LIB, StaleState.CURRENT);
		
		// Test WC
		testWorkingCopyModifications();
	}
	
	
	protected static final String SOURCE1 = "module change1;";
	protected static final String SOURCE2 = "module change2; /* */";
	protected static final String SOURCE3 = "/* */ module  change3;";
	
	protected void testWorkingCopyModifications() throws CommonException, IOException {
		Location modulePath = loc(COMPLEX_LIB, "source/complex_lib.d");
		
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

	protected ParsedModule doUpdateWorkingCopy(Location filePath, String contents) {
		BundlePath bundlePath = BundlePath.findBundleForPath(filePath);
		assertTrue(sm.checkIsResolutionStale(bundlePath) == false);
		return sm.setWorkingCopyAndParse(filePath.path, contents);
	}
	
	protected void doDiscardWorkingCopy(Location filePath) {
		BundlePath bundlePath = BundlePath.findBundleForPath(filePath);
		assertTrue(sm.checkIsResolutionStale(bundlePath) == false);
		sm.discardWorkingCopy(filePath.path);
		
		assertTrue(sm.parseCache.getEntry(filePath.path).getParsedModuleIfNotStale() == null);
	}
	
	/* -----------------  ----------------- */
	
	@Test
	public void testStdLibInteractions() throws Exception { testStdLibInteractions$(); }
	public void testStdLibInteractions$() throws Exception {
		prepSMTestsWorkingDir();
		___initSemanticManager();
		
		
		Location DMD_Install_WC_Base = SMTEST_WORKING_DIR_BUNDLES.resolveOrNull("DMD_Install_WC");
		Location DMD_Install_WC = DMD_Install_WC_Base.resolveOrNull("windows/bin/dmd.exe");
		MiscFileUtils.copyDirContentsIntoDirectory(DEFAULT_DMD_INSTALL_BaseLocation, DMD_Install_WC_Base);
		
		getUpdatedResolution(resKey(COMPLEX_LIB, DMD_Install_WC));
		BundleResolution complexLib = sm.getStoredResolution(resKey(COMPLEX_LIB, DMD_Install_WC));
		assertAreEqual(complexLib.getCompilerPath(), DMD_Install_WC);
		
		BundleResolution complexLib2;
		complexLib2 = getUpdatedResolution(resKey(COMPLEX_LIB, GDC_CompilerLocation));
		assertTrue(complexLib != complexLib2);
		
		checkStaleStatus(resKey(COMPLEX_LIB, DMD_Install_WC), StaleState.CURRENT);
		checkStaleStatus(resKey(COMPLEX_LIB, GDC_CompilerLocation), StaleState.CURRENT);
		
		
		StandardLibraryResolution stdLib = sm.getUpdatedStdLibResolution(compilerInstall(DMD_Install_WC));
		
		assertTrue(stdLib.checkIsModuleContentsStale() == false);
		Location DMD_INSTALL_ObjectModule = DMD_Install_WC_Base.resolve_fromValid("src/druntime/import/object.di");
		stdLib.getBundleResolvedModule("object");
		sm.setWorkingCopyAndParse(DMD_INSTALL_ObjectModule.path, "module object.d; /*SM_TEST*/"); 
		assertTrue(stdLib.checkIsModuleContentsStale());
		
		
		checkStaleStatus(resKey(COMPLEX_LIB, DMD_Install_WC), StaleState.DEP_STALE);
		checkStaleStatus(resKey(COMPLEX_LIB, GDC_CompilerLocation), StaleState.CURRENT);
		
		StandardLibraryResolution stdLib2 = sm.getUpdatedStdLibResolution(compilerInstall(DMD_Install_WC));
		assertTrue(stdLib2 != stdLib);
		assertTrue(stdLib2 == sm.getUpdatedStdLibResolution(compilerInstall(DMD_Install_WC)));
		
		checkStaleStatus(resKey(COMPLEX_LIB, DMD_Install_WC), StaleState.DEP_STALE);
		complexLib = sm.getUpdatedResolution(resKey(COMPLEX_LIB, DMD_Install_WC));
		checkStaleStatus(resKey(COMPLEX_LIB, DMD_Install_WC), StaleState.CURRENT);
		
		assertTrue(stdLib2 == sm.getUpdatedStdLibResolution(compilerInstall(DMD_Install_WC)));
	}
	
	protected static CompilerInstall compilerInstall(Location compilerPath) {
		return DToolServer.getCompilerInstallForPath(compilerPath);
	}
	
}