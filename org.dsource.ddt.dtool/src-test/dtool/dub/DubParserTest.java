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

import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;

import com.google.gson.stream.MalformedJsonException;

import dtool.dub.DeeBundleParser.DubBundleException;
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
		
		DubRegistry dubRegistry = new DubRegistry(dubWorkspaceA);
		// TODO:
		
	}
	
	public DubBundle parseDubBundle(Path location) {
		try {
			return new DeeBundleParser().parseDubBundle(location);
		} catch (MalformedJsonException e) {
			throw assertFail();
		} catch (IOException e) {
			throw assertFail();
		} catch (DubBundleException e) {
			throw assertFail();
		}
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
	}
	
	public static Path path(String first) {
		return Paths.get(first);
	}
	
}