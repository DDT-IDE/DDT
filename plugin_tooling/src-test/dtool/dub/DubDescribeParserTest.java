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

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import static melnorme.utilbox.core.CoreUtil.areEqual;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import melnorme.utilbox.core.fntypes.Predicate;
import melnorme.utilbox.misc.CollectionUtil;
import melnorme.utilbox.misc.MiscUtil;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import dtool.dub.DubBundle.BundleFile;
import dtool.tests.DToolTestResources;


public class DubDescribeParserTest extends CommonDubTest {
	
	protected static final DubBundleChecker BAR_LIB_CHECKER = 
			bundle(DUB_TEST_BUNDLES.resolve("bar_lib"), null, "bar_lib", "~master", paths("source"));
	protected static final DubBundleChecker FOO_LIB_CHECKER = 
			bundle(DUB_TEST_BUNDLES.resolve("foo_lib"), null, "foo_lib", "~master", paths("src", "src2"));
	
	@BeforeClass
	public static void initDubRepositoriesPath() {
		dubAddPath(DUB_TEST_BUNDLES);
	}
	@AfterClass
	public static void cleanupDubRepositoriesPath() {
		dubRemovePath(DUB_TEST_BUNDLES);
	}
	
	@Test
	public void testBasic() throws Exception { testBasic$(); }
	public void testBasic$() throws Exception {
		
		String describeSource = runDubDescribe(XPTO_BUNDLE_PATH);
		DubBundleDescription description = DubDescribeParser.parseDescription(XPTO_BUNDLE_PATH, describeSource);
		
		checkResolvedBundle(description, null, 
			main(XPTO_BUNDLE_PATH.path, null, "xptobundle", "~master", paths("src", "src-test", "src-import"),
				rawDeps("foo_lib"),
				FOO_LIB_CHECKER, 
				BAR_LIB_CHECKER));
		
		checkBundleFiles(description.mainDubBundle.bundleFiles, list(
			"src/app.d", 
			"src/xptoApp.d", 
			"src-import/modA_import_only.d"));
	}
	
	protected void checkBundleFiles(List<BundleFile> bundleFilesOriginal, List<String> expected) {
		ArrayList<BundleFile> bundleFiles = new ArrayList<>(bundleFilesOriginal);
		for (String expectedFile : expected) {
			final Path expectedPath = MiscUtil.createPathOrNull(expectedFile);
			
			assertTrue(CollectionUtil.removeElement(bundleFiles, new Predicate<BundleFile>() {
				@Override
				public boolean evaluate(BundleFile obj) {
					return areEqual(MiscUtil.createPathOrNull(obj.filePath), expectedPath);
				}
			}));
		}
	}
	
	public static final BundlePath DESCRIBE_RESPATH = BundlePath.create(
		DToolTestResources.getTestResourcePath("dub", "_describeErrors"));
	
	@Test
	public void testDescriptionParseErrors() throws Exception { testDescriptionParseErrors$(); }
	public void testDescriptionParseErrors$() throws Exception {
		{
			String source = readStringFromFile(DESCRIBE_RESPATH.resolve("error.no_mainPackage.json"));
			DubBundleDescription dubDescribe = DubDescribeParser.parseDescription(DESCRIBE_RESPATH, source);
			
			checkResolvedBundle(dubDescribe, DubDescribeParser.ERROR_PACKAGES_IS_EMPTY, 
				bundle(DubDescribeParser.ERROR_PACKAGES_IS_EMPTY, IGNORE_STR));
		}
		
		{
			String source = readStringFromFile(DESCRIBE_RESPATH.resolve("error.no_package_name_in_dep.json"));
			DubBundleDescription dubDescribe = DubDescribeParser.parseDescription(DESCRIBE_RESPATH, source);
			
			checkResolvedBundle(dubDescribe, "Bundle name not defined.",
				main(IGNORE_PATH, "Bundle name not defined.", IGNORE_STR, IGNORE_STR, null,
					rawDeps("foo_lib"),
 					IGNORE_DEPS));
		}
	}
	
	
	@Test
	public void testSubPackages() throws Exception { testSubPackages$(); }
	public void testSubPackages$() throws Exception {
		 
		final BundlePath bundlePath = BundlePath.create(DUB_TEST_BUNDLES.resolve("SubPackagesTest"));
		DubBundleDescription description = DubDescribeParser.parseDescription(
			bundlePath, runDubDescribe(bundlePath));
		
		checkResolvedBundle(description, null, 
			main(bundlePath.path, null, "sub_packages_test", "0.1.0", paths("src"),
				rawDeps(
					"bar_lib", 
					"sub_packages_test:sub_x",
					"sub_packages_test:sub_a",
					"sub_packages_test:sub_b"
				),
				FOO_LIB_CHECKER, 
				BAR_LIB_CHECKER,
				bundle(bundlePath.path, null, "sub_packages_test:sub_x", "0.1.0", paths("src")),
				bundle(bundlePath.path, null, "sub_packages_test:sub_a", "0.1.0", paths("src-A")),
				bundle(bundlePath.path, null, "sub_packages_test:sub_b", "0.1.0", paths("src-B"))
			)
		);
		
	}
	
}