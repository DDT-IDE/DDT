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
import melnorme.lang.tooling.ast.util.ASTSourceRangeChecker;
import melnorme.lang.tooling.context.ISemanticContext;
import melnorme.lang.tooling.engine.PickedElement;
import melnorme.lang.tooling.engine.completion.CompletionScopeLookup;
import melnorme.lang.tooling.symbols.INamedElement;
import melnorme.utilbox.collections.Collection2;

import org.junit.Test;

import dtool.ast.definitions.DefinitionTemplate;
import dtool.ast.references.RefTemplateInstance;
import dtool.ast.references.Reference;
import dtool.engine.analysis.templates.TemplateInstance;
import dtool.engine.tests.DefUnitResultsChecker;
import dtool.parser.SourceEquivalenceChecker;


public class Template_SemanticsTest extends NamedElement_CommonTest {
	
	public static void checkNamedElements(Collection2<INamedElement> originalElements, String... expectedResults) {
		new DefUnitResultsChecker(originalElements).checkNamedElements(expectedResults);
	}
	
	protected static <T extends Reference> INamedElement resolveTarget(PickedElement<T> ref) {
		return ref.element.resolveTargetElement(ref.context);
	}
	
	protected static INamedElement resolveTarget(Reference ref, PickedElement<?> other) {
		return ref.resolveTargetElement(other.context);
	}
	
	protected static String expectNotAValue(INamedElement namedElement) {
		return expectNotAValue(namedElement.getFullyQualifiedName());
	}
	
	protected CompletionScopeLookup allElementsSearch(TemplateInstance tplInstance) {
		return new CompletionScopeLookup(tplInstance.getStartPos(), tplInstance.context, "");
	}
	
	@Override
	public void test_resolveElement________() throws Exception {
	}
	
	@Override
	public void test_resolveSearchInMembersScope________() throws Exception {
	}
	
	/* -----------------  ----------------- */
		
	protected static final String TPL_DEF_A = "template Tpl("
			+ "TYPE1"
			+ ") { TYPE1 foo; }";
	
	protected static final String TPL_DEF_B = "template Tpl("
			+ "TYPE1 : int = bar, "
			+ "TYPE2 = ambigB, "
			+ "int VAR1 : 10 = 1,"
			+ "alias ALIAS1 : 12 + 2 = foo,"
			+ "alias ALIAS2 : int = 2,"
			+ "TUPLE ..., "
			+ "this THIS"
			+ ") { int x; };";
	
	@Test
	public void testTemplateInstantiation() throws Exception { testTemplateInstantiation$(); }
	public void testTemplateInstantiation$() throws Exception {

		PickedElement<RefTemplateInstance> tplRef = parseElement(
			TPL_DEF_A + "Tpl!(int)/*M*/ _dummy", "/*M*/", RefTemplateInstance.class);
		
		TemplateInstance tplInstance = assertCast(resolveTarget(tplRef), TemplateInstance.class);
		DefinitionTemplate templateDef = tplInstance.templateDef;
		
		assertTrue(tplInstance.getLexicalParent() != null);
		assertTrue(areEqual(tplInstance.getNameSourceRangeOrNull(), templateDef.getNameSourceRangeOrNull()));
		assertTrue(tplInstance.isCompleted());
//		assertTrue(tplInstance.getOwnerElement() == tplInstance.templateDef.getParent());
		assertTrue(tplInstance.getSemanticContainerKey() == templateDef.getSemanticContainerKey());
		assertTrue(tplInstance.getElementSemanticContext(tplRef.context) == tplRef.context); /*FIXME: BUG here*/
		ASTSourceRangeChecker.checkConsistency(tplInstance);
		
		namedElementChecker("$_tests/Tpl!(int)").evaluate(tplInstance);
		
		SourceEquivalenceChecker.assertCheck(tplInstance.toStringAsCode(), 
			"Tpl!(int){ @TYPE1 = int; }{ TYPE1 foo; }");
		
		PickedElement<TemplateInstance> tplInstancePick = picked(tplInstance, tplRef.context);
		ISemanticContext context = tplInstancePick.context;
		
		test_resolveElement(tplInstancePick, null, expectNotAValue(tplInstance));
		test_resolveSearchInMembersScope(tplInstancePick, "foo");
		
		CompletionScopeLookup search = allElementsSearch(tplInstance);
		tplInstance.performNameLookup(search);
		checkNamedElements(search.getMatchedElements(), array("@TYPE1 = int;", "$_tests/", "$_tests/Tpl"));
		
		Reference ref = NodeFinderByString.find(tplInstance, Reference.class, "TYPE1");
		INamedElement typeAlias = resolveTarget(ref, tplInstancePick);
		
		test_resolveElement(picked(typeAlias, context), "$/int", expectNotAValue(typeAlias));
	}
	
}