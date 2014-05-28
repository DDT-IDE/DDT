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
			main(XPTO_BUNDLE_PATH, null, "xptobundle", "~master", paths("src", "src-test", "src-import"),
				rawDeps("foo_lib"),
				bundle(DUB_TEST_BUNDLES.resolve("foo_lib"), null, "foo_lib", "~master", paths("src", "src2")), 
				bundle(DUB_TEST_BUNDLES.resolve("bar_lib"), null, "bar_lib", "~master", paths("source"))));
		
		checkBundleFiles(description.mainDubBundle.bundleFiles, list(
			"src/app.d", 
			"src/xptoApp.d", 
			"src-import/modA_import_only.d"));
	}
	
	protected void checkBundleFiles(List<BundleFile> bundleFilesOriginal, List<String> expected) {
		ArrayList<BundleFile> bundleFiles = new ArrayList<>(bundleFilesOriginal);
		for (String expectedFile : expected) {
			final Path expectedPath = MiscUtil.createValidPath(expectedFile);
			
			assertTrue(CollectionUtil.removeElement(bundleFiles, new Predicate<BundleFile>() {
				@Override
				public boolean evaluate(BundleFile obj) {
					return areEqual(MiscUtil.createValidPath(obj.filePath), expectedPath);
				}
			}));
		}
	}
	
	public static final Path DESCRIBE_RESPATH = DToolTestResources.getTestResourcePath("dub", "_describeErrors");
	
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
				main(IGNORE_PATH, null, "xptobundle", IGNORE_STR, null,
					rawDeps("foo_lib"),
 					IGNORE_DEPS));
		}
	}
	
}