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
import melnorme.lang.tooling.ast_actual.ILangNamedElement;
import melnorme.utilbox.misc.StringUtil;

import org.junit.Test;

import dtool.ast.definitions.DefUnit;
import dtool.ast.expressions.Expression;
import dtool.engine.common.NotAValueErrorElement;
import dtool.engine.modules.NullModuleResolver;
import dtool.parser.DeeParsingChecks.DeeTestsChecksParser;
import dtool.resolver.DefUnitResultsChecker;
import dtool.resolver.PrefixDefUnitSearch;


public abstract class DefElement_CommonTest extends CommonNodeSemanticsTest {
	
	public static final String[] COMMON_PROPERTIES = array(
		"init", "sizeof", "alignof", "mangleof", "stringof"
	);
	
	@Test
	public void test_resolveSearchInMembersScope() throws Exception { test_resolveSearchInMembersScope________(); }
	public void test_resolveSearchInMembersScope________() throws Exception {
		 
	}
	
	protected static void testResolveSearchInMembersScope(ILangNamedElement namedElement, String... expectedResults) {
		PrefixDefUnitSearch search = new PrefixDefUnitSearch(null, 0, new NullModuleResolver());
		namedElement.resolveSearchInMembersScope(search);
		
		DefUnitResultsChecker resultsChecker = new DefUnitResultsChecker(search.getMatchedElements());
		resultsChecker.removeIgnoredDefUnits(true, true);
		resultsChecker.checkResults(expectedResults);
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
		DefUnit defElem = parseSourceAndPickNode(source, offset, DefUnit.class);
		
		ILangNamedElement resolvedType = defElem.resolveTypeForValueContext(new NullModuleResolver());
		if(expectedFullName == null) {
			assertTrue(resolvedType == null);
			assertTrue(isError);
			return;
		}
		assertEquals(isError, resolvedType instanceof NotAValueErrorElement);
		String fullName = resolvedType.getFullyQualifiedName();
		fullName = StringUtil.trimStart(fullName, DEFAULT_MODULE + ".");
		assertEquals(fullName, expectedFullName);
	}
	
	// TODO: cleanup these two methods
	protected static void testExpressionResolution(String source, String... expectedResults) {
		Expression exp = parseSourceAndPickNode(source, source.indexOf("/*X*/"), Expression.class);
		assertNotNull(exp);
		ILangNamedElement expType = getSingleElementOrNull(exp.resolveTypeOfUnderlyingValue(new NullModuleResolver()));
		
		testResolveSearchInMembersScope(expType, expectedResults);
	}
	protected static void testExpressionResolution2(String source, String... expectedResults) {
		Expression exp = new DeeTestsChecksParser(source).parseExpression().getNode();
		ILangNamedElement expType = getSingleElementOrNull(exp.resolveTypeOfUnderlyingValue(new NullModuleResolver()));
		
		testResolveSearchInMembersScope(expType, expectedResults);
	}
	
}