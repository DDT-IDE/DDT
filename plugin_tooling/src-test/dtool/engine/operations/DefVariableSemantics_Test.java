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
package dtool.engine.operations;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import org.junit.Test;

import dtool.ast.ASTNode;
import dtool.ast.ASTNodeFinder;
import dtool.ast.definitions.DefSymbol;
import dtool.ast.definitions.INamedElement;
import dtool.ast.definitions.Module;
import dtool.engine.common.NotAValueErrorElement;
import dtool.engine.modules.NullModuleResolver;
import dtool.parser.DeeParsingChecks.DeeTestsChecksParser;
import dtool.tests.CommonDToolTest;

public class DefVariableSemantics_Test extends CommonDToolTest {
	
	protected static final String SOURCE_PREFIX1 = "module mod; class Foo {}; Foo foovar;\n";
	
	protected IVarDefinitionLike parseDefinitionVar(String source, int offset) {
		DeeTestsChecksParser parser = new DeeTestsChecksParser(source);
		Module module = parser.parseModule("_tests", null).getNode();
		ASTNode node = ASTNodeFinder.findElement(module, offset);
		if(node instanceof DefSymbol) {
			DefSymbol defSymbol = (DefSymbol) node;
			return assertInstance(defSymbol.getDefUnit(), IVarDefinitionLike.class);
		}
		return assertInstance(node, IVarDefinitionLike.class);
	}
	
	@Test
	public void test() throws Exception { test$(); }
	public void test$() throws Exception {
		
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
			testResolveEffectiveType(source, "xxx", expectedTypeFQN, errorSuffix);
		}
	}
	
	protected void testResolveEffectiveType(String source, String defNameMarker, String expectedTypeFQN, 
			String errorSuffix) {
		testResolveEffectiveType(source, source.indexOf(defNameMarker), expectedTypeFQN, errorSuffix);
	}
	
	protected void testResolveEffectiveType(String source, int offset, String expectedTypeFQN, String errorSuffix) {
		NullModuleResolver mr = new NullModuleResolver();
		INamedElement effectiveType = parseDefinitionVar(source, offset).getNodeSemantics().resolveEffectiveType(mr);
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