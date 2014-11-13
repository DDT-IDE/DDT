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
import melnorme.lang.tooling.ast_actual.ASTNode;

import org.junit.Test;

import dtool.ast.declarations.DeclarationAttrib;
import dtool.ast.declarations.IDeclaration;
import dtool.ast.definitions.DefinitionVariable;
import dtool.parser.DeeParsingChecks.DeeTestsChecksParser;
import dtool.tests.CommonDToolTest;

public class DeclAttrib_SemanticsTest extends CommonDToolTest {
	
	@Test
	public void test() throws Exception { test$(); }
	public void test$() throws Exception {
		checkNode(parseDefinitionAlias("auto x;").getSingleDeclaration(), 
			DefinitionVariable.class, "x;");
		
		checkNode(parseDefinitionAlias("auto const x = 1;").getSingleDeclaration(), 
			DefinitionVariable.class, "x = 1;");
		
		checkNode(parseDefinitionAlias("const: public x = 1;").getSingleDeclaration(), 
			null, null);
		checkNode(parseDefinitionAlias("const { int x = 1; }").getSingleDeclaration(), 
			null, null);
	}
	
	protected DeclarationAttrib parseDefinitionAlias(String source) {
		return new DeeTestsChecksParser(source).parseDeclarationAttrib(false).getNode();
	}
	
	protected void checkNode(IDeclaration decl, Class<DefinitionVariable> klass, String expectedSource) {
		if(klass == null) {
			assertTrue(decl == null);
			return;
		}
		assertInstance(decl, klass);
		ASTNode node = (ASTNode) decl;
		assertEquals(node.toStringAsCode(), expectedSource);
	}
	
}