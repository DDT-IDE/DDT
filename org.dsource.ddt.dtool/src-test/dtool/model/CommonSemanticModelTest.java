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

import static dtool.model.CommonSemanticModelTest.StaleState.CURRENT;
import static dtool.model.CommonSemanticModelTest.StaleState.MANIFEST_STALE;
import static dtool.model.CommonSemanticModelTest.StaleState.MODULE_CONTENTS_STALE;
import static dtool.model.CommonSemanticModelTest.StaleState.MODULE_LIST_STALE;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
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
import dtool.model.ModuleParseCache.ParseSourceException;
import dtool.parser.DeeParserResult.ParsedModule;
import dtool.tests.DToolBaseTest;
import dtool.tests.DToolTestResources;
import dtool.tests.utils.MiscFileUtils;

public class CommonSemanticModelTest extends DToolBaseTest {
	
	public static class Tests_DToolServer extends DToolServer {
		
		public Tests_DToolServer() {
		}
		
		@Override
		protected void logError(String message, Throwable throwable) {
			assertFail();
		}
	}
	
	public static BundlePath createBP(Path basePath, String other) {
		return BundlePath.create(basePath.resolve(other));
	}
	
	public static final Path SEMMODEL_TEST_BUNDLES = DToolTestResources.getTestResourcePath("semanticModel");
	
	@BeforeClass
	public static void initDubRepositoriesPath() {
		CommonDubTest.dubRemovePath(SEMMODEL_TEST_BUNDLES); // workaround to remove duplicate entries
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
	
	/* -----------------  ----------------- */
	
	protected SemanticManager sm;
	
	protected void __initSemanticManager() throws IOException {
		sm = new SemanticManager(new Tests_DToolServer());
	}
	
	@After
	public void cleanSemanticManager() {
		sm.shutdown();
	}
	
	public enum StaleState { CURRENT, MANIFEST_STALE, MODULE_LIST_STALE, MODULE_CONTENTS_STALE }
	
	protected void checkIsStale(BundlePath bundlePath, boolean expected) {
		assertTrue(sm.isResolutionStale(bundlePath) == expected);
		assertTrue(sm.isManifestInfoStale(bundlePath) == expected);
	}
	
	protected void checkIsStale(BundlePath bundlePath, StaleState staleState) {

		boolean expectedStale = staleState != CURRENT;
		assertTrue(sm.isResolutionStale(bundlePath) == expectedStale);
		
		if(staleState == MODULE_LIST_STALE || staleState == MODULE_CONTENTS_STALE) {
			staleState = MANIFEST_STALE; // TODO: more precision in staleness
		}
		assertEquals(sm.isManifestInfoStale(bundlePath), staleState == MANIFEST_STALE);
//		assertEquals(sm.isModuleListStale(bundlePath), staleState == MODULE_LIST_STALE);
//		assertEquals(sm.isModuleContentsStale(bundlePath), staleState == MODULE_CONTENTS_STALE); // TODO
	}
	
	protected void checkIsStaleInDepsOnly(BundlePath bundlePath) {
		assertTrue(sm.isResolutionStale(bundlePath) == true);
		assertTrue(sm.isManifestInfoStale(bundlePath) == false); // Node itself is up to date
	}
	
	protected BundleSemanticResolution testGetUpdatedResolution(BundlePath bundlePath) throws ExecutionException {
		BundleSemanticResolution bundleSR = sm.getUpdatedResolution(bundlePath);
		assertEquals(bundleSR.bundlePath, bundlePath);
		checkIsStale(bundlePath, false);
		assertTrue(bundleSR == sm.getUpdatedResolution(bundlePath));
		return bundleSR;
	}
	
	protected void checkHasModule(BundleSemanticResolution bunddleSR, String moduleName, boolean hasModule) 
			throws ParseSourceException {
		ParsedModule parsedModule = bunddleSR.getParsedModule(moduleName);
		assertTrue((parsedModule != null) == hasModule);
		if(hasModule) {
			assertTrue(parsedModule.module.getName().equals(moduleName)); // Not necessarily true
		}
	}
	
	/* ----------------- working dir setup ----------------- */
	
	public static final Path WORKING_DIR = TestsWorkingDir.getWorkingDir().toPath().resolve(
			"SemModel");
	
	public static void prepSMTestsWorkingDir() throws IOException {
		FileUtil.deleteDirContents(WORKING_DIR);
		MiscFileUtils.copyDirContentsIntoDirectory(SEMMODEL_TEST_BUNDLES, WORKING_DIR);
	}
	
}