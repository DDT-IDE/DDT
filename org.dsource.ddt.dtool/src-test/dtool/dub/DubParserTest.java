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
import java.nio.file.Paths;

import org.junit.Test;

import dtool.tests.DToolBaseTest;
import dtool.tests.DToolTestResources;

public class DubParserTest extends DToolBaseTest {

	@Test
	public void testBasic() throws Exception { testBasic$(); }
	public void testBasic$() {
		
		Path dubWorkspaceA = DToolTestResources.getTestResourcePath("dub", "workspaceA");
		
		testBundle(dubWorkspaceA.resolve("XptoBundle"), "xptobundle", null, 
				array(path("src"), path("src-test")));
		
		testBundle(dubWorkspaceA.resolve("bar_lib"), 
				"bar_lib", null, array(path("source")));

		testBundle(dubWorkspaceA.resolve("foo_lib"), 
				"foo_lib", null, array(path("source")));
		
		testBundle(dubWorkspaceA.resolve("LenientJsonA"), 
				"lenient-json1", null, array(path("src"), path("src-test")));
		
	}
	
	public DubBundle parseDubBundle(Path location) {
		return DubBundleParser.parseDubBundleFromLocation(location);
	}
	
	public void testBundle(Path location, String name, String version, Path[] srcFolders) {
		checkBundle(parseDubBundle(location), location, name, version, srcFolders);
	}
	
	public static void checkBundle(DubBundle bundle, Path location, String name, String version, Path[] srcFolders) {
		assertAreEqual(bundle.location, location);
		assertAreEqual(bundle.name, name);
		assertAreEqual(bundle.version, version);
		assertEqualArrays(bundle.getEffectiveSourceFolders(), srcFolders);
		assertEqualArrays(bundle.dependencies, null); // TODO
		assertAreEqual(bundle.error, null);
	}
	
	public static Path path(String first) {
		return Paths.get(first);
	}
	
	@Test
	public void testNonExistant() throws Exception { testNonExistant$(); }
	public void testNonExistant$() throws Exception {
		DubBundle dubBundle = parseDubBundle(DToolTestResources.getTestResourcePath("dub", "nonExistent"));
		assertTrue(dubBundle.error.getCause() instanceof FileNotFoundException);
		assertEquals(dubBundle.name, "nonExistent");
	}
	
}