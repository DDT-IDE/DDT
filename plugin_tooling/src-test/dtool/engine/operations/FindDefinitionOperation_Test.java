/*******************************************************************************
 * Copyright (c) 2014 Bruno Medeiros and other Contributors.
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
import melnorme.lang.tooling.ast.SourceRange;
import melnorme.utilbox.misc.Location;

import org.junit.Test;

import dtool.engine.operations.FindDefinitionResult.FindDefinitionResultEntry;

public class FindDefinitionOperation_Test extends CommonDToolOperation_Test {
	
	public static final Location BASIC_FOO_FilePath = BUNDLE_FOO__SRC_FOLDER.resolve_valid("basic_foo.d");
	public static final String BASIC_FOO_Contents = readStringFromFile(BASIC_FOO_FilePath);
	
	protected FindDefinitionResult doOperation(Location filePath, int offset) throws Exception {
		return dtoolEngine.doFindDefinition(filePath.path, offset, testsDubPath());
	}
	
	@Test
	public void testALL() throws Exception { testALL$(); }
	public void testALL$() throws Exception {
		
		testFindDefinition(BASIC_FOO_FilePath, indexOf(BASIC_FOO_Contents, "/*defA_REF1*/"), 
			new FindDefinitionResultEntry(
				"defA", false, BASIC_FOO_FilePath, new SourceRange(indexOf(BASIC_FOO_Contents, "defA/*DEF*/"), 4))
		);
		
		// Test implicit source module
		testFindDefinition(BASIC_FOO_FilePath, indexOf(BASIC_FOO_Contents, "implicit_name/*MARKER*/"), 
			new FindDefinitionResultEntry(
				"basic_pack.implicit_name", false, BUNDLE_FOO__SRC_FOLDER.resolve("basic_pack/implicit_name.d"), 
				new SourceRange(0, 0))
		);
		
		// Test intrinsic
		testFindDefinition(BASIC_FOO_FilePath, indexOf(BASIC_FOO_Contents, "int/*int_ref*/"), 
			new FindDefinitionResultEntry(
				"int", true, null, null)
		);
		
		// Test error cases
		testFindDefinition(BASIC_FOO_FilePath, indexOf(BASIC_FOO_Contents, "intvar ="),
			FindDefinitionOperation.FIND_DEF_PickedElementAlreadyADefinition
		);
		testFindDefinition(BASIC_FOO_FilePath, indexOf(BASIC_FOO_Contents, "123"),
			FindDefinitionOperation.FIND_DEF_NoReferenceFoundAtCursor
		);
		testFindDefinition(BASIC_FOO_FilePath, indexOf(BASIC_FOO_Contents, "not_found"),
			FindDefinitionOperation.FIND_DEF_ReferenceResolveFailed
		);
		
	}
	
	protected void testFindDefinition(Location modulePath, int offset, FindDefinitionResultEntry... expectedResults) 
			throws Exception {
		testFindDefinition(modulePath, offset, null, expectedResults);
	}
	
	protected void testFindDefinition(Location modulePath, int offset, String errorMsg, 
			FindDefinitionResultEntry... expectedResults) throws Exception {
		FindDefinitionResult opResult = doOperation(modulePath, offset);
		
		if(opResult.errorMessage == null) {
			assertTrue(errorMsg == null);
		} else {
			assertTrue(opResult.errorMessage.startsWith(errorMsg));
			assertTrue(expectedResults.length == 0);
			return;
		}
		
		assertTrue(expectedResults.length == opResult.results.size());
		
		for (int i = 0; i < expectedResults.length; i++) {
			FindDefinitionResultEntry expected = expectedResults[i];
			FindDefinitionResultEntry nameResult = opResult.results.get(i);
			
			assertAreEqual(nameResult.extendedName, expected.extendedName);
			assertAreEqual(nameResult.modulePath, expected.modulePath);
			assertAreEqual(nameResult.sourceRange, expected.sourceRange);
			assertAreEqual(nameResult.isLanguageIntrinsic, expected.isLanguageIntrinsic);
		}
	}
	
}