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

import static dtool.engine.analysis.NE_LanguageIntrinsics_SemanticsTest.PRIMITIVE_TYPES;
import static dtool.engine.analysis.NamedElement_CommonTest.COMMON_PROPERTIES;
import static melnorme.lang.tooling.EProtection.PUBLIC;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import static melnorme.utilbox.misc.ArrayUtil.concat;

import org.junit.Test;

import dtool.engine.operations.DeeSymbolCompletionResult.ECompletionResultStatus;
import dtool.engine.tests.DefUnitResultsChecker;
import melnorme.lang.tooling.CompletionProposalKind;
import melnorme.lang.tooling.EAttributeFlag;
import melnorme.lang.tooling.EProtection;
import melnorme.lang.tooling.ElementAttributes;
import melnorme.lang.tooling.ToolCompletionProposal;
import melnorme.lang.tooling.ast.SourceRange;
import melnorme.lang.tooling.completion.LangToolCompletionProposal;
import melnorme.utilbox.collections.ArrayList2;
import melnorme.utilbox.collections.Indexable;
import melnorme.utilbox.misc.Location;

public class CompletionOperation_Test extends CommonDToolOperation_Test {
	
	protected DeeSymbolCompletionResult doOperation(Location filePath, int offset) throws Exception {
		return dtoolEngine.doCodeCompletion(filePath.path, offset, null, testsDubPath());
	}
	
	public static final Location MODULE_FilePath = BUNDLE_FOO__SRC_FOLDER.resolve_valid("completion_test.d");
	public static final String MODULE_Contents = readStringFromFile(MODULE_FilePath);
	
	protected final String[] COMPLETION_TEST_MEMBERS = array("Foo", "bar", "abc1", "abc2");
	protected final String[] FOO_MEMBERS = concat(COMMON_PROPERTIES, "xx1", "xx2", "intOther", "inzzz");
	
	@Test
	public void testBasic() throws Exception { testBasic$(); }
	public void testBasic$() throws Exception {
		testCodeCompletion(MODULE_FilePath, indexOf(MODULE_Contents, "/*CC1*/"), 0,
			"abc1", "abc2"
		);
		testCodeCompletion(MODULE_FilePath, indexOf(MODULE_Contents, "abc/*CC1*/"), 3,
			concat(PRIMITIVE_TYPES, COMPLETION_TEST_MEMBERS)
		);
		
		
		// Boundary condition, offset = 0 && offset = length
		testCodeCompletion(MODULE_FilePath, 0, 0,
			concat(PRIMITIVE_TYPES, COMPLETION_TEST_MEMBERS)
		);
		testCodeCompletion(MODULE_FilePath, MODULE_Contents.length(), 0,
			concat(PRIMITIVE_TYPES, COMPLETION_TEST_MEMBERS)
		);
		testCodeCompletion(BUNDLE_FOO__SRC_FOLDER.resolve("empty_module.d"), 0, 0,
			concat(PRIMITIVE_TYPES, "empty_module")
		);
		
		
		/* -----------------  ----------------- */
		
		// Test qualified ref
		testCodeCompletion(MODULE_FilePath, indexOf(MODULE_Contents, "/*CC2*/"), 0,
			"xx1", "xx2"
		);
		testCodeCompletion(MODULE_FilePath, indexOf(MODULE_Contents, "xx/*CC2*/"), 2,
			FOO_MEMBERS
		);
		
		// Test qualified ref - odd case before dot
		testCodeCompletion(MODULE_FilePath, indexOf(MODULE_Contents, "/*CC_beforeDot*/"), 0,
			concat(PRIMITIVE_TYPES, COMPLETION_TEST_MEMBERS)
		);
		testCodeCompletion(MODULE_FilePath, indexOf(MODULE_Contents, ". /*CC_afterDot*/"), 0,
			concat(PRIMITIVE_TYPES, COMPLETION_TEST_MEMBERS)
		);
		// Test qualified ref - odd case after dot
		testCodeCompletion(MODULE_FilePath, indexOf(MODULE_Contents, "/*CC_afterDot*/"), 0,
			FOO_MEMBERS
		);
		testCodeCompletion(MODULE_FilePath, indexOf(MODULE_Contents, " /*CC_afterDot*/"), 0,
			FOO_MEMBERS
		);
		
		
		// Test qualified ref - missing qualifier
		testCodeCompletion(MODULE_FilePath, indexOf(MODULE_Contents, "/*CC_afterDot2*/"), 0,
			FOO_MEMBERS
		);
		
		// Test completion under primitive
		testCodeCompletion(MODULE_FilePath, indexOf(MODULE_Contents, "/*CC_keywords_1*/"), 0,
			"int", "intVar"
		);
		testCodeCompletion(MODULE_FilePath, indexOf(MODULE_Contents, "t/*CC_keywords_1*/"), 1,
			"int", "intVar", "incredible"
		);
		testCodeCompletion(MODULE_FilePath, indexOf(MODULE_Contents, "nt/*CC_keywords_1*/"), 2,
			"int", "intVar", "incredible", "ifloat", "idouble", "ireal"
		);
		testCodeCompletion(MODULE_FilePath, indexOf(MODULE_Contents, "int/*CC_keywords_1*/"), 3,
			concat(PRIMITIVE_TYPES, concat(COMPLETION_TEST_MEMBERS, "intVar", "incredible"))
		);
		
		// Test completion under keyword
		testCodeCompletion(MODULE_FilePath, indexOf(MODULE_Contents, "/*CC_keywords_2*/"), 0,
			"int", "intVar", "incredible"
		);
		testCodeCompletion(MODULE_FilePath, indexOf(MODULE_Contents, "in/*CC_keywords_2*/"), 2,
			concat(PRIMITIVE_TYPES, concat(COMPLETION_TEST_MEMBERS, "intVar", "incredible"))
		);
		
		
		// Test completion under primitive - in qualified
		testCodeCompletion(MODULE_FilePath, indexOf(MODULE_Contents, "/*CC_keywords_q1*/"), 0,
			"intOther"
		);
		testCodeCompletion(MODULE_FilePath, indexOf(MODULE_Contents, "int/*CC_keywords_q1*/"), 3,
			FOO_MEMBERS
		);
		// Test completion under keyword - in qualified
		testCodeCompletion(MODULE_FilePath, indexOf(MODULE_Contents, "/*CC_keywords_q2*/"), 0,
			"intOther", "inzzz", "init"
		);
		testCodeCompletion(MODULE_FilePath, indexOf(MODULE_Contents, "in/*CC_keywords_q2*/"), 2,
			FOO_MEMBERS
		);
		
	}
	
