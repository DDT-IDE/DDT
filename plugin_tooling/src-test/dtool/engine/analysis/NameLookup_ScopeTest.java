/*******************************************************************************
 * Copyright (c) 2014 Bruno Medeiros and other Contributors.
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

import java.util.Set;

import melnorme.lang.tooling.ast_actual.ASTNode;
import melnorme.lang.tooling.engine.PickedElement;
import melnorme.lang.tooling.engine.scoping.IScopeElement;
import melnorme.lang.tooling.engine.scoping.ResolutionLookup;
import melnorme.utilbox.collections.ArrayList2;

import org.junit.Test;

import dtool.ast.declarations.DeclBlock;
import dtool.ast.definitions.DefinitionClass;
import dtool.ast.references.NamedReference;

public class NameLookup_ScopeTest extends CommonLookupTest {
	
	@Test
	public void testShadowing() throws Exception { testShadowing$(); }
	public void testShadowing$() throws Exception {
		PickedElement<NamedReference> pick = parseElement(
			"void xxx; class Blah { int xxx = xxx/*M*/; } ", "xxx/*M*/", NamedReference.class);
		ResolutionLookup lookup = pick.element.getSemantics(pick.context).doResolutionLookup();
		
		resultsChecker(lookup).checkResults(array(
			"_tests/Blah.xxx"
		));
		
		// Test that we didnt look up redundant scopes
		Set<IScopeElement> searchedScopes = lookup.getSearchedScopes();
		searchedScopes.remove(ASTNode.getPrimitivesScope());
		assertTrue(searchedScopes.size() == 1);
		
		IScopeElement searchedScope = new ArrayList2<>(searchedScopes).get(0);
		assertTrue(((DeclBlock) searchedScope).getLexicalParent() instanceof DefinitionClass);
	}
	
	protected static final String DEFAULT_MARKER = "/*MARKER*/";
	
	@Test
	public void testOverloads() throws Exception { testOverloads_________(); }
	public void testOverloads_________() throws Exception {
		
		testLookup(parseModule_("void xxx; int xxx; auto _ = xxx/*M*/; "), 
			checkNameConflict("void xxx;", "int xxx;")
		);
		
		// Test across multiple scopes
		
		testLookup(parseModule_(
			" struct xxx ; class Foo {" +
				"void func() {" +
				"	void xxx; int xxx; 1 + xxx/*M*/;" +
				"}" +
				"char xxx;" +
			"}"
			), 
			checkNameConflict("void xxx;", "int xxx;")
		);
		
		
		testLookup(parseModule_("int xxx; void func() { Blah xxx; char xxx; auto _ = xxx/*M*/; }"), 
			checkNameConflict("Blah xxx;", "char xxx;") 
		);
		
	}
	
}