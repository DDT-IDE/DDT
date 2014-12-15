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

import static dtool.engine.analysis.NamedElement_CommonTest.COMMON_PROPERTIES;
import static dtool.engine.analysis.NE_LanguageIntrinsics_SemanticsTest.PRIMITIVE_TYPES;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import static melnorme.utilbox.misc.ArrayUtil.concat;

import java.nio.file.Path;

import melnorme.lang.tooling.engine.completion.CompletionSearchResult;
import melnorme.lang.tooling.engine.completion.CompletionSearchResult.ECompletionResultStatus;

import org.junit.Test;

import dtool.resolver.DefUnitResultsChecker;

public class CompletionOperation_Test extends CommonDToolOperation_Test {
	
	protected CompletionSearchResult doOperation(Path filePath, int offset) throws Exception {
		return dtoolEngine.doCodeCompletion(filePath, offset, null, testsDubPath());
	}
	
	public static final Path MODULE_FilePath = BUNDLE_FOO__SRC_FOLDER.resolve("completion_test.d");
	public static final String MODULE_Contents = readStringFromFile(MODULE_FilePath);
	
	protected final String[] COMPLETION_TEST_MEMBERS = array("Foo", "bar", "abc1", "abc2");
	protected final String[] FOO_MEMBERS = concat(COMMON_PROPERTIES, "xx1", "xx2", "intOther", "inzzz");
	
	@Test
	public void testBasic() throws Exception { testBasic$(); }
	public void testBasic$() throws Exception {
		testFindDefinition(MODULE_FilePath, indexOf(MODULE_Contents, "/*CC1*/"), 0,
			"abc1", "abc2"
		);
		testFindDefinition(MODULE_FilePath, indexOf(MODULE_Contents, "abc/*CC1*/"), 3,
			concat(PRIMITIVE_TYPES, COMPLETION_TEST_MEMBERS)
		);
		
		
		// Boundary condition, offset = 0 && offset = length
		testFindDefinition(MODULE_FilePath, 0, 0,
			concat(PRIMITIVE_TYPES, COMPLETION_TEST_MEMBERS)
		);
		testFindDefinition(MODULE_FilePath, MODULE_Contents.length(), 0,
			concat(PRIMITIVE_TYPES, COMPLETION_TEST_MEMBERS)
		);
		testFindDefinition(BUNDLE_FOO__SRC_FOLDER.resolve("empty_module.d"), 0, 0,
			concat(PRIMITIVE_TYPES, "empty_module")
		);
		
		
		/* -----------------  ----------------- */
		
		// Test qualified ref
		testFindDefinition(MODULE_FilePath, indexOf(MODULE_Contents, "/*CC2*/"), 0,
			"xx1", "xx2"
		);
		testFindDefinition(MODULE_FilePath, indexOf(MODULE_Contents, "xx/*CC2*/"), 2,
			FOO_MEMBERS
		);
		
		// Test qualified ref - odd case before dot
		testFindDefinition(MODULE_FilePath, indexOf(MODULE_Contents, "/*CC_beforeDot*/"), 0,
			concat(PRIMITIVE_TYPES, COMPLETION_TEST_MEMBERS)
		);
		testFindDefinition(MODULE_FilePath, indexOf(MODULE_Contents, ". /*CC_afterDot*/"), 0,
			concat(PRIMITIVE_TYPES, COMPLETION_TEST_MEMBERS)
		);
		// Test qualified ref - odd case after dot
		testFindDefinition(MODULE_FilePath, indexOf(MODULE_Contents, "/*CC_afterDot*/"), 0,
			FOO_MEMBERS
		);
		testFindDefinition(MODULE_FilePath, indexOf(MODULE_Contents, " /*CC_afterDot*/"), 0,
			FOO_MEMBERS
		);
		
		
		// Test qualified ref - missing qualifier
		testFindDefinition(MODULE_FilePath, indexOf(MODULE_Contents, "/*CC_afterDot2*/"), 0,
			FOO_MEMBERS
		);
		
		// Test completion under primitive
		testFindDefinition(MODULE_FilePath, indexOf(MODULE_Contents, "/*CC_keywords_1*/"), 0,
			"int", "intVar"
		);
		testFindDefinition(MODULE_FilePath, indexOf(MODULE_Contents, "t/*CC_keywords_1*/"), 1,
			"int", "intVar", "incredible"
		);
		testFindDefinition(MODULE_FilePath, indexOf(MODULE_Contents, "nt/*CC_keywords_1*/"), 2,
			"int", "intVar", "incredible", "ifloat", "idouble", "ireal"
		);
		testFindDefinition(MODULE_FilePath, indexOf(MODULE_Contents, "int/*CC_keywords_1*/"), 3,
			concat(PRIMITIVE_TYPES, concat(COMPLETION_TEST_MEMBERS, "intVar", "incredible"))
		);
		
		// Test completion under keyword
		testFindDefinition(MODULE_FilePath, indexOf(MODULE_Contents, "/*CC_keywords_2*/"), 0,
			"int", "intVar", "incredible"
		);
		testFindDefinition(MODULE_FilePath, indexOf(MODULE_Contents, "in/*CC_keywords_2*/"), 2,
			concat(PRIMITIVE_TYPES, concat(COMPLETION_TEST_MEMBERS, "intVar", "incredible"))
		);
		
		
		// Test completion under primitive - in qualified
		testFindDefinition(MODULE_FilePath, indexOf(MODULE_Contents, "/*CC_keywords_q1*/"), 0,
			"intOther"
		);
		testFindDefinition(MODULE_FilePath, indexOf(MODULE_Contents, "int/*CC_keywords_q1*/"), 3,
			FOO_MEMBERS
		);
		// Test completion under keyword - in qualified
		testFindDefinition(MODULE_FilePath, indexOf(MODULE_Contents, "/*CC_keywords_q2*/"), 0,
			"intOther", "inzzz", "init"
		);
		testFindDefinition(MODULE_FilePath, indexOf(MODULE_Contents, "in/*CC_keywords_q2*/"), 2,
			FOO_MEMBERS
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
		checker.removeIgnoredDefUnits(true, false, false);
		checker.removeStdLibObjectDefUnits();
		checker.checkResults(expectedResults);
	}
	
	
	@Test
	public void testNameMatching() throws Exception { testNameMatching$(); }
	public void testNameMatching$() throws Exception {
		// Test case insensitive matching
		testFindDefinition(MODULE_FilePath, indexOf(MODULE_Contents, "/*CC1-b*/"), 0,
			"abc1", "abc2"
		);
		testFindDefinition(MODULE_FilePath, indexOf(MODULE_Contents, "/*CC1-c*/"), 0,
			"abc1", "abc2"
		);
		
	}
	
}