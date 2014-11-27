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
import melnorme.lang.tooling.engine.PickedElement;
import melnorme.lang.tooling.engine.scoping.CommonScopeLookup;
import melnorme.lang.tooling.engine.scoping.IScopeElement;
import melnorme.lang.tooling.engine.scoping.ResolutionLookup;

import org.junit.Test;


public class Scope_SemanticsTest extends CommonNodeSemanticsTest {
	
	@Test
	public void testShadowing() throws Exception { testShadowing$(); }
	public void testShadowing$() throws Exception {
		PickedElement<ASTNode> pick = 
				parseTestElement("void foo; class Blah { int foo = 8; } ", "8", ASTNode.class);
		
		ASTNode node = pick.element;
		CommonScopeLookup lookup = new ResolutionLookup("foo", node.getModuleNode(), true, pick.context);
		node.performNameLookup(lookup);
		
		resultsChecker(lookup, true, true, true).checkResults(array(
			"_tests/Blah.foo"
		));
		
		// Test that we did look up redundant scopes
		assertTrue(lookup.getSearchedScopes().size() == 1);
		assertTrue(containsModuleScope(lookup, node) == false);
	}
	
	protected boolean containsModuleScope(CommonScopeLookup lookup, ASTNode node) {
		IScopeElement moduleNode = (IScopeElement) node.getModuleNode();
		return lookup.getSearchedScopes().contains(moduleNode);
	}
	
}