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
import dtool.engine.modules.NullModuleResolver;
import dtool.parser.DeeParsingChecks.DeeTestsChecksParser;
import dtool.tests.CommonDToolTest;

public class DefVariableSemantics_Test extends CommonDToolTest {
	
	@Test
	public void test() throws Exception { test$(); }
	public void test$() throws Exception {
		
		testResolveEffectiveType("int xxx = 123;", "xxx", "int");
		testResolveEffectiveType("int z, xxx = 123;", "xxx", "int");
		
//		testResolveEffectiveType("auto x2 = 123;", "x2", "int");
		
		testResolveEffectiveType("auto xpto;", "xpto", null);
		testResolveEffectiveType("auto z, xpto;", "xpto", null);
		//testResolveEffectiveType("enum xpto;", "xpto", null);
		
		testResolveEffectiveType("auto xpto = ref_not_found;", "xpto", null);
		testResolveEffectiveType("auto z = 1, xpto = ref_not_found;", "xpto", null);
		testResolveEffectiveType("enum xpto = ref_not_found;", "xpto", null);
		
		//testResolveEffectiveType("module mod; class Foo {}; Foo foovar; auto xpto = foovar;", "xpto", "mod.Foo");
	}
	
	protected void testResolveEffectiveType(String source, String defNameMarker, String expectedTypeFQN) {
		testResolveEffectiveType(source, source.indexOf(defNameMarker), expectedTypeFQN);
	}
	
	protected void testResolveEffectiveType(String source, int offset, String expectedTypeFQN) {
		NullModuleResolver mr = new NullModuleResolver();
		INamedElement effectiveType = parseDefinitionVar(source, offset).getNodeSemantics().resolveEffectiveType(mr);
		if(expectedTypeFQN == null) {
			assertTrue(effectiveType == null);
			return;
		}
		assertEquals(effectiveType.getFullyQualifiedName(), expectedTypeFQN);
	}
	
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
	
}