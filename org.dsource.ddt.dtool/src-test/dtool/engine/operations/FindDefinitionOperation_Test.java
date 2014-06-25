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
package dtool.engine.operations;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.nio.file.Path;

import org.junit.Test;

import dtool.ast.SourceRange;
import dtool.engine.CommonSemanticModelTest.Tests_DToolServer;
import dtool.resolver.api.FindDefinitionResult;
import dtool.resolver.api.FindDefinitionResult.FindDefinitionResultEntry;
import dtool.tests.CommonDToolTest;
import dtool.tests.DToolTestResources;

public class FindDefinitionOperation_Test extends CommonDToolTest {
	
	public static final String RESOLVER2 = "resolver2";
	
	protected static Path getTestResource(String resourcePath) {
		return DToolTestResources.getInstance().getResourcesDir().toPath().
				resolve(RESOLVER2).resolve(resourcePath);
	}
	
	Path BUNDLE_FOO_SRC = getTestResource("bundleFoo/source");
	Tests_DToolServer dtoolEngine = new Tests_DToolServer();
	
	@Test
	public void test() throws Exception { test$(); }
	public void test$() throws Exception {
		testImportImplicitlyDefinedModule();
	}
	
	protected void testImportImplicitlyDefinedModule() {
		Path fooModule = BUNDLE_FOO_SRC.resolve("basic_foo.d");
		int offset = readStringFromFile(fooModule).indexOf("bar/*MARKER*/");
		
		FindDefinitionResult opResult = dtoolEngine.doFindDefinition(fooModule, offset);
		assertTrue(opResult.errorMessage == null);
		assertAreEqual(opResult.originFilePath, fooModule);
		
		assertTrue(opResult.results.size() == 1);
		FindDefinitionResultEntry nameResult = opResult.results.get(0);
		assertAreEqual(nameResult.compilationUnitPath, BUNDLE_FOO_SRC.resolve("basic_pack/bar.d"));
		assertAreEqual(nameResult.sourceRange, new SourceRange(0, 0));
	}
	
}