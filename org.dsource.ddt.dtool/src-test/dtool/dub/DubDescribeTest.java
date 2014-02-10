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

import static dtool.dub.DubParserTest.checkBundle;
import static dtool.dub.DubParserTest.paths;

import java.nio.file.Path;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import dtool.dub.DubBundle.DubBundleDescription;
import dtool.tests.DToolTestResources;

public class DubDescribeTest extends DubCommonTest {
	
	public static final Path DUB_WORKSPACE = DubParserTest.DUB_WORKSPACE;
	
	protected static final Path XPTO_BUNDLE = DUB_WORKSPACE.resolve("XptoBundle");
	
	@BeforeClass
	public static void initDubRepositoriesPath() {
		dubAddPath(DUB_WORKSPACE);
	}
	@AfterClass
	public static void cleanupDubRepositoriesPath() {
		dubRemovePath(DUB_WORKSPACE);
	}
	
	@Test
	public void testBasic() throws Exception { testBasic$(); }
	public void testBasic$() throws Exception {
		
		String describeSource = runDubDescribe(XPTO_BUNDLE);
		
		DubBundleDescription dubDescribe = new DubBundleDescriptionParser().parseDescription(describeSource);
		assertEquals(dubDescribe.bundleName, "xptobundle");
		assertExceptionContains(dubDescribe.error, null);
		checkBundle(dubDescribe.getMainBundle(), XPTO_BUNDLE, 
				"xptobundle", "~master", paths("src", "src-test"), null);
		checkBundle(dubDescribe.getBundleDependencies()[0], DUB_WORKSPACE.resolve("foo_lib"), 
				"foo_lib", "~master", paths("src", "src2"), null);
		checkBundle(dubDescribe.getBundleDependencies()[1], DUB_WORKSPACE.resolve("bar_lib"), 
				"bar_lib", "~master", paths("source"), null);
		
	}
	
	public static final Path DESCRIBE = DToolTestResources.getTestResourcePath("dub", "_describeErrors");
	
	@Test
	public void testDescriptionParseErrors() throws Exception { testDescriptionParseErrors$(); }
	public void testDescriptionParseErrors$() throws Exception {
		{
			Path describeOutputFile = DESCRIBE.resolve("error.no_mainPackage.json");
			String source = readStringFromFile(describeOutputFile.toFile());
			
			DubBundleDescription dubDescribe = new DubBundleDescriptionParser().parseDescription(source);
			assertExceptionContains(dubDescribe.error, "Expected \"mainPackage\" entry.");
		}
		
		{
			Path describeOutputFile = DESCRIBE.resolve("error.no_package_name.json");
			String source = readStringFromFile(describeOutputFile.toFile());
			
			DubBundleDescription dubDescribe = new DubBundleDescriptionParser().parseDescription(source);
			assertEquals(dubDescribe.bundleName, "xptobundle");
			assertExceptionContains(dubDescribe.error, "name not defined.");
			assertExceptionContains(dubDescribe.getBundleDependencies()[0].error, "name not defined.");
		}
	}
	
}