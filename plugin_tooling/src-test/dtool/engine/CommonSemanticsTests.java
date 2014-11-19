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


import java.io.IOException;
import java.nio.file.Path;

import melnorme.lang.utils.MiscFileUtils;
import melnorme.utilbox.misc.FileUtil;
import melnorme.utilbox.tests.TestsWorkingDir;

import org.junit.AfterClass;

import dtool.dub.BundlePath;
import dtool.dub.CommonDubTest;
import dtool.engine.CommonSemanticManagerTest.Tests_SemanticManager;
import dtool.tests.CommonDToolTest;
import dtool.tests.DToolTestResources;

public class CommonSemanticsTests extends CommonDToolTest {
	
	public static final Path SEMMODEL_TEST_BUNDLES = DToolTestResources.getTestResourcePath("semanticModel");

	public static final Path WORKING_DIR = TestsWorkingDir.getWorkingDir().toPath().resolve("SemModel");
	

	/* -----------------  ----------------- */
	
	static {
		// workaround to cleanup state of abruptly-terminated tests
		CommonDubTest.dubRemovePath(WORKING_DIR); 
		CommonDubTest.dubRemovePath(SEMMODEL_TEST_BUNDLES);
		
		// init
		CommonDubTest.dubAddPath(SEMMODEL_TEST_BUNDLES);
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				CommonDubTest.dubRemovePath(SEMMODEL_TEST_BUNDLES);
			}
		});
	}
	
	/* -----------------  ----------------- */
	
	protected static Tests_SemanticManager defaultSemMgr = new Tests_SemanticManager();
	
	@AfterClass
	public static void cleanDefaultSemanticManager() {
//		defaultSemMgr.shutdown();
//		defaultSemMgr = null;
	}
	
	public static BundlePath bundlePath(Path basePath, String other) {
		return BundlePath.create(basePath.resolve(other));
	}
	
	/* ----------------- working dir setup ----------------- */
	
	public static void prepSMTestsWorkingDir() throws IOException {
		FileUtil.deleteDirContents(WORKING_DIR);
		MiscFileUtils.copyDirContentsIntoDirectory(SEMMODEL_TEST_BUNDLES, WORKING_DIR);
	}
	
}