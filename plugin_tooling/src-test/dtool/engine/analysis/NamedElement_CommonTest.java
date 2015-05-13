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

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import static melnorme.utilbox.misc.StringUtil.emptyAsNull;
import melnorme.lang.tooling.context.ISemanticContext;
import melnorme.lang.tooling.engine.ErrorElement;
import melnorme.lang.tooling.engine.ErrorElement.NotAValueErrorElement;
import melnorme.lang.tooling.engine.PickedElement;
import melnorme.lang.tooling.engine.completion.CompletionScopeLookup;
import melnorme.lang.tooling.engine.resolver.INamedElementSemanticData;
import melnorme.lang.tooling.symbols.IConcreteNamedElement;
import melnorme.lang.tooling.symbols.INamedElement;
import melnorme.lang.tooling.symbols.ITypeNamedElement;

import org.junit.Test;

import dtool.ast.expressions.Expression;

/**
 * A base test for a {@link INamedElement}s. 
 * Each subclass should reimplement each test method as appropriate (even if there is nothing to test).
 */
public abstract class NamedElement_CommonTest extends CommonNodeSemanticsTest {
	
	protected final String ERROR_NotAValue = NotAValueErrorElement.ERROR_IS_NOT_A_VALUE;
	
	protected PickedElement<INamedElement> parseNamedElement(String source) {
		return parseElement(source, getMarkerIndex(source), INamedElement.class);
	}
	
	protected PickedElement<ITypeNamedElement> parseTypeElement(String source) {
		return parseElement(source, getMarkerIndex(source), ITypeNamedElement.class);
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
	
	protected static String expectNotAValue(INamedElement namedElement) {
		return expectNotAValue(namedElement.getExtendedName());
	}
	
	protected static PickedElement<INamedElement> picked2(INamedElement namedElement, ISemanticContext context) {
		return picked(namedElement, namedElement.getElementSemanticContext(context));
	}
	
	/* -----------------  ----------------- */
	
	@Test
	public void test_NamedElement() throws Exception { test_NamedElement________(); }
	public abstract void test_NamedElement________() throws Exception;
	
	
	public static void test_NamedElement(PickedElement<? extends INamedElement> pickedElement, String aliasTarget,
			String expectedTypeOfValue, String[] expectedMembers) {
		final INamedElement namedElement = pickedElement.element;
		
		// TODO: might want to review this code
		assertTrue(
			namedElement.isBuiltinElement() 
			|| namedElement.getSemanticContainerKey() != null
			|| namedElement.getElementSemanticContext(null) != null
			|| namedElement.getSemantics(null) != null);
		
		if(emptyAsNull(namedElement.getModuleFullName()) == null) {
			assertTrue(namedElement.getParentNamespace() == null);
			assertTrue(namedElement.getModuleFullName() == null);
		} else {
			if(namedElement.getModuleFullName().equals(namedElement.getFullyQualifiedName())) {
				assertTrue(namedElement.getParentNamespace() == null);
			} else {
				assertTrue(namedElement.getParentNamespace() != null);
			}
		}
		
		test_resolveConcreteElement(pickedElement, aliasTarget);
		test_resolveTypeForValueContext(pickedElement, expectedTypeOfValue);
		
		if(expectedMembers != null) {
			test_resolveSearchInMembersScope(pickedElement, expectedMembers);
		}
	}
	
	public static void test_NamedElement_Alias(PickedElement<? extends INamedElement> pickedElement, 
			String aliasTarget, String expectedTypeOfValue, String[] expectedMembers) {
		test_NamedElement(pickedElement, aliasTarget, expectedTypeOfValue, expectedMembers);
	}
	
	public static void test_NamedElement_Concrete(PickedElement<? extends INamedElement> pickedElement, 
			String expectedTypeName, String[] expectedMembers) {
		test_NamedElement(pickedElement, null, expectedTypeName, expectedMembers);
	}
	
	public static void test_NamedElement_Type(PickedElement<? extends ITypeNamedElement> pickedElement, 
			String[] expectedMembers) {
		test_NamedElement_NonValue(pickedElement, null, expectedMembers);
	}
	
	public static void test_NamedElement_NonValue(PickedElement<? extends INamedElement> pickedElement, 
			String aliasTarget, String[] expectedMembers) {
		test_NamedElement(pickedElement, aliasTarget, expectNotAValue(pickedElement.element.getName()), 
			expectedMembers);
	}
	
	protected static void test_resolveConcreteElement(PickedElement<? extends INamedElement> pickedElement, 
			String concreteTargetLabel) {
		final ISemanticContext context = pickedElement.context;
		final INamedElement namedElement = pickedElement.element;
		
		assertTrue(context == namedElement.getElementSemanticContext(context));
		
		checkIsSameResolution(
			namedElement.getSemantics(context).resolveConcreteElement(),
			namedElement.getSemantics(context).resolveConcreteElement()
		);
		
		INamedElementSemanticData semantics = namedElement.getSemantics(context);
		IConcreteNamedElement concreteElement = semantics.resolveConcreteElement().result;
		
		if(concreteElement instanceof ErrorElement) {
			ErrorElement notFoundError = (ErrorElement) concreteElement;
			assertTrue(notFoundError.getSemanticContainerKey() == namedElement.getSemanticContainerKey());
			assertTrue(notFoundError.getOwnerElement() != null);
			assertTrue(notFoundError.getLexicalParent() == null);
			assertTrue(notFoundError.getParentNamespace() == null);
		}
		
		if(concreteTargetLabel == null) {
			// non-alias elements relsolve to themselves
			assertTrue(concreteElement == namedElement);
		} else {
			checkElementLabel(concreteElement, concreteTargetLabel);
		}
	}
	
	public static void test_resolveTypeForValueContext(PickedElement<? extends INamedElement> pickedElement, 
			String expectedTypeName) {
		INamedElement namedElement = pickedElement.element;
		pickedElement.context._resetSemantics();
		
		INamedElement resolvedType = namedElement.getSemantics(pickedElement.context).getTypeForValueContext();
		
		// Test caching
		assertTrue(resolvedType == namedElement.getSemantics(pickedElement.context).getTypeForValueContext()); 
		
		checkElementLabel(resolvedType, expectedTypeName);
	}
	
	/* -----------------  ----------------- */
	
	public static final String[] NO_MEMBERS = strings();
	
	public static final String[] COMMON_PROPERTIES = array(
		"init", "sizeof", "alignof", "mangleof", "stringof"
	);
	
	protected static void test_resolveSearchInMembersScope(PickedElement<? extends INamedElement> pickedElement, 
			String... expectedResults) {
		CompletionScopeLookup search = new CompletionScopeLookup(0, pickedElement.context, "");
		search.evaluateInMembersScope(pickedElement.element);
		
		resultsChecker(search).checkResults(expectedResults);
	}
	
	protected static PickedElement<Expression> parseExp(String source) {
		return parseElement(source, Expression.class);
	}
	
	// TODO: need to refactor/review this
	public static void testExpressionResolution(PickedElement<Expression> expPick, String... expectedResults) {
		ISemanticContext context = expPick.context;
		INamedElement expType = expPick.element.resolveTypeOfUnderlyingValue_nonNull(context).originalType;
		
		test_resolveSearchInMembersScope(picked2(expType, context), expectedResults);
	}
	
}