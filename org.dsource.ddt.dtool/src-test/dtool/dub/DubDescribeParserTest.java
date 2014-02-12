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

import java.nio.file.Path;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import dtool.tests.DToolTestResources;

public class DubDescribeParserTest extends CommonDubTest {
	
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
		DubBundleDescription dubDescribe = new DubDescribeParser().parseDescription(describeSource);
		
		checkResolvedBundle(dubDescribe, null, "xptobundle", 
			dep(XPTO_BUNDLE, null, "xptobundle", "~master", paths("src", "src-test")), 
			dep(DUB_WORKSPACE.resolve("foo_lib"), null, "foo_lib", "~master", paths("src", "src2")), 
			dep(DUB_WORKSPACE.resolve("bar_lib"), null, "bar_lib", "~master", paths("source")));
	}
	
	public static final Path DESCRIBE_RESPATH = DToolTestResources.getTestResourcePath("dub", "_describeErrors");
	
	@Test
	public void testDescriptionParseErrors() throws Exception { testDescriptionParseErrors$(); }
	public void testDescriptionParseErrors$() throws Exception {
		{
			String source = readStringFromFile(DESCRIBE_RESPATH.resolve("error.no_mainPackage.json"));
			DubBundleDescription dubDescribe = new DubDescribeParser().parseDescription(source);
			
			checkResolvedBundle(dubDescribe, "Expected \"mainPackage\" entry.", null, 
				depNoCheck(), 
				depNoCheck(), depNoCheck());
			
		}
		
		{
			String source = readStringFromFile(DESCRIBE_RESPATH.resolve("error.no_package_name_in_dep.json"));
			DubBundleDescription dubDescribe = new DubDescribeParser().parseDescription(source);
			
			checkResolvedBundle(dubDescribe, "Bundle name not defined.", "xptobundle", 
				depNoCheck(),
				dep("Bundle name not defined.", "foo_lib"),
				depNoCheck());
		}
	}
	
}