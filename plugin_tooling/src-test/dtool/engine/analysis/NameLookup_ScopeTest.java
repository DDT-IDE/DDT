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

import java.util.Set;
import java.util.concurrent.ExecutionException;

import melnorme.lang.tooling.ast_actual.ASTNode;
import melnorme.lang.tooling.engine.scoping.IScopeElement;
import melnorme.lang.tooling.engine.scoping.ResolutionLookup;

import org.junit.Test;

public class NameLookup_ScopeTest extends CommonLookupTest {
	
	@Test
	public void testShadowing() throws Exception { testShadowing$(); }
	public void testShadowing$() throws Exception {
		ResolutionLookup lookup = doResolutionLookup("void xxx; class Blah { int xxx = xxx/*M*/; } ", "xxx/*M*/");
		
		resultsChecker(lookup).checkResults(array(
			"_tests/Blah.xxx"
		));
		
		// Test that we did look up redundant scopes
		Set<IScopeElement> searchedScopes = lookup.getSearchedScopes();
		searchedScopes.remove(ASTNode.getPrimitivesScope());
		assertTrue(searchedScopes.size() == 1);
		
		assertTrue(lookup.getSearchedScopes().contains((IScopeElement) lookup.refOriginModule) == false);
	}
	
	protected ResolutionLookup doResolutionLookup(String source, String offsetMarker) throws ExecutionException {
		return doResolutionLookup(parseModule_(source), offsetMarker);
	}
	
	protected static final String DEFAULT_MARKER = "/*MARKER*/";
	
	@Test
	public void testOverloads() throws Exception { testOverloads_________(); }
	public void testOverloads_________() throws Exception {
		
		testLookupFromFile("scope_overload1.d", DEFAULT_MARKER, 
			checkNameError(
			"void xxx;",
			"int xxx;"
		));
		
		// Test across multiple scopes
		testLookupFromFile("scope_overload2.d", DEFAULT_MARKER,
			checkNameError(
			"void xxx;",
			"int xxx;"
		));
		
		// Test versus a secondary namespace match.
		testLookupFromFile("scope_overload3_vsImport.d", DEFAULT_MARKER, 
			checkSingleResult(
			"void xxx;"
		));
		
		testLookupFromFile("scope_overload3_vsImport.d", "/*MARKER2*/", 
			checkSingleResult(
			"module[xxx]"
		));
		
		
		testLookup(parseModule_("int xxx; void func() { Blah xxx; char xxx; auto _ = xxx/*M*/; }"), 
			checkNameError("Blah xxx;", "char xxx;") 
		);
		
	}
	
}