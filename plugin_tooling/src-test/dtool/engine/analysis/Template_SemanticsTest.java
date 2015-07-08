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

import java.nio.file.Path;
import java.util.regex.Pattern;

import org.junit.Test;

import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.ITemplatableElement;
import dtool.ast.references.RefTemplateInstance;
import dtool.ast.references.Reference;
import dtool.dub.BundlePath;
import dtool.engine.ResolvedModule;
import dtool.engine.analysis.templates.InstantiatedDefUnit;
import dtool.engine.analysis.templates.RefTemplateInstanceSemantics;
import dtool.engine.analysis.templates.TemplateInstance;
import dtool.engine.analysis.templates.TemplateParameterAnalyser.NotInstantiatedErrorElement;
import dtool.engine.tests.DefUnitResultsChecker;
import dtool.engine.util.NamedElementUtil;
import dtool.parser.SourceEquivalenceChecker;
import melnorme.lang.tooling.ast.util.ASTSourceRangeChecker;
import melnorme.lang.tooling.ast_actual.ASTNode;
import melnorme.lang.tooling.engine.ErrorElement;
import melnorme.lang.tooling.engine.OverloadedNamedElement;
import melnorme.lang.tooling.engine.PickedElement;
import melnorme.lang.tooling.engine.completion.CompletionScopeLookup;
import melnorme.lang.tooling.symbols.INamedElement;


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
	
	
	
	public static void checkNamedElements(Iterable<INamedElement> originalElements, String... expectedResults) {
		new DefUnitResultsChecker(originalElements).checkNamedElements(expectedResults);
	}
	
	protected static <T extends Reference> INamedElement resolveTarget(PickedElement<T> ref) {
		return ref.element.resolveTargetElement(ref.context);
	}
	
	protected static INamedElement resolveTarget(Reference ref, PickedElement<?> other) {
		return ref.resolveTargetElement(other.context);
	}
	
	protected static PickedElement<INamedElement> findTplParamInstance(TemplateInstance tplInstance, 
		String toStringAsCode) {
		Reference ref = NodeFinderByString.find(tplInstance, Reference.class, toStringAsCode);
		INamedElement typeAlias = resolveTarget(ref, picked(tplInstance, tplInstance.refContext));
		return picked(typeAlias, tplInstance.refContext);
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
		
		TemplateInstance tplInstance = doTestTemplateInstantiation(
			TPL_DEF_SAMPLE + "Tpl!(int)/*M*/ _dummy", 
			
			DEFAULT_ModuleName + "/Tpl!(int)", 
			"@{ @TYPE1 = /int; } template Tpl { TYPE1 foo; }",
			expectNotAValue("Tpl!(int)"), 
			array("foo")
		);
		
		CompletionScopeLookup search = new CompletionScopeLookup(tplInstance.getStartPos(), tplInstance.refContext, "");
		tplInstance.performNameLookup(search);
		checkNamedElements(search.getMatchedElements(), array("@TYPE1 = /int;", "$_tests/", "$_tests/Tpl"));
		
		// Some error cases
		testTemplateInstantiation("template Tpl { }; ", "Tpl!()", 
			"@{ } template Tpl "
		);
		testTemplateInstantiation("template Tpl { }; ", "Tpl!(int)", // An extra parameters 
			RefTemplateInstanceSemantics.ERROR__TPL_REF_MATCHED_NONE
		);
		testTemplateInstantiation("template Tpl(A, B) { }; ", "Tpl!(int)", // An extra parameters 
			RefTemplateInstanceSemantics.ERROR__TPL_REF_MATCHED_NONE
		);
		
		test_TypeParam$();
		test_VarParam$();
		test_AliasParam$();
		test_ThisParam$();
		test_TupleParam$();
		
		// Test template-like aggregates:
		
		testTemplateInstantiation("class Tpl() { int bar; }; ", "Tpl!()", 
			"@{ } class Tpl { int bar; }"
		);
		testTemplateInstantiation("struct Tpl(T) { int foo; }; ", "Tpl!(int)", 
			"@{ @ T = /int;} struct Tpl { int foo;}"
		);
		
		
		doTestTemplateInstantiation(
			sourcePlusRef("bool Tpl(T) (T myParam) { return myParam; }; ", "Tpl!(int)"),
			
			DEFAULT_ModuleName + "/" + "Tpl!(int)(T myParam)",
			"@{ @ T = /int; } bool Tpl(T myParam) { return myParam; }",
			"$" + DEFAULT_ModuleName + "/Tpl!(int)(T myParam)" /* this should be changed at some point*/,
			null
		);
		
		testParamKindOverloads$();
		test_TemplateOverloads$();
		test_templateContextAndCaching$();
	}
	
	protected static TemplateInstance testTemplateInstantiation(String baseSource,
		String tplRef, String tplExpectedToStringAsCode) {
		
		return doTestTemplateInstantiation(
			sourcePlusRef(baseSource, tplRef),
			
			DEFAULT_ModuleName + "/" + tplRef,
			tplExpectedToStringAsCode,
			expectNotAValue(tplRef), 
			null
		);
	}
	
	protected static String sourcePlusRef(String baseSource, String tplRef) {
		return baseSource + "; " + tplRef + "/*M*/ _dummy;";
	}
	
	protected static TemplateInstance doTestTemplateInstantiation(String source, 
		String expectedLabel, String expectedToStringAsCode, String expectedTypeLabel, String[] expectedMembers) {
		PickedElement<RefTemplateInstance> tplRef = parseElement(source, "/*M*/", RefTemplateInstance.class);
		return doTestTemplateInstantiation_____(tplRef, expectedLabel, expectedToStringAsCode,
			expectedTypeLabel, expectedMembers);
	}
	
	protected static TemplateInstance doTestTemplateInstantiation_____(PickedElement<RefTemplateInstance> tplRef,
			String expectedLabel, String expectedToStringAsCode, String expectedTypeLabel,
			String[] expectedMembers) {
		INamedElement tplRefTarget = resolveTarget(tplRef);
		
		if(expectedToStringAsCode != null &&
				expectedToStringAsCode.startsWith(ErrorElement.ERROR_PREFIX)) {
			checkElementLabel(tplRefTarget, expectedToStringAsCode);
			return null;
		}
		
		DefUnit instantiatedElement = assertCast(tplRefTarget, DefUnit.class);
		TemplateInstance tplInstance = assertCast(instantiatedElement.getLexicalParent(), TemplateInstance.class);
		
		ITemplatableElement templateDef = tplInstance.templateDef;
				
		assertTrue(tplInstance.getLexicalParent() != null);
		assertTrue(areEqual(instantiatedElement.getNameSourceRangeOrNull(), 
			((DefUnit) templateDef).getNameSourceRangeOrNull()));
//		assertTrue(tplInstance.getOwnerElement() == tplInstance.templateDef.getParent());
		assertTrue(tplInstance.getSemanticContainerKey() == templateDef.getSemanticContainerKey());
		assertTrue(tplInstance.refContext == tplRef.context);
		assertTrue(tplInstance.getElementSemanticContext(tplRef.context) == 
				templateDef.getElementSemanticContext(tplRef.context));
		
		if(expectedLabel != null) {
			String elementLabel = NamedElementUtil.getElementTypedLabel(instantiatedElement, true);
			assertAreEqual(expectedLabel, elementLabel);
		}
		
		checkSourceEquivalence(expectedToStringAsCode, tplInstance);
		
		ASTSourceRangeChecker.checkConsistency(tplInstance);
		
		test_NamedElement(picked2(tplInstance.instantiatedElement, tplRef.context), 
			null, 
			expectedTypeLabel,
			expectedMembers
		);
		
		return tplInstance;
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
	
	protected static void testTemplateArgumentInstantiation(String baseSource, 
		String tplRef, String argExpectedToStringAsCode, String argConcreteTarget, String argType) {
		
		if(argExpectedToStringAsCode == null) {
			testTemplateInstantiation(baseSource, 
				tplRef, RefTemplateInstanceSemantics.ERROR__TPL_REF_MATCHED_NONE);
			return;
		}
		
		TemplateInstance tplInstance = testTemplateInstantiation(baseSource, tplRef, null);
		
		PickedElement<INamedElement> tplArgInstance = findTplParamInstance(tplInstance, "ARG");
		
		assertTrue(tplArgInstance.element instanceof InstantiatedDefUnit);
		checkSourceEquivalence(argExpectedToStringAsCode, (ASTNode) tplArgInstance.element);
		
		test_NamedElement(tplArgInstance, argConcreteTarget, argType, null);
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
		
		
		// Test default param
		testTemplateInstantiation("template Tpl(ARG = int) { ARG foo; }", "Tpl!()", 
			
			"@{ @ARG = /int; } template Tpl { ARG foo; }"
		);
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
		
		
		// Test default param
		testTemplateInstantiation("template Tpl(alias ARG = 123) {  }", "Tpl!()", 
			
			"@{ @value_alias ARG = 123; } template Tpl{  }"
		);
		
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
		
		
		// Test tuple with multiple sizes
		testTemplateInstantiation("template Tpl(ARG...) {  }", "Tpl!()", 
			
			"@{ @ ARG... = (); } template Tpl{  }"
		);
		
		testTemplateInstantiation("template Tpl(ARG...) {  }", "Tpl!(int,123)", 
			
			"@{ @ ARG... = (int,123); } template Tpl{  }"
		);
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
	
	protected void testParamKindOverloads$() {
		
		String TPL_T = "template Tpl(ARG) { void T; }";
		String TPL_Tint = "template Tpl(ARG : int)  { void Tint; }";
		String TPL_Tbool = "template Tpl(ARG : bool) { void Tbool; }";
		String TPL_ALIAS = "template Tpl(alias ARG) { void tAlias; }";
		String TPL_VALUEint = "template Tpl(int ARG) { void valueInt; }";
		
		String TPL_TUPLE = "template Tpl(ARG...) { void tuple; }";
		
		// --- Test specializations and overloads
		
		testTemplateInstantiation(TPL_T + TPL_Tint, "Tpl!(int)",
			
			"@{ @ARG = /int; } template Tpl{ void Tint; }"
		);
		// Multiple template matches should be an error, but match first tpl
		testTemplateInstantiation(TPL_T + TPL_T, "Tpl!(int)", 
			
			"@{ @ARG = /int; } template Tpl{ void T; }"
		);
		testTemplateInstantiation(TPL_T + TPL_Tint, "Tpl!(bool)", 
			
			"@{ @ARG = /bool; } template Tpl{ void T; }"
		);
		testTemplateInstantiation(TPL_T + TPL_Tint + TPL_Tbool, "Tpl!(int)", 
			
			"@{ @ARG = /int; } template Tpl{ void Tint; }"
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
			
			"@{ @int ARG = 123; } template Tpl { void valueInt; }"
		);
		testTemplateInstantiation(TPL_T + TPL_Tint + TPL_VALUEint + TPL_ALIAS, "Tpl!(true)", 
			
			"@{ @value_alias ARG = true; } template Tpl { void tAlias; }"
		);
		
		
		testTemplateInstantiation(
			TPL_T + TPL_Tint + TPL_VALUEint + TPL_TUPLE, "Tpl!(int)", 
			
			"@{ @ARG = /int; } template Tpl { void Tint; }"
		);
	}
	
	protected void test_TemplateOverloads$() {
		
		/* ----------------- Test wrong number of arguments ----------------- */
		
		final String TPL_DEF_0P = "template Tpl() { int A; }";
		
		testTemplateInstantiation(TPL_DEF_0P, "Tpl!()", 
			"@{  } template Tpl { int A; }"
		);
		testTemplateInstantiation(TPL_DEF_0P, "Tpl!(int)", 
			RefTemplateInstanceSemantics.ERROR__TPL_REF_MATCHED_NONE
		);
		testTemplateInstantiation(TPL_DEF_0P, "Tpl!(int, 123)", 
			RefTemplateInstanceSemantics.ERROR__TPL_REF_MATCHED_NONE
		);
		
		
		final String TPL_DEF_1P = "template Tpl(ARG) { int B; }";
		
		testTemplateInstantiation(TPL_DEF_1P, "Tpl!()", 
			RefTemplateInstanceSemantics.ERROR__TPL_REF_MATCHED_NONE
		);
		testTemplateInstantiation(TPL_DEF_1P, "Tpl!(int, 123)", 
			RefTemplateInstanceSemantics.ERROR__TPL_REF_MATCHED_NONE
		);
		
		final String TPL_DEF_1P_plus = "template Tpl(ARG, ARG = bool) { int C; }";
		
		testTemplateInstantiation(TPL_DEF_1P_plus, "Tpl!()", 
			RefTemplateInstanceSemantics.ERROR__TPL_REF_MATCHED_NONE
		);
		testTemplateInstantiation(TPL_DEF_1P_plus, "Tpl!(int, int, int)", 
			RefTemplateInstanceSemantics.ERROR__TPL_REF_MATCHED_NONE
		);
		testTemplateInstantiation(TPL_DEF_1P_plus, "Tpl!(int,char)", 
			"@{ @ARG = /int; @ARG = /char; } template Tpl { int C; }"
		);
		testTemplateInstantiation(TPL_DEF_1P_plus, "Tpl!(char)", 
			"@ { @ARG = /char; @ARG = /bool; } template Tpl { int C; }" 
		);
		
		/* ----------------- test param number overloads ----------------- */
		
		testTemplateInstantiation(TPL_DEF_0P + TPL_DEF_1P, "Tpl!()", 
			"@{  } template Tpl { int A; }"
		);
		testTemplateInstantiation(TPL_DEF_0P + TPL_DEF_1P, "Tpl!(int)", 
			"@{ @ARG = /int; } template Tpl { int B; }"
		);
		
		testTemplateInstantiation(
			"class Tpl(T, int NUM) { int A; } " +
			"template Tpl(T, T2) { int B; } "	, 
			"Tpl!(int,123)", 
			
			"@{ @T = /int; @ int NUM = 123; } class Tpl { int A; }"
		);
		
		
		// Matches multiples
		testTemplateInstantiation(TPL_DEF_1P_plus + TPL_DEF_1P, "Tpl!(char)", 
			"@{ @ARG = /char; @ARG = /bool; } template Tpl { int C; }" 
		);
		
		
		/* ----------------- vs tuples ----------------- */
		
		final String TPL_DEF_1P_Tuple = "template Tpl(ARG1, ARGT...) { int ARG1_Tuple; }";
		
		testTemplateInstantiation(TPL_DEF_1P_Tuple, "Tpl!()", 
			RefTemplateInstanceSemantics.ERROR__TPL_REF_MATCHED_NONE // Not enough arguments 
		);
		
		testTemplateInstantiation(TPL_DEF_1P_Tuple + TPL_DEF_0P, "Tpl!(int)", 
			"@{ @ ARG1 = /int; @ ARGT... = (); } template Tpl { int ARG1_Tuple; }"
		);
		testTemplateInstantiation(TPL_DEF_1P_Tuple + TPL_DEF_0P + TPL_DEF_1P, "Tpl!(int)", 
			null
		);
		testTemplateInstantiation(TPL_DEF_1P_Tuple + TPL_DEF_0P + TPL_DEF_1P_plus , "Tpl!(int)", 
			null
		);
		
		
		/* -----------------  ----------------- */
		
		// Overload with different kinds of template entities
		testTemplateInstantiation(
			"class Tpl() { int bar; }; " + "template Tpl(T) { int foo; }; ", 
			
			"Tpl!()", 
			"@{ } class Tpl { int bar; }"
		);

	}
	
	/* -----------------  ----------------- */
	
	public static final BundlePath DEFAULT_TestsBundle = bundlePath(SEMANTICS_TEST_BUNDLES, "defaultBundle");
	protected static final Path TESTER2 = loc(SEMANTICS_TEST_BUNDLES, "tester2/source/_tester.d").path;
	
	protected void test_templateContextAndCaching$() {
		
		ResolvedModule resModule = parseModule_(
			"import lib_foo.mod; import tpl_sampleA;  " + "Tpl_A!(Foo)/*M*/ _dummy;", 
			TESTER2);
		
		doTestTemplateInstantiation_____(pickElement(resModule, "/*M*/", RefTemplateInstance.class),
			
			"tpl_sampleA/" + "Tpl_A!(Foo)", 
			"@{ @ TYPE = lib_foo.mod/Foo; } template Tpl_A { TYPE foo; }",
			expectNotAValue("Tpl_A!(Foo)"), 
			null);
			
		// TODO: implement and test caching
	}
	
}