	protected void testCodeCompletion(Location modulePath, int offset, int replaceLength, String... expectedResults) 
			throws Exception {
		testCodeCompletion(modulePath, offset, ECompletionResultStatus.RESULT_OK, replaceLength, expectedResults);
	}
	
	protected void testCodeCompletion(Location modulePath, int offset,  
			ECompletionResultStatus resultStatus, int replaceLength, String... expectedResults) throws Exception {
		DeeSymbolCompletionResult opResult = doOperation(modulePath, offset);
		
		assertTrue(opResult.resultCode == resultStatus);
		assertTrue(opResult.getReplaceLength() == replaceLength);
		
		DefUnitResultsChecker checker = new DefUnitResultsChecker(opResult.results);
		checker.removeIgnoredElements(true, false, false);
		checker.removeStdLibObjectDefUnits();
		checker.checkResults(expectedResults);
	}
	
	
	@Test
	public void testNameMatching() throws Exception { testNameMatching$(); }
	public void testNameMatching$() throws Exception {
		// Test case insensitive matching
		testCodeCompletion(MODULE_FilePath, indexOf(MODULE_Contents, "/*CC1-b*/"), 0,
			"abc1", "abc2"
		);
		testCodeCompletion(MODULE_FilePath, indexOf(MODULE_Contents, "/*CC1-c*/"), 0,
			"abc1", "abc2"
		);
		
	}
	
	/* -----------------  ----------------- */
	
	public static ElementAttributes att(EAttributeFlag... flags) {
		return new ElementAttributes(null, flags);
	}
	
	public static ElementAttributes att(EProtection protection, EAttributeFlag... flags) {
		return new ElementAttributes(protection, flags);
	}
	
	@Test
	public void testCompletionProposals() throws Exception { testCompletionProposals$(); }
	public void testCompletionProposals$() throws Exception {
		
		final Location MODULE_FilePath = BUNDLE_FOO__SRC_FOLDER.resolve_valid("completion_test2.d");
		final String MODULE_Contents = readStringFromFile(MODULE_FilePath);
		
		// Test overload and fullReplaceString/subElements
		
		int offset;
		
		offset = MODULE_Contents.indexOf("/*N*/");
		testCodeCompletionProposals(MODULE_FilePath, offset,
			list(
				proposal(offset-4, 4, "void", "void", 
					CompletionProposalKind.NATIVE, att(), null, "void")
			)
		);
		
		offset = MODULE_Contents.indexOf("/*CC_1*/");
		testCodeCompletionProposals(MODULE_FilePath, offset,
			list(
				proposal(offset-3, 3, "foo", "foo() : void", 
					CompletionProposalKind.FUNCTION, att(PUBLIC), "_dummy2", "foo()"),
				proposal(offset-3, 3, "foo", "foo(int a) : void", 
					CompletionProposalKind.FUNCTION, att(PUBLIC), "_dummy2", "foo(a)", sr(4, 1)),
				proposal(offset-3, 3, "foo", "foo(int a, string str) : void", 
					CompletionProposalKind.FUNCTION, att(PUBLIC), "_dummy2", "foo(a, str)", sr(4, 1), sr(7, 3)),
				proposal(offset-3, 3, "fooTemplateFn", "fooTemplateFn(T) (T param) : int", 
					CompletionProposalKind.FUNCTION, att(PUBLIC, EAttributeFlag.TEMPLATED), "_dummy2", 
					"fooTemplateFn(param)", sr(14, 5))
			)
		);
		
	}
	
	protected SourceRange sr(int offset, int length) {
		return new SourceRange(offset, length);
	}
	
	public ToolCompletionProposal proposal(int replaceOffset, int replaceLength, String replaceString, String label,
			CompletionProposalKind kind, ElementAttributes attributes, String moduleName, 
			String fullReplaceString, SourceRange... sourceSubElements) {
		return new ToolCompletionProposal(replaceOffset, replaceLength, replaceString, label, kind, attributes, 
			null, moduleName, null, fullReplaceString, new ArrayList2<>(sourceSubElements), null) {
			
			@Override
			protected boolean subclassEquals(LangToolCompletionProposal _other) {
				return true; // Hack because we don't want to check namedElement
			}
		};
	}
	
	protected void testCodeCompletionProposals(Location modulePath, int offset, 
			Indexable<ToolCompletionProposal> expectedResult) 
			throws Exception {
		DeeSymbolCompletionResult opResult = doOperation(modulePath, offset);
		
		ArrayList2<ToolCompletionProposal> langResult = opResult.convertToCompletionResult();
		assertAreEqual(expectedResult, langResult);
	}
	
}