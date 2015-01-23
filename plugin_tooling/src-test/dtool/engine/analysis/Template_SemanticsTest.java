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
import melnorme.lang.tooling.engine.OverloadedNamedElement;
import melnorme.lang.tooling.engine.PickedElement;
import melnorme.lang.tooling.engine.completion.CompletionScopeLookup;
import melnorme.lang.tooling.symbols.INamedElement;
import melnorme.utilbox.collections.Collection2;

import org.junit.Test;

import dtool.ast.definitions.DefinitionTemplate;
import dtool.ast.references.RefTemplateInstance;
import dtool.ast.references.Reference;
import dtool.engine.analysis.templates.InstantiatedDefUnit;
import dtool.engine.analysis.templates.RefTemplateInstanceSemantics;
import dtool.engine.analysis.templates.TemplateInstance;
import dtool.engine.analysis.templates.TemplateParameterAnalyser.NotInstantiatedErrorElement;
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
	
	protected static PickedElement<INamedElement> findTplParamInstance(PickedElement<TemplateInstance> tplInstancePick, 
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
		
	public void testTemplateInstantiation$() throws Exception {
		
		testTemplateInstantiation("class Tpl { }; ", "Tpl!(int)", 
			RefTemplateInstanceSemantics.ERROR__NotATemplate + ":Tpl"
		);
		testTemplateInstantiation("", "Tpl!(int)", 
			expectNotFound("Tpl")
		);
		testTemplateInstantiation("void Tpl(); int Tpl; ", "Tpl!(int)",
			OverloadedNamedElement.ERROR_NAME + "[void Tpl() ;| int Tpl;]"
		);
		
		
		final String TPL_DEF_SAMPLE = "template Tpl(TYPE1) { TYPE1 foo; }";
		
		PickedElement<TemplateInstance> tplInstancePick = doTestTemplateInstantiation_____(
			TPL_DEF_SAMPLE + "Tpl!(int)/*M*/ _dummy", 
			
			"_tests/Tpl!(int)", 
			"Tpl!(int){ @TYPE1 = /int; }{ TYPE1 foo; }",
			array("foo")
		);
		
		CompletionScopeLookup search = allElementsSearch(tplInstancePick.element);
		tplInstancePick.element.performNameLookup(search);
		checkNamedElements(search.getMatchedElements(), array("@TYPE1 = /int;", "$_tests/", "$_tests/Tpl"));
		
		
		test_TypeParam$();
		test_VarParam$();
		test_AliasParam$();
		test_ThisParam$();
		test_TupleParam$();
		
		testParamOverloads$();
		test_TemplateOverloads$();
	}
	
	protected static PickedElement<TemplateInstance> testTemplateInstantiation(String baseSource,
		String tplRef, String tplExpectedToStringAsCode) {
		String source = baseSource + "; " + tplRef + "/*M*/ _dummy;";
		
		return doTestTemplateInstantiation_____(
			source,
			
			DEFAULT_ModuleName + "/" + tplRef,
			tplExpectedToStringAsCode,
			null
		);
	}
	
	protected static PickedElement<TemplateInstance> doTestTemplateInstantiation_____(String source, 
		String expectedLabel, String expectedToStringAsCode, String[] expectedMembers) {
		PickedElement<RefTemplateInstance> tplRef = parseElement(source, "/*M*/", RefTemplateInstance.class);
		INamedElement tplRefTarget = resolveTarget(tplRef);
		
		if(expectedToStringAsCode != null &&
				expectedToStringAsCode.startsWith(ErrorElement.ERROR_PREFIX)) {
			checkElementLabel(tplRefTarget, expectedToStringAsCode);
			return null;
		}
		
		TemplateInstance tplInstance = assertCast(tplRefTarget, TemplateInstance.class);
		PickedElement<TemplateInstance> tplInstancePick = picked(tplInstance, tplRef.context);
		
		DefinitionTemplate templateDef = tplInstance.templateDef;
				
		assertTrue(tplInstance.getLexicalParent() != null);
		assertTrue(areEqual(tplInstance.getNameSourceRangeOrNull(), templateDef.getNameSourceRangeOrNull()));
//		assertTrue(tplInstance.getOwnerElement() == tplInstance.templateDef.getParent());
		assertTrue(tplInstance.getSemanticContainerKey() == templateDef.getSemanticContainerKey());
		assertTrue(tplInstance.getElementSemanticContext(tplRef.context) == tplRef.context); /*FIXME: BUG here*/
		
		if(expectedLabel != null) {
			String elementLabel = NamedElementUtil.getElementTypedLabel(tplInstance, true);
			assertAreEqual(expectedLabel, elementLabel);
		}
		
		checkSourceEquivalence(expectedToStringAsCode, tplInstance);
		
		ASTSourceRangeChecker.checkConsistency(tplInstance);
		
		test_NamedElement(tplInstancePick, 
			null, 
			expectNotAValue(tplInstance),
			expectedMembers
		);
		
		return tplInstancePick;
	}

	protected static void checkSourceEquivalence(String expectedToStringAsCode, ASTNode node) {
		if(expectedToStringAsCode != null) {
			String nodeToStringAsCode = node.toStringAsCode();
			
			nodeToStringAsCode = nodeToStringAsCode.replaceAll(Pattern.quote("#"), "@");
			expectedToStringAsCode = expectedToStringAsCode.replaceAll(Pattern.quote("#"), "@");
			
			SourceEquivalenceChecker.assertCheck(nodeToStringAsCode, expectedToStringAsCode);
		}
	}
	
	/* -----------------  ----------------- */
	
	protected static class TemplateParamTester {
	
		protected String templateSource;
		
		protected String intArg_toStringAsCode;
		protected String intArg_concreteTarget;
		protected String intArg_type;
		protected String intArgAlias_toStringAsCode;
		protected String intArgAlias_concreteTarget;
		protected String intArgAlias_type;

		
		protected String varArg_toStringAsCode;
		protected String varArg_concreteTarget;
		protected String varArg_type;
		protected String varArgAlias_toStringAsCode;
		protected String varArgAlias_concreteTarget;
		protected String varArgAlias_type;
		
		protected String missingArg_ToStringAsCode;
		protected String missingArg_concreteTarget;
		protected String missingArg_type;
		
		protected String numberArg_ToStringAsCode;
		protected String numberArg_concreteTarget;
		protected String numberArg_type;
		
		protected void test_TplParameter$() {
			
			// test type ref parameter (and alias)
			testTemplateArgumentInstantiation(templateSource, "Tpl!(int)",
				intArg_toStringAsCode, 
				intArg_concreteTarget, 
				intArg_type
			);
			testTemplateArgumentInstantiation(templateSource + "alias intAlias = int;", "Tpl!(intAlias)",
				intArgAlias_toStringAsCode,
				intArgAlias_concreteTarget, 
				intArgAlias_type
			);
			
			// Test name parameter, to var (and alias)
			testTemplateArgumentInstantiation(templateSource + "int aVar;", "Tpl!(aVar)",
				varArg_toStringAsCode,
				varArg_concreteTarget,
				varArg_type
			);
			testTemplateArgumentInstantiation(templateSource + "int aVar; alias aVarAlias = aVar", "Tpl!(aVarAlias)",
				varArgAlias_toStringAsCode,
				varArgAlias_concreteTarget,
				varArgAlias_type
			);
			
			
			testTemplateArgumentInstantiation(templateSource,  "Tpl!(missing)", 
				missingArg_ToStringAsCode,
				missingArg_concreteTarget,
				missingArg_type
			);
			
			
			// Test number parameter
			testTemplateArgumentInstantiation(templateSource, "Tpl!(123)",
				numberArg_ToStringAsCode,
				numberArg_concreteTarget,
				numberArg_type
			);
			
		}
	}
	
	protected static PickedElement<TemplateInstance> testTemplateArgumentInstantiation(String baseSource, 
		String tplRef, String argExpectedToStringAsCode, String argConcreteTarget, String argType) {
		
		if(argExpectedToStringAsCode == null) {
			testTemplateInstantiation(baseSource, 
				tplRef, RefTemplateInstanceSemantics.ERROR__TPL_REF_MATCHED_NONE);
			return null;
		}
		
		PickedElement<TemplateInstance> tplInstancePick = testTemplateInstantiation(baseSource,
			tplRef, null);
		
		PickedElement<INamedElement> tplArgInstance = findTplParamInstance(tplInstancePick, "ARG");
		
		assertTrue(tplArgInstance.element instanceof InstantiatedDefUnit);
		checkSourceEquivalence(argExpectedToStringAsCode, (ASTNode) tplArgInstance.element);
		
		test_NamedElement(tplArgInstance, argConcreteTarget, argType, null);
		
		return tplInstancePick;
	}
	
	
	protected void test_TypeParam$() {
		new TemplateParamTester() {
			{
				templateSource = "template Tpl(ARG) { ARG foo; }";
				
				intArg_toStringAsCode = "@ARG = /int;";
				intArg_concreteTarget = "$/int";
				intArg_type = expectNotAValue("ARG");
				intArgAlias_toStringAsCode = intArg_toStringAsCode;
				intArgAlias_concreteTarget = intArg_concreteTarget;
				intArgAlias_type = intArg_type;
				
				varArg_toStringAsCode = null;
				varArgAlias_toStringAsCode = null;
				
				missingArg_ToStringAsCode = null;
				
				numberArg_ToStringAsCode = null;
				
			}
		}.test_TplParameter$();
		
	}
	
	protected void test_VarParam$() {
		new TemplateParamTester() {
			{
				templateSource = "template Tpl(int ARG) { ARG foo; }";
				
				
				intArg_toStringAsCode = null;
				intArgAlias_toStringAsCode = null;
				
				varArg_toStringAsCode = "@ int ARG = aVar;";
				varArg_concreteTarget = varArg_toStringAsCode;
				varArg_type = "$/int";
				varArgAlias_toStringAsCode = "@ int ARG = aVarAlias;";
				varArgAlias_concreteTarget = varArgAlias_toStringAsCode;
				varArgAlias_type = "$/int";
				
				missingArg_ToStringAsCode = null;
				
				numberArg_ToStringAsCode = "@ int ARG = 123;";
				numberArg_concreteTarget = numberArg_ToStringAsCode;
				numberArg_type = "$/int";
				
			}
		}.test_TplParameter$();
		
		
		new TemplateParamTester() {
			{
				templateSource = "template Tpl(bool ARG) { ARG foo; }";
				
				
				intArg_toStringAsCode = null;
				intArgAlias_toStringAsCode = null;
				
				varArg_toStringAsCode = null;
				varArgAlias_toStringAsCode = null;
				
				missingArg_ToStringAsCode = null;
				
				numberArg_ToStringAsCode = null;
			}
		}.test_TplParameter$();
	}
	
	protected void test_AliasParam$() {
		new TemplateParamTester() {
			{
				templateSource = "template Tpl(alias ARG) { auto foo = ARG; }";
				
				intArg_toStringAsCode = null;
				intArgAlias_toStringAsCode = null;
				
				
				varArg_toStringAsCode = "@value_alias ARG = aVar;";
				varArg_concreteTarget = varArg_toStringAsCode;
				varArg_type = "$/int";
				varArgAlias_toStringAsCode = "@value_alias ARG = aVarAlias;";
				varArgAlias_concreteTarget = varArgAlias_toStringAsCode;
				varArgAlias_type = "$/int";
				
				missingArg_ToStringAsCode = null;
				
				numberArg_ToStringAsCode = "@value_alias ARG = 123;";
				numberArg_concreteTarget = numberArg_ToStringAsCode;
				numberArg_type = "$/int";
				
			}
		}.test_TplParameter$();
	}
	
	protected void test_TupleParam$() {
		new TemplateParamTester() {
			{
				templateSource = "template Tpl(ARG...) { auto foo = ARG; }";
				
				intArg_toStringAsCode = "@ ARG... = (int);";
				intArg_concreteTarget = intArg_toStringAsCode;
				intArg_type = expectNotAValue("ARG");
				
				intArgAlias_toStringAsCode = "@ ARG... = (intAlias);";
				intArgAlias_concreteTarget = intArgAlias_toStringAsCode;
				intArgAlias_type = expectNotAValue("ARG");
				
				
				varArg_toStringAsCode = "@ ARG... = (aVar);";
				varArg_concreteTarget = varArg_toStringAsCode;
				varArg_type = expectNotAValue("ARG");
				varArgAlias_toStringAsCode = "@ ARG... = (aVarAlias);";
				varArgAlias_concreteTarget = varArgAlias_toStringAsCode;
				varArgAlias_type = expectNotAValue("ARG");
				
				missingArg_ToStringAsCode = "@ ARG... = (missing);";
				missingArg_concreteTarget = missingArg_ToStringAsCode;
				missingArg_type = expectNotAValue("ARG");
				
				numberArg_ToStringAsCode = "@ ARG... = (123);";
				numberArg_concreteTarget = numberArg_ToStringAsCode;
				numberArg_type = expectNotAValue("ARG");
				
			}
		}.test_TplParameter$();
		
		// TODO Tuple with multiple sizes
	}
	
	protected void test_ThisParam$() {
		// ThisParameter can only be correctly instantiated with templated functions, using IFTI
		// (implicit Function Template Instantiation
		
		new TemplateParamTester() {
			{
				templateSource = "template Tpl(this ARG){ auto foo = ARG; }";
				
				intArg_toStringAsCode = null;
				intArgAlias_toStringAsCode = null;
				
				varArg_toStringAsCode = null;
				varArgAlias_toStringAsCode = null;
				
				missingArg_ToStringAsCode = null;
				
				numberArg_ToStringAsCode = null;
			}
		}.test_TplParameter$();
	}
	
	/* -----------------  ----------------- */
	
	protected void testParamOverloads$() {
		
		String TPL_T = "template Tpl(ARG) { void T; }";
		String TPL_Tint = "template Tpl(ARG : int)  { void Tint; }";
		String TPL_Tbool = "template Tpl(ARG : bool) { void Tbool; }";
		String TPL_ALIAS = "template Tpl(alias ARG) { void tAlias; }";
		String TPL_VALUEint = "template Tpl(int ARG) { void valueInt; }";
		
		String TPL_TUPLE = "template Tpl(ARG...) { void tuple; }";
		
		// --- Test specializations and overloads
		
		testTemplateInstantiation(TPL_T + TPL_Tint, "Tpl!(int)",
			
			"Tpl!(int){ @ARG = /int; }{ void Tint; }"
		);
		// Multiple template matches should be an error, but match first tpl
		testTemplateInstantiation(TPL_T + TPL_T, "Tpl!(int)", 
			
			"Tpl!(int){ @ARG = /int; }{ void T; }"
		);
		testTemplateInstantiation(TPL_T + TPL_Tint, "Tpl!(bool)", 
			
			"Tpl!(bool){ @ARG = /bool; }{ void T; }"
		);
		testTemplateInstantiation(TPL_T + TPL_Tint + TPL_Tbool, "Tpl!(int)", 
			
			"Tpl!(int){ @ARG = /int; }{ void Tint; }"
		);
		// Match none:
		testTemplateInstantiation(TPL_Tint + TPL_Tbool, "Tpl!(float)", 
			
			RefTemplateInstanceSemantics.ERROR__TPL_REF_MATCHED_NONE
		);
		
		
		// ---- Value params
		testTemplateInstantiation(TPL_T + TPL_Tint + TPL_Tbool, "Tpl!(123)", 
			
			RefTemplateInstanceSemantics.ERROR__TPL_REF_MATCHED_NONE
		);
		
		testTemplateInstantiation(TPL_T + TPL_Tint + TPL_VALUEint + TPL_ALIAS, "Tpl!(123)", 
			
			"Tpl!(123){ @int ARG = 123; }{ void valueInt; }"
		);
		testTemplateInstantiation(TPL_T + TPL_Tint + TPL_VALUEint + TPL_ALIAS, "Tpl!(true)", 
			
			"Tpl!(true){ @value_alias ARG = true; }{ void tAlias; }"
		);
		
		
		testTemplateInstantiation(
			TPL_T + TPL_Tint + TPL_VALUEint + TPL_TUPLE, "Tpl!(int)", 
			
			"Tpl!(int){ @ARG = /int; }{ void Tint; }"
		);
	}
	
	protected void test_TemplateOverloads$() {
		
		// Test wrong number of parameters:
		
		final String TPL_DEF_A = "template Tpl() { int A; }";
		
		testTemplateInstantiation(TPL_DEF_A, "Tpl!()", 
			"Tpl!(){  }{ int A; }"
		);
		testTemplateInstantiation(TPL_DEF_A, "Tpl!(int)", 
			RefTemplateInstanceSemantics.ERROR__TPL_REF_MATCHED_NONE
		);
		testTemplateInstantiation(TPL_DEF_A, "Tpl!(int, 123)", 
			RefTemplateInstanceSemantics.ERROR__TPL_REF_MATCHED_NONE
		);
		
//		
//		final String TPL_DEF_B = "template Tpl(ARG) { int B; }";
//		
//		testTemplateInstantiation_____(TPL_DEF_B + "Tpl!()/*M*/ _dummy", 
//			RefTemplateInstanceSemantics.ERROR__TPL_REF_MATCHED_NONE, null, null
//		);
//		testTemplateInstantiation_____(TPL_DEF_B + "Tpl!(int, 123)/*M*/ _dummy", 
//			RefTemplateInstanceSemantics.ERROR__TPL_REF_MATCHED_NONE, null, null
//		);
//		
//		final String TPL_DEF_C = "template Tpl(ARG, ARG = int) { int C; }";
//		
//		testTemplateInstantiation_____(TPL_DEF_C + "Tpl!()/*M*/ _dummy", 
//			RefTemplateInstanceSemantics.ERROR__TPL_REF_MATCHED_NONE, null, null
//		);
//		testTemplateInstantiation_____(TPL_DEF_C + "Tpl!(int, int, int)/*M*/ _dummy", 
//			RefTemplateInstanceSemantics.ERROR__TPL_REF_MATCHED_NONE, null, null
//		);
//		
//		testTemplateInstantiation_____(TPL_DEF_C + "Tpl!(char)/*M*/ _dummy", 
//			"_tests/Tpl!(char)",
//			"Tpl!(char) { } { int C; }", 
//			null
//		);
//
//		// Test overloads
//		
//		final String TPL_DEFs = TPL_DEF_A + TPL_DEF_B + TPL_DEF_C;
//		
//		testTemplateInstantiation_____(TPL_DEF_A + TPL_DEF_B + "Tpl!(int)/*M*/ _dummy", 
//			"_tests/Tpl!(int)", 
//			"Tpl!(int){ #### }{ int B; }",
//			null
//		);
//		testTemplateInstantiation_____(TPL_DEF_A + TPL_DEF_B + "Tpl!()/*M*/ _dummy", 
//			"_tests/Tpl!()", 
//			"Tpl!(){  }{ int A; }",
//			null
//		);

	}
	
}