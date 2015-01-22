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
import static melnorme.utilbox.core.CoreUtil.areEqual;

import java.util.regex.Pattern;

import melnorme.lang.tooling.ast.util.ASTSourceRangeChecker;
import melnorme.lang.tooling.ast_actual.ASTNode;
import melnorme.lang.tooling.engine.ErrorElement;
import melnorme.lang.tooling.engine.ErrorElement.NotATypeErrorElement;
import melnorme.lang.tooling.engine.PickedElement;
import melnorme.lang.tooling.engine.completion.CompletionScopeLookup;
import melnorme.lang.tooling.symbols.INamedElement;
import melnorme.utilbox.collections.Collection2;

import org.junit.Test;

import dtool.ast.definitions.DefinitionTemplate;
import dtool.ast.definitions.ITemplateParameter.NotInstantiatedErrorElement;
import dtool.ast.references.RefTemplateInstance;
import dtool.ast.references.Reference;
import dtool.engine.analysis.templates.RefTemplateInstanceSemantics;
import dtool.engine.analysis.templates.TemplateInstance;
import dtool.engine.tests.DefUnitResultsChecker;
import dtool.engine.util.NamedElementUtil;
import dtool.parser.SourceEquivalenceChecker;


public class Template_SemanticsTest extends NamedElement_CommonTest {
	
	@Test
	public void test_UninstantiatedTemplate() throws Exception { test_UninstantiatedTemplate$(); }
	public void test_UninstantiatedTemplate$() throws Exception {
		test_NamedElement_Concrete(parseElement("template xxx/*M*/() { int foo; } ", INamedElement.class), 
			expectNotAValue("xxx"), array("foo") );
		
		
		test_NamedElement_Concrete(parseElement("template Tpl(int xxx/*M*/) { int foo; } ", INamedElement.class), 
			"$/int", NE_LanguageIntrinsics_SemanticsTest.INT_PROPERTIES);
		

		test_NamedElement_Alias(parseElement("template Tpl(xxx/*M*/) { int foo; } ", INamedElement.class), 
			ErrorElement.UNSUPPORTED__Name, expectNotAValue("xxx"), NO_MEMBERS);
		test_NamedElement_Alias(parseElement("template Tpl(xxx/*M*/ : int) { int foo; } ", INamedElement.class), 
			"$/int", expectNotAValue("xxx"), NE_LanguageIntrinsics_SemanticsTest.INT_PROPERTIES);
		
		
		test_NamedElement_Alias(parseElement("template Tpl(alias xxx/*M*/) { int foo; } ", INamedElement.class), 
			NotInstantiatedErrorElement.NAME, NotInstantiatedErrorElement.NAME, NO_MEMBERS);
		
		
		test_NamedElement_Alias(parseElement("template Tpl(this xxx/*M*/) { int foo; } ", INamedElement.class), 
			NotInstantiatedErrorElement.NAME, NotInstantiatedErrorElement.NAME, NO_MEMBERS);
		
		test_NamedElement_Concrete(parseElement("template Tpl(xxx/*M*/...) { int foo; } ", INamedElement.class), 
			expectNotAValue("xxx"), NO_MEMBERS);
		
	}
	
	/* -----------------  ----------------- */ 
	
	
	
	public static void checkNamedElements(Collection2<INamedElement> originalElements, String... expectedResults) {
		new DefUnitResultsChecker(originalElements).checkNamedElements(expectedResults);
	}
	
	protected static <T extends Reference> INamedElement resolveTarget(PickedElement<T> ref) {
		return ref.element.resolveTargetElement(ref.context);
	}
	
	protected static INamedElement resolveTarget(Reference ref, PickedElement<?> other) {
		return ref.resolveTargetElement(other.context);
	}
	
	protected CompletionScopeLookup allElementsSearch(TemplateInstance tplInstance) {
		return new CompletionScopeLookup(tplInstance.getStartPos(), tplInstance.context, "");
	}
	
	protected PickedElement<INamedElement> findTplParamInstance(PickedElement<TemplateInstance> tplInstancePick, 
		String toStringAsCode) {
		Reference ref = NodeFinderByString.find(tplInstancePick.element, Reference.class, toStringAsCode);
		INamedElement typeAlias = resolveTarget(ref, tplInstancePick);
		return picked(typeAlias, tplInstancePick.context);
	}
	
	/* -----------------  ----------------- */
	
	@Override
	public void test_NamedElement________() throws Exception {
		testTemplateInstantiation$();
	}
		
	protected static final String TPL_DEF_A = "template Tpl("
			+ "TYPE1"
			+ ") { TYPE1 foo; }";
	
	public void testTemplateInstantiation$() throws Exception {
		
		PickedElement<TemplateInstance> tplInstancePick = testTemplateInstantiation_____(
			TPL_DEF_A + "Tpl!(int)/*M*/ _dummy", 
			
			"_tests/Tpl!(int)", 
			"Tpl!(int){ @TYPE1 = /int; }{ TYPE1 foo; }",
			array("foo")
		);
		
		CompletionScopeLookup search = allElementsSearch(tplInstancePick.element);
		tplInstancePick.element.performNameLookup(search);
		checkNamedElements(search.getMatchedElements(), array("@TYPE1 = /int;", "$_tests/", "$_tests/Tpl"));
		
		test_TypeParam$();
		
	}
	
