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
import melnorme.lang.tooling.ast.util.ASTSourceRangeChecker;
import melnorme.lang.tooling.engine.PickedElement;
import melnorme.lang.tooling.symbols.INamedElement;

import org.junit.Test;

import dtool.ast.references.RefTemplateInstance;
import dtool.ast.references.Reference;
import dtool.engine.analysis.templates.TemplateInstance;
import dtool.parser.SourceEquivalenceChecker;


public class Template_SemanticsTest extends CommonNodeSemanticsTest {
	
	protected static <T extends Reference> INamedElement resolveTarget(PickedElement<T> ref) {
		return ref.element.resolveTargetElement(ref.context);
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
			TPL_DEF_A + "Tpl!(int)/*M*/ ref1", "/*M*/", RefTemplateInstance.class);
		
		TemplateInstance tplInstance = assertCast(resolveTarget(tplRef), TemplateInstance.class);
		
		assertTrue(tplInstance.isCompleted());
		assertTrue(tplInstance.getElementSemanticContext(tplRef.context) == tplRef.context);
		ASTSourceRangeChecker.checkConsistency(tplInstance);
		
		namedElementChecker("$_tests/Tpl!(int)").evaluate(tplInstance);
		
		SourceEquivalenceChecker.assertCheck(tplInstance.toStringAsCode(), 
			"Tpl!(int){ @TYPE1 = int; }{ TYPE1 foo; }");
		
		PickedElement<TemplateInstance> tplInstancePick = picked(tplInstance, tplRef.context);
		NamedElement_CommonTest.test_resolveElement(tplInstancePick, null, 
			expectNotAValue(tplInstance.getFullyQualifiedName()));
		
		NamedElement_CommonTest.test_resolveSearchInMembersScope(tplInstancePick,
			"foo");
		
	}

}