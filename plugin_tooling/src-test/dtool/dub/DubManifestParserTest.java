/*******************************************************************************
 * Copyright (c) 2013, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package dtool.dub;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;

import org.junit.Test;

import dtool.dub.DubBundle.DubConfiguration;
import dtool.tests.DToolTestResources;
import melnorme.lang.tooling.BundlePath;
import melnorme.utilbox.collections.ArrayList2;
import melnorme.utilbox.core.CommonException;
import melnorme.utilbox.misc.Location;
import melnorme.utilbox.misc.MiscUtil;

public class DubManifestParserTest extends CommonDubTest {
	
	@Test
	public void testBasic() throws Exception { testBasic$(); }
	public void testBasic$() throws Exception {
		
		testBundle(main(DUB_TEST_BUNDLES.resolve_fromValid("bar_lib"), 
			null, "bar_lib", DubBundle.DEFAULT_VERSION, paths("source"),
			rawDeps()));
		
		testBundle(main(DUB_TEST_BUNDLES.resolve_fromValid("foo_lib"), 
			null, "foo_lib", DubBundle.DEFAULT_VERSION, paths("src", "src2"), 
			rawDeps("bar_lib")));
		
		testBundle(main(DUB_TEST_BUNDLES.resolve_fromValid("XptoBundle"), 
			null, "xptobundle", DubBundle.DEFAULT_VERSION, paths("src", "src-test", "src-import"),
			rawDeps("foo_lib")));
		
		testBundle(main(DUB_TEST_BUNDLES.resolve_fromValid("LenientJsonA"), 
			null, "lenient-json1", DubBundle.DEFAULT_VERSION, paths("src", "src-test"),
			rawDeps("foo_lib", "other_lib")));
		
		
		testPath(parseDubBundle(DUB_TEST_BUNDLES.resolve_fromValid("XptoBundle")), 
			"bin");
		
		testPath(parseDubBundle(DUB_TEST_BUNDLES.resolve_fromValid("bar_lib")), 
			null);
	}
	
	protected void testPath(DubBundle xptoBundle, String targetPath) 
			throws CommonException {
		assertAreEqual(xptoBundle.getTargetPath(), targetPath);
	}
	
	public DubBundle parseDubBundle(Location location) {
		return assertNotNull(
			DubManifestParser.parseDubBundleFromLocation2(BundlePath.create(location))
		);
	}
	
	public void testBundle(DubBundleChecker bundle) {
		bundle.check(parseDubBundle(bundle.location), false);
	}
	
	
	@Test
	public void testNonExistant() throws Exception { testNonExistant$(); }
	public void testNonExistant$() throws Exception {
		testBundle(bundle(DToolTestResources.getTestResourceLoc("dub").resolve_fromValid("nonExistent"), 
			"java.io.FileNotFoundException", IGNORE_STR, IGNORE_STR, null));
	}
	
	public static final BundlePath SAMPLE_BUNDLE_PATH = new BundlePath(DUB_TEST_BUNDLES);
	
	
	@Test
	public void testBuildConfigs() throws Exception { testBuildConfigs$(); }
	public void testBuildConfigs$() throws Exception {
		BundlePath BUILD_CONFIGS = bundlePath(DUB_TEST_BUNDLES, "build_configs");
		
		DubBundle dubBundle = parseDubBundle(BUILD_CONFIGS.location);
		
		assertAreEqual(dubBundle.getConfigurations(), 
			new ArrayList2<DubConfiguration>(
					new DubConfiguration("metro-app", "executable", null, null),
					new DubConfiguration("glut-app", "library", "foo_glut_app", "bin/lib")
		));
		
		assertEquals(new DubConfiguration("m", "executable", null, null).getEffectiveTargetFullPath(dubBundle), 
			path("default_path/default_name" + MiscUtil.getExecutableSuffix()).toString());
		
		assertEquals(new DubConfiguration("m", "executable", null, "xxx").getEffectiveTargetFullPath(dubBundle), 
			path("xxx/default_name"  + MiscUtil.getExecutableSuffix()).toString());
		
		DubBundle barLibBundle = parseDubBundle(DUB_TEST_BUNDLES.resolve_fromValid("bar_lib"));
		
		assertEquals(new DubConfiguration("m", "executable", null, null).getEffectiveTargetFullPath(barLibBundle), 
			"bar_lib"  + MiscUtil.getExecutableSuffix());
	}
	
}