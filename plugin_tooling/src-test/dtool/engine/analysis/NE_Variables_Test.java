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

import static dtool.engine.analysis.NE_LanguageIntrinsics_SemanticsTest.INT_PROPERTIES;
import melnorme.lang.tooling.engine.ErrorElement.SyntaxErrorElement;
import melnorme.lang.tooling.engine.PickedElement;
import melnorme.lang.tooling.engine.resolver.INamedElementSemanticData;
import melnorme.lang.tooling.symbols.INamedElement;

import org.junit.Test;

public class NE_Variables_Test extends NamedElement_CommonTest {
	
	@Override
	public void test_NamedElement________() throws Exception {
		test_NamedElement_Concrete(parseNamedElement("char xxx;"), "$/char", INT_PROPERTIES);
		test_NamedElement_Concrete(parseNamedElement("char z, xxx;"), "$/char", INT_PROPERTIES);
		
		test_NamedElement_Concrete(parseNamedElement("NotFound xxx;"), expectNotFound("NotFound"), NO_MEMBERS);
		
		test_NamedElement_Concrete(parseNamedElement("auto xxx = true;"), "$/bool", COMMON_PROPERTIES);
		test_NamedElement_Concrete(parseNamedElement("auto xxx = 123;"), "$/int", INT_PROPERTIES);
		test_NamedElement_Concrete(parseNamedElement("auto z, xxx = 123;"), "$/int", INT_PROPERTIES);
		test_NamedElement_Concrete(parseNamedElement("enum xxx = 123;"), "$/int", INT_PROPERTIES);
		

		test_NamedElement_Concrete(parseNamedElement("auto xxx = ; "), "#InvalidExp", NO_MEMBERS);
		test_NamedElement_Concrete(parseNamedElement("auto xxx = missing; "), expectNotFound("missing"), NO_MEMBERS);

	}
	
	protected static final String SOURCE_PREFIX1 = "module mod; class Foo {}; Foo foovar;\n";
	
	@Test
	public void testResolveEffectiveType() throws Exception { testResolveEffectiveType$(); }
	public void testResolveEffectiveType$() throws Exception {
		
		testMultiple_ResolveEffectiveType2(array(
			"int xxx = 123;",
			"int z, xxx = 123;",
			"int xxx = int;"
		), "$/int");
		
		testMultiple_ResolveEffectiveType2(array(
			"auto xxx = 123;",
			"auto z, xxx = 123;",
			"enum xxx = 123;"
		), "$/int");
		
		testMultiple_ResolveEffectiveType2(array(
			"auto xxx = int;",
			"auto z, xxx = int;",
			"enum xxx = int;"
		), expectNotAValue("int"));
		

		testMultiple_ResolveEffectiveType2(array(
			"auto xxx;",
			"auto z, xxx;"
		), SyntaxErrorElement.SYNTAX_ERROR__Name);
		
		testMultiple_ResolveEffectiveType2(array(
			"auto xxx = ref_not_found;",
			"auto z = 1, xxx = ref_not_found;",
			"enum xxx = ref_not_found;"
		), expectNotFound("ref_not_found"));
		
		testMultiple_ResolveEffectiveType2(array(
			SOURCE_PREFIX1+"auto xxx = foovar;",
			SOURCE_PREFIX1+"auto z = 1, xxx = foovar;",
			SOURCE_PREFIX1+"enum xxx = foovar;"
		), "$mod/Foo");
		
	}
	
	protected void testMultiple_ResolveEffectiveType2(String[] sources, String expectedTypeFQN) {
		for (String source : sources) {
			testResolveEffectiveType(source, expectedTypeFQN);
		}
	}
	
	protected void testResolveEffectiveType(String source, String expectedResult) {
		PickedElement<INamedElement> pickedElement = parseNamedElement(source);
		INamedElementSemanticData nodeSemantics = pickedElement.element.getSemantics(pickedElement.context);
		INamedElement effectiveType = nodeSemantics.resolveTypeForValueContext();
		
		namedElementChecker(expectedResult).evaluate(effectiveType);
	}
	
}