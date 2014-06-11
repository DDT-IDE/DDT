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

import java.io.IOException;
import java.nio.file.Path;

import melnorme.utilbox.misc.FileUtil;
import melnorme.utilbox.tests.TestsWorkingDir;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import dtool.dub.BundlePath;
import dtool.dub.CommonDubTest;
import dtool.tests.DToolBaseTest;
import dtool.tests.DToolTestResources;
import dtool.tests.utils.MiscFileUtils;

public class CommonSemanticModelTest extends DToolBaseTest {

	public static final Path SEMMODEL_TEST_BUNDLES = DToolTestResources.getTestResourcePath("semanticModel");
	
	public static final BundlePath BASIC_LIB = new BundlePath(SEMMODEL_TEST_BUNDLES.resolve("basic_lib"));
	public static final BundlePath SMTEST = new BundlePath(SEMMODEL_TEST_BUNDLES.resolve("smtest_foo"));
	public static final BundlePath BASIC_LIB2 = new BundlePath(SEMMODEL_TEST_BUNDLES.resolve("basic_lib2"));
	public static final BundlePath COMPLEX_LIB = new BundlePath(SEMMODEL_TEST_BUNDLES.resolve("complex_lib"));
	public static final BundlePath COMPLEX_BUNDLE = new BundlePath(SEMMODEL_TEST_BUNDLES.resolve("complex_bundle"));
	
	public static BundlePath createBP(Path basePath, String other) {
		return BundlePath.create(basePath.resolve(other));
	}
	
	
	@BeforeClass
	public static void initDubRepositoriesPath() {
		CommonDubTest.dubRemovePath(SEMMODEL_TEST_BUNDLES); // workaround to remove duplicate entries
		CommonDubTest.dubAddPath(SEMMODEL_TEST_BUNDLES);
	}
	
	@AfterClass
	public static void cleanupDubRepositoriesPath() {
		CommonDubTest.dubRemovePath(SEMMODEL_TEST_BUNDLES);
	}
	
	public static class Tests_DToolServer extends DToolServer {
		
		public Tests_DToolServer() throws IOException {
		}
		
		@Override
		protected void logError(String message, Throwable throwable) {
			assertFail();
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