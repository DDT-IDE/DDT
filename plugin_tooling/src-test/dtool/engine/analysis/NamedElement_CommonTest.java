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
import static melnorme.utilbox.misc.StringUtil.emptyAsNull;

import java.util.concurrent.ExecutionException;

import melnorme.lang.tooling.ast.util.NodeElementUtil;
import melnorme.lang.tooling.context.EmptySemanticResolution;
import melnorme.lang.tooling.context.ISemanticContext;
import melnorme.lang.tooling.engine.ErrorElement;
import melnorme.lang.tooling.engine.PickedElement;
import melnorme.lang.tooling.engine.completion.CompletionScopeLookup;
import melnorme.lang.tooling.engine.resolver.NamedElementSemantics;
import melnorme.lang.tooling.symbols.IConcreteNamedElement;
import melnorme.lang.tooling.symbols.INamedElement;
import melnorme.utilbox.misc.ArrayUtil;
import melnorme.utilbox.misc.StringUtil;

import org.junit.Test;

import dtool.ast.definitions.EArcheType;
import dtool.ast.expressions.Expression;

/**
 * A base test for a {@link INamedElement}s. 
 * Each subclass should reimplement each test method as appropriate (even if there is nothing to test).
 */
public abstract class NamedElement_CommonTest extends CommonNodeSemanticsTest {
	
	protected INamedElement parseNamedElement_(String source) {
		return parseSourceAndFindNode(source, getMarkerIndex(source), INamedElement.class);
	}
	
	protected PickedElement<INamedElement> parseNamedElement(String source) {
		return parseElement(source, getMarkerIndex(source), INamedElement.class);
	}
	
	protected int getMarkerIndex(String source) {
		int index = source.indexOf("xxx");
		if(index == -1) {
			index = source.indexOf("XXX");
		}
		if(index == -1) {
			index = source.indexOf("Foo");
		}
		assertTrue(index != -1);
		return index;
	}
	
	/* -----------------  ----------------- */
	
	@Test
	public void test_resolveElement() throws Exception { test_resolveElement________(); }
	public abstract void test_resolveElement________() throws Exception;
	
	
	public static void test_resolveElement(PickedElement<? extends INamedElement> pickedElement, String aliasTarget,
			String expectedTypeName, boolean isError) {
		final INamedElement namedElement = pickedElement.element;
		
		assertTrue(namedElement.isLanguageIntrinsic() || namedElement.getModulePath() != null);
		
		if(emptyAsNull(namedElement.getModuleFullyQualifiedName()) == null) {
			assertTrue(namedElement.getParentNamespace() == null);
			assertTrue(namedElement.getModuleFullName() == null
					|| emptyAsNull(namedElement.getModuleFullName().getFullNameAsString()) == null);
		} else {
			if(namedElement.getModuleFullyQualifiedName().equals(namedElement.getFullyQualifiedName())) {
				assertTrue(namedElement.getParentNamespace() == null);
			} else {
				assertTrue(namedElement.getParentNamespace() != null);
			}
			
			/* FIXME: refactor getModuleFullName */
			assertAreEqual(namedElement.getModuleFullName().getFullNameAsString(), 
				namedElement.getModuleFullyQualifiedName());
		}
		
		test_resolveConcreteElement(pickedElement, aliasTarget);
		test_resolveTypeForValueContext(pickedElement, expectedTypeName, isError);
	}
	
	public static void test_resolveElement_Concrete(PickedElement<? extends INamedElement> pickedElement, 
			String expectedTypeName, boolean isError) {
		test_resolveElement(pickedElement, null, expectedTypeName, isError);
	}
	
	
	protected static void test_resolveConcreteElement(PickedElement<? extends INamedElement> pickedElement, 
			String aliasTarget) {
		final ISemanticContext context = pickedElement.context;
		final INamedElement namedElement = pickedElement.element;
		
		assertTrue(context == context.findSemanticContext(namedElement));
		
		checkIsSameResolution(
			namedElement.getSemantics(context).resolveConcreteElement(),
			namedElement.getSemantics(context).resolveConcreteElement()
		);
		
		NamedElementSemantics semantics = namedElement.getSemantics(context);
		IConcreteNamedElement concreteElement = semantics.resolveConcreteElement().result;
		
		if(concreteElement instanceof ErrorElement) {
			ErrorElement notFoundError = (ErrorElement) concreteElement;
			assertTrue(notFoundError.getModulePath() == namedElement.getModulePath());
			assertTrue(notFoundError.getParent() != null);
			assertTrue(NodeElementUtil.isContainedIn(notFoundError.getParent(), namedElement));
			assertTrue(notFoundError.getParentNamespace() == null);
		}
		
		if(aliasTarget == null) {
			// non-alias elements relsolve to themselves
			assertTrue(concreteElement == namedElement);
		} else {
			assertTrue(concreteElement != null);
			assertTrue(concreteElement.getName().equals(aliasTarget));
		}
	}
	
	public static void test_resolveTypeForValueContext(PickedElement<? extends INamedElement> pickedElement, 
			String expectedTypeName, boolean isError) {
		INamedElement namedElement = pickedElement.element;
		pickedElement.internal_resetSemanticResolutions();
		
		INamedElement resolvedType = namedElement.resolveTypeForValueContext(pickedElement.context);
		
		// Test caching
		assertTrue(resolvedType == namedElement.resolveTypeForValueContext(pickedElement.context)); 
		
		assertEquals(isError, resolvedType.getArcheType() == EArcheType.Error);
		// TODO: test that archetype is a type?
		String type_modulefullName = resolvedType.getFullyQualifiedName();
		type_modulefullName = StringUtil.trimStart(type_modulefullName, DEFAULT_ModuleName + ".");
		assertEquals(type_modulefullName, expectedTypeName);
	}
	
	/* -----------------  ----------------- */
	
	@Test
	public void test_resolveSearchInMembersScope() throws Exception { test_resolveSearchInMembersScope________(); }
	public abstract void test_resolveSearchInMembersScope________() throws Exception;
	
	public static final String[] COMMON_PROPERTIES = array(
		"init", "sizeof", "alignof", "mangleof", "stringof"
	);
	
	protected static void test_resolveSearchInMembersScope(PickedElement<? extends INamedElement> pickedElement, 
			String... expectedResults) {
		CompletionScopeLookup search = new CompletionScopeLookup(0, pickedElement.context, "");
		search.evaluateInMembersScope(pickedElement.element);
		
		resultsChecker(search).checkResults(expectedResults);
	}
	
	protected static void test_resolveSearchInMembersScope(PickedElement<? extends INamedElement> pickedElement, 
			String[] properties, String... expectedResults) {
		if(properties != null) {
			expectedResults = ArrayUtil.concat(expectedResults, properties);
		}
		test_resolveSearchInMembersScope(pickedElement, expectedResults);
	}
	
	protected static void testExpressionResolution(String source, String... expectedResults) 
			throws ExecutionException {
		Expression exp = parseSourceAndFindNode(source, source.indexOf("/*X*/"), Expression.class);
		assertNotNull(exp);
		testExpressionResolution_(exp, expectedResults);
	}
	protected static void testExpressionResolution_(Expression exp, String... expectedResults) {
		EmptySemanticResolution context = new EmptySemanticResolution();
		INamedElement expType = getSingleElementOrNull(exp.getSemantics(context).resolveTypeOfUnderlyingValue());
		
		ISemanticContext context2 = expType.isLanguageIntrinsic() ? context.getStdLibResolution() : context;
		test_resolveSearchInMembersScope(picked(expType, context2), expectedResults);
	}
	
}