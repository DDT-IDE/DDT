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

import java.io.FileNotFoundException;
import java.nio.file.Path;

import org.junit.Test;

import dtool.tests.DToolTestResources;

public class DubParserTest extends CommonDubTest {
	
	public static final Path DUB_WORKSPACE = DToolTestResources.getTestResourcePath("dub");
	
	@Test
	public void testBasic() throws Exception { testBasic$(); }
	public void testBasic$() throws Exception {
		
		testBundle(DUB_WORKSPACE.resolve("XptoBundle"), 
				"xptobundle", null, paths("src", "src-test"));
		
		testBundle(DUB_WORKSPACE.resolve("bar_lib"), 
				"bar_lib", null, paths("source"));

		testBundle(DUB_WORKSPACE.resolve("foo_lib"), 
				"foo_lib", null, paths("src", "src2"));
		
		testBundle(DUB_WORKSPACE.resolve("LenientJsonA"), 
				"lenient-json1", null, paths("src", "src-test"));
		
	}
	
	public DubBundle parseDubBundle(Path location) {
		return DubBundleParser.parseDubBundleFromLocation(location);
	}
	
	public void testBundle(Path location, String name, String version, Path[] srcFolders) {
		DubBundle dubBundle = parseDubBundle(location);
		dep(location, null, name, version, srcFolders).check(dubBundle);
	}
	
	
	@Test
	public void testNonExistant() throws Exception { testNonExistant$(); }
	public void testNonExistant$() throws Exception {
		DubBundle dubBundle = parseDubBundle(DToolTestResources.getTestResourcePath("dub", "nonExistent"));
		assertTrue(dubBundle.error.getCause() instanceof FileNotFoundException);
		assertExceptionContains(dubBundle.error, "FileNotFoundException");
		assertEquals(dubBundle.name, "nonExistent");
	}
	
}