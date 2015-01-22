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
		test_VarParam$();
		test_AliasParam$();
		test_ThisParam$();
		test_TupleParam$();
		
	}
	
	protected static PickedElement<TemplateInstance> testTemplateInstantiation_____(String source, 
		String expectedLabel, String expectedToStringAsCode, String[] expectedMembers) {
		PickedElement<RefTemplateInstance> tplRef = parseElement(source, "/*M*/", RefTemplateInstance.class);
		
		TemplateInstance tplInstance = assertCast(resolveTarget(tplRef), TemplateInstance.class);
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
			testTemplateArgumentInstantiation(templateSource + "char aVar;", "Tpl!(aVar)",
				varArg_toStringAsCode,
				varArg_concreteTarget,
				varArg_type
			);
			testTemplateArgumentInstantiation(templateSource + "char aVar; alias aVarAlias = aVar", "Tpl!(aVarAlias)",
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
		String expectedExtendedName, String expectedToStringAsCode, String argConcreteTarget, String argType) {
		
		String source = baseSource + "; " + expectedExtendedName + "/*M*/ _dummy;"; 
		PickedElement<TemplateInstance> tplInstancePick = testTemplateInstantiation_____(
			source,
			
			DEFAULT_ModuleName + "/" + expectedExtendedName,
			null,
			null
		);
		
		PickedElement<INamedElement> tplArgInstance = findTplParamInstance(tplInstancePick, "ARG");
		
		checkSourceEquivalence(expectedToStringAsCode, (ASTNode) tplArgInstance.element);
		
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
				
				varArg_toStringAsCode = "@ARG = " + NotATypeErrorElement.errorName("aVar") + " ;";
				varArg_concreteTarget = NotATypeErrorElement.errorName("aVar");
				varArg_type = expectNotAValue("ARG");
				varArgAlias_toStringAsCode = varArg_toStringAsCode;
				varArgAlias_concreteTarget = varArg_concreteTarget;
				varArgAlias_type = varArg_type;
				
				missingArg_ToStringAsCode = "@ARG = " + expectNotFound("missing") + ";";
				missingArg_concreteTarget = expectNotFound("missing");
				missingArg_type = expectNotAValue("ARG");
				
				numberArg_ToStringAsCode = "@ARG = " + RefTemplateInstanceSemantics.ERROR__TPL_ARG__NotAType + ";";
				numberArg_concreteTarget = RefTemplateInstanceSemantics.ERROR__TPL_ARG__NotAType;
				numberArg_type = expectNotAValue("ARG");
				
			}
		}.test_TplParameter$();
		
		// TODO: test specializations and overloads
	}
	
	protected void test_VarParam$() {
		new TemplateParamTester() {
			{
				templateSource = "template Tpl(int ARG) { ARG foo; }";
				
				intArg_toStringAsCode = "@ int ARG = int;";
				intArg_concreteTarget = intArg_toStringAsCode;
				intArg_type = "$/int";
				
				intArgAlias_toStringAsCode = "@ int ARG = intAlias;";
				intArgAlias_concreteTarget = intArgAlias_toStringAsCode;
				intArgAlias_type = "$/int";
				
				
				varArg_toStringAsCode = "@ int ARG = aVar;";
				varArg_concreteTarget = varArg_toStringAsCode;
				varArg_type = "$/int";
				varArgAlias_toStringAsCode = "@ int ARG = aVarAlias;";
				varArgAlias_concreteTarget = varArgAlias_toStringAsCode;
				varArgAlias_type = "$/int";
				
				missingArg_ToStringAsCode = "@ int ARG = missing;";
				missingArg_concreteTarget = missingArg_ToStringAsCode;
				missingArg_type = "$/int";
				
				numberArg_ToStringAsCode = "@ int ARG = 123;";
				numberArg_concreteTarget = numberArg_ToStringAsCode;
				numberArg_type = "$/int";
				
			}
		}.test_TplParameter$();
	}
	
	protected void test_AliasParam$() {
		new TemplateParamTester() {
			{
				templateSource = "template Tpl(alias ARG) { auto foo = ARG; }";
				
				intArg_toStringAsCode = "@value_alias ARG = int;";
				intArg_concreteTarget = intArg_toStringAsCode;
				intArg_type = expectNotAValue("int");
				
				intArgAlias_toStringAsCode = "@value_alias ARG = intAlias;";
				intArgAlias_concreteTarget = intArgAlias_toStringAsCode;
				intArgAlias_type = expectNotAValue("int");
				
				
				varArg_toStringAsCode = "@value_alias ARG = aVar;";
				varArg_concreteTarget = varArg_toStringAsCode;
				varArg_type = "$/char";
				varArgAlias_toStringAsCode = "@value_alias ARG = aVarAlias;";
				varArgAlias_concreteTarget = varArgAlias_toStringAsCode;
				varArgAlias_type = "$/char";
				
				missingArg_ToStringAsCode = "@value_alias ARG = missing;";
				missingArg_concreteTarget = missingArg_ToStringAsCode;
				missingArg_type = expectNotFound("missing");
				
				numberArg_ToStringAsCode = "@value_alias ARG = 123;";
				numberArg_concreteTarget = numberArg_ToStringAsCode;
				numberArg_type = "$/int";
				
			}
		}.test_TplParameter$();
	}
	
	protected void test_TupleParam$() {
		 
	}
	
	protected void test_ThisParam$() {
		 
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