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
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import melnorme.utilbox.misc.Location;
import melnorme.utilbox.tests.TestsWorkingDir;
import dtool.dub.BundlePath;
import dtool.dub.CommonDubTest;
import dtool.engine.CommonSemanticManagerTest.Tests_DToolServer;
import dtool.engine.CommonSemanticManagerTest.Tests_SemanticManager;
import dtool.engine.SemanticManager.ManifestUpdateOptions;
import dtool.engine.StandardLibraryResolution.MissingStandardLibraryResolution;
import dtool.engine.compiler_installs.CompilerInstall;
import dtool.tests.CommonDToolTest;
import dtool.tests.DToolTestResources;
import dtool.tests.DToolTests;
import dtool.tests.MockCompilerInstalls;

public class CommonSemanticsTest extends CommonDToolTest {
	
	public static final Location BUNDLEMODEL_TEST_BUNDLES = DToolTestResources.getTestResourceLoc("bundleModel");
	public static final Location SEMANTICS_TEST_BUNDLES = DToolTestResources.getTestResourceLoc("semantics");
	
	public static final Location SMTEST_WORKING_DIR_BUNDLES = TestsWorkingDir.getWorkingDir("BundleModelWD");
	
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
	
	protected static final Tests_SemanticManager defaultSemMgr = new Tests_DToolServer().getSemanticManager() ;
	
	public static final CompilerInstall DEFAULT_TestsCompilerInstall = MockCompilerInstalls.DMD_CompilerInstall;
	
	public static Location loc(BundlePath bundlePath, String other) {
		return bundlePath.resolve(path(other));
	}
	
	public static ManifestUpdateOptions defaultManifestUpdateOptions() {
		return new ManifestUpdateOptions(testsDubPath());
	}
	
	/* -----------------  ----------------- */
	
	protected BundleKey bundleKey(BundlePath bundlePath) {
		return new BundleKey(bundlePath);
	}
	
	public ResolutionKey resKey(BundlePath bundlePath) {
		return resKey(new BundleKey(bundlePath));
	}
	
	public ResolutionKey resKey(BundlePath bundlePath, String subpackageName) {
		return resKey(new BundleKey(bundlePath, subpackageName));
	}
	
	public ResolutionKey resKey(BundlePath bundlePath, Location compilerPath) {
		return resKey(new BundleKey(bundlePath), compilerPath);
	}
	
	public ResolutionKey resKey(BundleKey bundleKey) {
		return new ResolutionKey(bundleKey, DEFAULT_TestsCompilerInstall);
	}
	
	public ResolutionKey resKey(BundleKey bundleKey, Location compilerPath) {
		assertTrue(compilerPath != MissingStandardLibraryResolution.NULL_COMPILER_INSTALL_PATH);
		CompilerInstall compilerInstall = DToolServer.getCompilerInstallForPath(compilerPath);
		assertNotNull(compilerInstall);
		return new ResolutionKey(bundleKey, compilerInstall);
	}
	
}