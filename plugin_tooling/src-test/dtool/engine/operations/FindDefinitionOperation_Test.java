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
import dtool.engine.CommonSemanticManagerTest.Tests_DToolServer;
import dtool.resolver.api.FindDefinitionResult;
import dtool.resolver.api.FindDefinitionResult.FindDefinitionResultEntry;

public class FindDefinitionOperation_Test extends Resolver2Tests {
	
	public static final Path BUNDLE_FOO__SRC_FOLDER = getTestResource("bundleFoo/source");
	public static final Path BASIC_FOO_FilePath = BUNDLE_FOO__SRC_FOLDER.resolve("basic_foo.d");
	public static final String BASIC_FOO_Contents = readStringFromFile(BASIC_FOO_FilePath);
	
	protected Tests_DToolServer dtoolEngine;
	
	protected void prepEngineServer() {
		dtoolEngine = new Tests_DToolServer();
	}
	
	protected FindDefinitionResult doOperation(Path filePath, int offset) {
		return dtoolEngine.doFindDefinition(filePath, offset);
	}
	
	@Test
	public void testALL() throws Exception { testALL$(); }
	public void testALL$() throws Exception {
		prepEngineServer();
		
		testFindDefinition(BASIC_FOO_FilePath, BASIC_FOO_Contents.indexOf("/*defA_REF1*/"), 
			new FindDefinitionResultEntry(
				BASIC_FOO_FilePath, new SourceRange(BASIC_FOO_Contents.indexOf("defA/*DEF*/"), 4), "defA", false)
		);
		
		testFindDefinition(BASIC_FOO_FilePath, BASIC_FOO_Contents.indexOf("bar/*MARKER*/"), 
			new FindDefinitionResultEntry(
				BUNDLE_FOO__SRC_FOLDER.resolve("basic_pack/bar.d"), new SourceRange(0, 0), "bar", false)
		);
	}
	
	protected void testFindDefinition(Path modulePath, int offset, 
			FindDefinitionResultEntry... expectedResults) {
		FindDefinitionResult opResult = doOperation(modulePath, offset);
		assertTrue(opResult.errorMessage == null);
		
		for (int i = 0; i < expectedResults.length; i++) {
			FindDefinitionResultEntry expected = expectedResults[i];
			FindDefinitionResultEntry nameResult = opResult.results.get(i);
			
			assertAreEqual(nameResult.extendedName, expected.extendedName);
			assertAreEqual(nameResult.compilationUnitPath, expected.compilationUnitPath);
			assertAreEqual(nameResult.sourceRange, expected.sourceRange);
			assertAreEqual(nameResult.isLanguageIntrinsic, expected.isLanguageIntrinsic);
		}
	}
	
}