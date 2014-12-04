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
import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.util.concurrent.ExecutionException;

import melnorme.lang.tooling.context.EmptySemanticResolution;
import melnorme.lang.tooling.engine.NotAValueErrorElement;
import melnorme.lang.tooling.engine.completion.CompletionScopeLookup;
import melnorme.lang.tooling.symbols.INamedElement;
import melnorme.utilbox.misc.ArrayUtil;
import melnorme.utilbox.misc.StringUtil;

import org.junit.Test;

import dtool.ast.definitions.DefUnit;
import dtool.ast.expressions.Expression;

/* FIXME: merge with NamedElements test*/
/**
 * A base test for a {@link INamedElement}s. 
 * Each subclass should reimplement each test method as appropriate (even if there is nothing to test).
 */
public abstract class NamedElement_CommonTest extends CommonNodeSemanticsTest {
	
	protected INamedElement parseNamedElement(String source) {
		int index = source.indexOf("xxx");
		if(index == -1) {
			index = source.indexOf("XXX");
		}
		if(index == -1) {
			index = source.indexOf("Foo");
		}
		assertTrue(index != -1);
		return parseSourceAndFindNode(source, index, INamedElement.class);
	}
	
	/* -----------------  ----------------- */
	
	@Test
	public void test_resolveConcreteElement() throws Exception { test_resolveConcreteElement________(); }
	public void test_resolveConcreteElement________() throws Exception { } /* FIXME: make abstract */
	
	/* -----------------  ----------------- */
	
	@Test
	public void test_resolveTypeForValueContext() throws Exception { test_resolveTypeForValueContext________(); }
	public abstract void test_resolveTypeForValueContext________() throws Exception;
	
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
	
	protected static void testExpressionResolution(String source, String... expectedResults) 
			throws ExecutionException {
		Expression exp = parseSourceAndFindNode(source, source.indexOf("/*X*/"), Expression.class);
		testExpressionResolution_(exp, expectedResults);
	}
	protected static void testExpressionResolution_(Expression exp, String... expectedResults) {
		assertNotNull(exp);
		EmptySemanticResolution context = new EmptySemanticResolution();
		INamedElement expType = getSingleElementOrNull(exp.getSemantics(context).resolveTypeOfUnderlyingValue());
		
		testResolveSearchInMembersScope(expType, expectedResults);
	}
	
	/* -----------------  ----------------- */
	
	@Test
	public void test_resolveSearchInMembersScope() throws Exception { test_resolveSearchInMembersScope________(); }
	public abstract void test_resolveSearchInMembersScope________() throws Exception;
	
	public static final String[] COMMON_PROPERTIES = array(
		"init", "sizeof", "alignof", "mangleof", "stringof"
	);
	
	protected static void testResolveSearchInMembersScope(INamedElement namedElement, String... expectedResults) {
		CompletionScopeLookup search = new CompletionScopeLookup(null, 0, new EmptySemanticResolution());
		namedElement.resolveSearchInMembersScope(search);
		
		resultsChecker(search).checkResults(expectedResults);
	}
	
	protected static void testResolveSearchInMembersScope(INamedElement namedElement, String[] properties, 
			String... expectedResults) {
		if(properties != null) {
			expectedResults = ArrayUtil.concat(expectedResults, properties);
		}
		testResolveSearchInMembersScope(namedElement, expectedResults);
	}
	
}