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

import static dtool.engine.analysis.LanguageIntrinsics_SemanticsTest.INT_PROPERTIES;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import static melnorme.utilbox.misc.ArrayUtil.concat;
import melnorme.lang.tooling.bundles.MockSemanticResolution;
import melnorme.lang.tooling.engine.INamedElementSemantics;
import melnorme.lang.tooling.engine.resolver.NotAValueErrorElement;
import melnorme.lang.tooling.symbols.INamedElement;

import org.junit.Test;

public class DefVariable_SemanticsTest extends DefElement_CommonTest {
	
	@Override
	public void test_resolveSearchInMembersScope________() throws Exception {
		defVar_testResolveSearchInMembers("auto xxx = true; ", COMMON_PROPERTIES);
		defVar_testResolveSearchInMembers("auto xxx = 123; ", concat(COMMON_PROPERTIES, INT_PROPERTIES));
		defVar_testResolveSearchInMembers("auto xxx = ; ");
		defVar_testResolveSearchInMembers("auto xxx = notFOUND; ");
	}
	
	protected void defVar_testResolveSearchInMembers(String source, String... expectedResults) {
		IVarDefinitionLike defVar = parseDefinitionVar(source, source.indexOf("xxx"));
		testResolveSearchInMembersScope(defVar, expectedResults);
	}
	
	protected IVarDefinitionLike parseDefinitionVar(String source, int offset) {
		return parseSourceAndFindNode(source, offset, IVarDefinitionLike.class);
	}
	
	/* -----------------  ----------------- */
	
	protected static final String SOURCE_PREFIX1 = "module mod; class Foo {}; Foo foovar;\n";
	
	@Override
	public void test_resolveTypeForValueContext________() throws Exception {
		test_resolveTypeForValueContext("char xxx;", "char");
		test_resolveTypeForValueContext("char z, xxx;", "char");
		
		test_resolveTypeForValueContext("NotFound xxx;", null, true);
		
		test_resolveTypeForValueContext("auto xxx = 123;", "int");
		test_resolveTypeForValueContext("auto z, xxx = 123;", "int");
		test_resolveTypeForValueContext("enum xxx = 123;", "int");
	}
	
	@Test
	public void testResolveEffectiveType() throws Exception { testResolveEffectiveType$(); }
	public void testResolveEffectiveType$() throws Exception {
		
		testMultiple_ResolveEffectiveType(array(
			"int xxx = 123;",
			"int z, xxx = 123;",
			"int xxx = int;"
		), "int", null);
		
		testMultiple_ResolveEffectiveType(array(
			"auto xxx = 123;",
			"auto z, xxx = 123;",
			"enum xxx = 123;"
		), "int", null);
		
		testMultiple_ResolveEffectiveType(array(
			"auto xxx = int;",
			"auto z, xxx = int;",
			"enum xxx = int;"
		), "int", NotAValueErrorElement.ERROR_IS_NOT_A_VALUE);
		

		testMultiple_ResolveEffectiveType(array(
			"auto xxx;",
			"auto z, xxx;"
		), null, null);
		
		testMultiple_ResolveEffectiveType(array(
			"auto xxx = ref_not_found;",
			"auto z = 1, xxx = ref_not_found;",
			"enum xxx = ref_not_found;"
		), null, null);
		
		testMultiple_ResolveEffectiveType(array(
			SOURCE_PREFIX1+"auto xxx = foovar;",
			SOURCE_PREFIX1+"auto z = 1, xxx = foovar;",
			SOURCE_PREFIX1+"enum xxx = foovar;"
		), "mod.Foo", null);
		
	}
	
	protected void testMultiple_ResolveEffectiveType(String[] sources, String expectedTypeFQN, String errorSuffix) {
		for (String source : sources) {
			testResolveEffectiveType(source, source.indexOf("xxx"), expectedTypeFQN, errorSuffix);
		}
	}
	
	protected void testResolveEffectiveType(String source, int offset, String expectedTypeFQN, String errorSuffix) {
		MockSemanticResolution mr = new MockSemanticResolution();
		INamedElementSemantics nodeSemantics = parseDefinitionVar(source, offset).getNodeSemantics();
		INamedElement effectiveType = nodeSemantics.resolveTypeForValueContext(mr);
		if(expectedTypeFQN == null || effectiveType == null) {
			assertTrue(expectedTypeFQN == null && effectiveType == null);
			return;
		}
		assertEquals(effectiveType.getFullyQualifiedName(), expectedTypeFQN);
		if(errorSuffix != null) {
			assertTrue(effectiveType.getExtendedName().endsWith(errorSuffix));
		}
	}
	
}