	protected PickedElement<TemplateInstance> testTemplateInstantiation_____(String source, String expectedLabel,
			String expectedToStringAsCode, String[] expectedMembers) {
		PickedElement<RefTemplateInstance> tplRef = parseElement(source, "/*M*/", RefTemplateInstance.class);
		
		TemplateInstance tplInstance = assertCast(resolveTarget(tplRef), TemplateInstance.class);
		PickedElement<TemplateInstance> tplInstancePick = picked(tplInstance, tplRef.context);
		
		DefinitionTemplate templateDef = tplInstance.templateDef;
				
		assertTrue(tplInstance.getLexicalParent() != null);
		assertTrue(areEqual(tplInstance.getNameSourceRangeOrNull(), templateDef.getNameSourceRangeOrNull()));
//		assertTrue(tplInstance.getOwnerElement() == tplInstance.templateDef.getParent());
		assertTrue(tplInstance.getSemanticContainerKey() == templateDef.getSemanticContainerKey());
		assertTrue(tplInstance.getElementSemanticContext(tplRef.context) == tplRef.context); /*FIXME: BUG here*/
		
		String elementLabel = NamedElementUtil.getElementTypedLabel(tplInstance, true);
		assertAreEqual(expectedLabel, elementLabel);
		
		checkSourceEquivalence(expectedToStringAsCode, tplInstance);
		
		ASTSourceRangeChecker.checkConsistency(tplInstance);
		
		test_NamedElement(tplInstancePick, 
			null, 
			expectNotAValue(tplInstance),
			expectedMembers
		);
		
		return tplInstancePick;
	}

	protected void checkSourceEquivalence(String expectedToStringAsCode, ASTNode node) {
		if(expectedToStringAsCode != null) {
			String nodeToStringAsCode = node.toStringAsCode();
			
			expectedToStringAsCode = expectedToStringAsCode.replaceAll(Pattern.quote("#"), "@");
			nodeToStringAsCode = expectedToStringAsCode.replaceAll(Pattern.quote("#"), "@");
			
			SourceEquivalenceChecker.assertCheck(nodeToStringAsCode, expectedToStringAsCode);
		}
	}
	
	/* -----------------  ----------------- */
	
	protected void test_TypeParam$() {
		PickedElement<TemplateInstance> tplInstancePick;
		
		tplInstancePick = testTemplateInstantiation_____(
			TPL_DEF_A + "Tpl!(int)/*M*/ _dummy", 
			
			"_tests/Tpl!(int)", 
			"Tpl!(int){ @TYPE1 = /int; }{ TYPE1 foo; }",
			array("foo")
		);
		test_NamedElement_NonValue(findTplParamInstance(tplInstancePick, "TYPE1"), "$/int", null);
		
		// Test alias is same template instance
		tplInstancePick = testTemplateInstantiation_____(
			TPL_DEF_A + "alias intAlias = int; Tpl!(intAlias)/*M*/ _dummy", 
			
			"_tests/Tpl!(intAlias)", 
			"Tpl!(intAlias){ @TYPE1 = /int; }{ TYPE1 foo; }",
			array("foo")
		);
		test_NamedElement_NonValue(findTplParamInstance(tplInstancePick, "TYPE1"), "$/int", null);
		
		
		// --- Test invalid parameter
		tplInstancePick = testTemplateInstantiation_____(
			TPL_DEF_A + "Tpl!(123)/*M*/ _dummy", 
			
			"_tests/Tpl!(123)", 
			"Tpl!(123){ @TYPE1 = " + RefTemplateInstanceSemantics.ERROR__TPL_ARG__NotAType + " ; }{ TYPE1 foo; }",
			array("foo")
		);
		
		tplInstancePick = testTemplateInstantiation_____(
			TPL_DEF_A + "int someVar; Tpl!(someVar)/*M*/ _dummy", 
			
			"_tests/Tpl!(someVar)", 
			"Tpl!(someVar){ @TYPE1 = " + NotATypeErrorElement.errorName("someVar") + " ; }{ TYPE1 foo; }",
			array("foo")
		);

		
//		tplInstancePick = testTemplateInstantiation_____(
//			TPL_DEF_A + "Tpl!()/*M*/ _dummy", 
//			
//			"_tests/Tpl!()", 
//			"Tpl!(){ @TYPE1 = int; }{ TYPE1 foo; }",
//			array("foo")
//		);
		tplInstancePick = testTemplateInstantiation_____(
			TPL_DEF_A + "Tpl!(missing)/*M*/ _dummy", 
			
			"_tests/Tpl!(missing)", 
			"Tpl!(missing){ @TYPE1 = " + expectNotFound("missing") + " ; }{ TYPE1 foo; }",
			array("foo")
		);
		
		test_NamedElement_NonValue(findTplParamInstance(tplInstancePick, "TYPE1"), expectNotFound("missing"), null);
	}
	
	
	
	/* -----------------  ----------------- */
	
	protected static final String TPL_DEF_B = "template Tpl("
			+ "TYPE1 : int = bar, "
			+ "TYPE2 = ambigB, "
			+ "int VAR1 : 10 = 1,"
			+ "alias ALIAS1 : 12 + 2 = foo,"
			+ "alias ALIAS2 : int = 2,"
			+ "TUPLE ..., "
			+ "this THIS"
			+ ") { int x; };";
	
}