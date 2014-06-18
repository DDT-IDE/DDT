/*******************************************************************************
 * Copyright (c) 2014, 2014 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package mmrnmhrm.core.engine_client;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.nio.file.Path;

import melnorme.lang.ide.core.tests.CommonCoreTest;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.compiler.env.ModuleSource;
import org.junit.Test;

import dtool.resolver.DefUnitResultsChecker;
import dtool.resolver.PrefixDefUnitSearch;
import dtool.tests.DToolTestResources;

public class DToolClient_Test extends CommonCoreTest {
	
	protected DToolClient client;
	
	@Test
	public void testBasic() throws Exception { testBasic$(); }
	public void testBasic$() throws Exception {
		
		client = new DToolClient();
		
		ModuleSource moduleSource = new ModuleSource("relative/path/foo.d", "module blah;");
		
		assertEquals(client.getParsedModuleOrNull(moduleSource).module.getName(), "blah");
		
		assertEquals(client.getExistingModuleNodeOrNull(moduleSource).getName(), "blah");
		assertEquals(client.getExistingParsedModuleOrNull(moduleSource).module.getName(), "blah");
		
		
		testCodeCompletion(moduleSource, 0, 
			"blah");
		testCodeCompletion(new ModuleSource("relative/path/foo.d", "module xpto;"), 0, 
			"xpto");
		
		Path path = DToolTestResources.getTestResourcePath("dummy__non_existant.d");
		assertTrue(path.isAbsolute());
		testCodeCompletion(new ModuleSource(path.toString(), "module blah;"), 0, 
				"blah");
		
		// Error case
		try {
			client.doCodeCompletion_Do(null, 0, null);
			assertFail();
		} catch (CoreException e) {
		}
		
	}
	
	protected void testCodeCompletion(ModuleSource moduleSource, int offset, String... results) throws CoreException {
		PrefixDefUnitSearch cc = client.doCodeCompletion(moduleSource, offset);
		new DefUnitResultsChecker(cc.getResults()).simpleCheckResults(results);
	}
	
}