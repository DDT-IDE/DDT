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
package dtool.engine.analysis;

import static dtool.util.NewUtils.getSingleElementOrNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.util.concurrent.ExecutionException;

import melnorme.lang.tooling.bundles.EmptySemanticResolution;
import melnorme.lang.tooling.engine.NotAValueErrorElement;
import melnorme.lang.tooling.engine.completion.CompletionScopeLookup;
import melnorme.lang.tooling.symbols.INamedElement;
import melnorme.utilbox.misc.StringUtil;

import org.junit.Test;

import dtool.ast.definitions.DefUnit;
import dtool.ast.expressions.Expression;
import dtool.parser.DeeParsingChecks.DeeTestsChecksParser;


public abstract class DefElement_CommonTest extends CommonNodeSemanticsTest {
	
	public static final String[] COMMON_PROPERTIES = array(
		"init", "sizeof", "alignof", "mangleof", "stringof"
	);
	
	@Test
	public void test_resolveSearchInMembersScope() throws Exception { test_resolveSearchInMembersScope________(); }
	public void test_resolveSearchInMembersScope________() throws Exception {
		 
	}
	
	protected static void testResolveSearchInMembersScope(INamedElement namedElement, String... expectedResults) {
		CompletionScopeLookup search = new CompletionScopeLookup(null, 0, new EmptySemanticResolution());
		namedElement.resolveSearchInMembersScope(search);
		
		resultsChecker(search).checkResults(expectedResults);
	}
	
	/* -----------------  ----------------- */
	
	@Test
	public void test_resolveTypeForValueContext() throws Exception { test_resolveTypeForValueContext________(); }
	public void test_resolveTypeForValueContext________() throws Exception {
		 assertFail();
	}
	
	protected void test_resolveTypeForValueContext(String source, String fullName) {
		test_resolveTypeForValueContext(source, fullName, false);
	}
	
	protected void test_resolveTypeForValueContext(String source, String expectedFullName, boolean isError) {
		int offset = source.indexOf("XXX");
		offset = offset == -1 ? source.indexOf("xxx") : offset;
		assertTrue(offset != -1);
		DefUnit defElem = parseSourceAndFindNode(source, offset, DefUnit.class);
		
		INamedElement resolvedType = defElem.resolveTypeForValueContext(new EmptySemanticResolution());
		if(expectedFullName == null) {
			assertTrue(resolvedType == null);
			assertTrue(isError);
			return;
		}
		assertEquals(isError, resolvedType instanceof NotAValueErrorElement);
		String fullName = resolvedType.getFullyQualifiedName();
		fullName = StringUtil.trimStart(fullName, DEFAULT_ModuleName + ".");
		assertEquals(fullName, expectedFullName);
	}
	
	// TODO: cleanup these two methods
	protected static void testExpressionResolution(String source, String... expectedResults) throws ExecutionException {
		Expression exp = parseSourceAndFindNode(source, source.indexOf("/*X*/"), Expression.class);
		assertNotNull(exp);
		INamedElement expType = getSingleElementOrNull(exp.getSemantics().resolveTypeOfUnderlyingValue(new EmptySemanticResolution()));
		
		testResolveSearchInMembersScope(expType, expectedResults);
	}
	protected static void testExpressionResolution2(String source, String... expectedResults) {
		Expression exp = new DeeTestsChecksParser(source).parseExpression().getNode();
		INamedElement expType = getSingleElementOrNull(exp.getSemantics().resolveTypeOfUnderlyingValue(new EmptySemanticResolution()));
		
		testResolveSearchInMembersScope(expType, expectedResults);
	}
	
}