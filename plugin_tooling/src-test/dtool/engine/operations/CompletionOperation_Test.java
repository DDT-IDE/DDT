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

import dtool.engine.operations.CompletionSearchResult.ECompletionResultStatus;
import dtool.resolver.DefUnitResultsChecker;

public class CompletionOperation_Test extends CommonDToolOperation_Test {
	
	protected CompletionSearchResult doOperation(Path filePath, int offset) throws Exception {
		return dtoolEngine.doCodeCompletion(filePath, offset, null);
	}
	
	public static final Path MODULE_FilePath = BUNDLE_FOO__SRC_FOLDER.resolve("completion_test.d");
	public static final String MODULE_Contents = readStringFromFile(MODULE_FilePath);
	
	protected final String[] COMPLETION_TEST_MEMBERS = array("Foo", "bar", "abc1", "abc2");
	
	@Test
	public void testBasic() throws Exception { testBasic$(); }
	public void testBasic$() throws Exception {
		testFindDefinition(MODULE_FilePath, indexOf(MODULE_Contents, "/*CC1*/"), 0,
			"abc1", "abc2"
		);
		testFindDefinition(MODULE_FilePath, indexOf(MODULE_Contents, "abc/*CC1*/"), 3,
			COMPLETION_TEST_MEMBERS
		);
		
		
		// Test qualified ref
		testFindDefinition(MODULE_FilePath, indexOf(MODULE_Contents, "/*CC2*/"), 0,
			"xx1", "xx2"
		);
		testFindDefinition(MODULE_FilePath, indexOf(MODULE_Contents, "xx/*CC2*/"), 2,
			"xx1", "xx2", "other"
		);
		
		// Test qualified ref - odd case before dot
		testFindDefinition(MODULE_FilePath, indexOf(MODULE_Contents, "/*CC_beforeDot*/"), 0,
			COMPLETION_TEST_MEMBERS
		);
		testFindDefinition(MODULE_FilePath, indexOf(MODULE_Contents, ". /*CC_afterDot*/"), 0,
			COMPLETION_TEST_MEMBERS
		);
		// Test qualified ref - odd case after dot
		testFindDefinition(MODULE_FilePath, indexOf(MODULE_Contents, " /*CC_afterDot*/"), 0,
			"xx1", "xx2", "other"
		);
		testFindDefinition(MODULE_FilePath, indexOf(MODULE_Contents, " /*CC_afterDot*/"), 0,
			"xx1", "xx2", "other"
		);
		
	}
	
	protected void testFindDefinition(Path modulePath, int offset, int replaceLength, String... expectedResults) 
			throws Exception {
		testFindDefinition(modulePath, offset, ECompletionResultStatus.RESULT_OK, replaceLength, expectedResults);
	}
	
	protected void testFindDefinition(Path modulePath, int offset,  
			ECompletionResultStatus resultStatus, int replaceLength, String... expectedResults) throws Exception {
		CompletionSearchResult opResult = doOperation(modulePath, offset);
		
		assertTrue(opResult.resultCode == resultStatus);
		assertTrue(opResult.getReplaceLength() == replaceLength);
		
		DefUnitResultsChecker checker = new DefUnitResultsChecker(opResult.results);
		checker.removeIgnoredDefUnits(true, true, true);
		checker.removeStdLibObjectDefUnits();
		checker.checkResults(expectedResults);
	}
	
}