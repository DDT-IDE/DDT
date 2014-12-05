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


import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;

import java.nio.file.Path;

import melnorme.utilbox.tests.TestsWorkingDir;
import dtool.dub.BundlePath;
import dtool.dub.CommonDubTest;
import dtool.engine.CommonSemanticManagerTest.Tests_SemanticManager;
import dtool.engine.compiler_installs.CompilerInstall;
import dtool.tests.CommonDToolTest;
import dtool.tests.DToolTestResources;
import dtool.tests.DToolTests;

public class CommonSemanticsTest extends CommonDToolTest {
	
	public static final Path BUNDLEMODEL_TEST_BUNDLES = DToolTestResources.getTestResourcePath("semanticModel");
	public static final Path SEMANTICS_TEST_BUNDLES = DToolTestResources.getTestResourcePath("semantics");
	
	public static final Path SMTEST_WORKING_DIR_BUNDLES = TestsWorkingDir.getWorkingDirPath("SemModel");
	
	/* -----------------  ----------------- */
	
	static {
		if(!DToolTests.TESTS_LITE_MODE) {
			// workaround to cleanup state of abruptly-terminated tests
			CommonDubTest.dubRemovePath(SMTEST_WORKING_DIR_BUNDLES); 
			CommonDubTest.dubRemovePath(BUNDLEMODEL_TEST_BUNDLES); 
			CommonDubTest.dubRemovePath(SEMANTICS_TEST_BUNDLES); 
		}
		
		// init
		CommonDubTest.dubAddPath(SEMANTICS_TEST_BUNDLES);
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				CommonDubTest.dubRemovePath(SEMANTICS_TEST_BUNDLES);
			}
		});
	}
	
	/* -----------------  ----------------- */
	
	protected static Tests_SemanticManager defaultSemMgr = new Tests_SemanticManager();
	
	public static BundlePath bundlePath(Path basePath, String other) {
		return BundlePath.create(basePath.resolve(other));
	}
	
	public static BundleKey bundleKey(Path basePath, String subpackageName) {
		return new BundleKey(BundlePath.create(basePath), subpackageName);
	}
	
	protected BundleKey bundleKey(BundlePath bundlePath) {
		return bundleKey(bundlePath.path, null);
	}
	
	public ResolutionKey resKey(Path basePath) {
		return resKey(BundlePath.create(basePath));
	}
	
	public ResolutionKey resKey(BundlePath bundlePath) {
		return resKey(new BundleKey(bundlePath));
	}
	
	public ResolutionKey resKey(BundlePath bundlePath, Path compilerPath) {
		return resKey(new BundleKey(bundlePath), compilerPath);
	}
	
	public ResolutionKey resKey(BundleKey bundleKey) {
		return resKey(bundleKey, (Path) null);
	}
	
	public ResolutionKey resKey(BundleKey bundleKey, Path compilerPath) {
		CompilerInstall compilerInstall = defaultSemMgr.getCompilerInstallForPath(compilerPath);
		assertNotNull(compilerInstall);
		return new ResolutionKey(bundleKey, compilerInstall);
	}
	
}