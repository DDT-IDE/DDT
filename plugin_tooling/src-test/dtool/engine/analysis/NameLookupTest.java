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

import java.util.concurrent.ExecutionException;

import melnorme.lang.tooling.ast.IModuleNode;
import melnorme.lang.tooling.ast.SourceElement;
import melnorme.lang.tooling.ast_actual.ASTNode;
import melnorme.lang.tooling.engine.PickedElement;
import melnorme.lang.tooling.engine.scoping.CommonScopeLookup;
import melnorme.lang.tooling.engine.scoping.IScopeElement;
import melnorme.lang.tooling.engine.scoping.ResolutionLookup;
import melnorme.lang.tooling.symbols.INamedElement;
import melnorme.utilbox.collections.ArrayList2;
import melnorme.utilbox.core.fntypes.Function;
import melnorme.utilbox.misc.ArrayUtil;

import org.junit.Test;

public class NameLookupTest extends CommonNodeSemanticsTest {
	
	protected ASTNode pickedNode;
	
	protected ResolutionLookup doResolutionLookup(String source, String offsetSource) throws ExecutionException {
		PickedElement<ASTNode> pick = parseElement(source, offsetSource, ASTNode.class);
		return doResolutionLookup(pick, "xxx");
	}
	
	protected ResolutionLookup doResolutionLookup(PickedElement<ASTNode> pick, String name) {
		pickedNode = pick.element;
		IModuleNode moduleNode = pickedNode.getModuleNode();
		ResolutionLookup lookup = new ResolutionLookup(name, moduleNode, pickedNode.getOffset(), true, pick.context);
		pickedNode.performNameLookup(lookup);
		return lookup;
	}
	
	@Test
	public void testShadowing() throws Exception { testShadowing$(); }
	public void testShadowing$() throws Exception {
		CommonScopeLookup lookup = doResolutionLookup("void xxx; class Blah { int xxx = 8; } ", "8");
		
		resultsChecker(lookup).checkResults(array(
			"_tests/Blah.xxx"
		));
		
		// Test that we did look up redundant scopes
		assertTrue(lookup.getSearchedScopes().size() == 1);
		assertTrue(containsModuleScope(lookup, pickedNode) == false);
	}
	
	protected boolean containsModuleScope(CommonScopeLookup lookup, ASTNode node) {
		IScopeElement moduleNode = (IScopeElement) node.getModuleNode();
		return lookup.getSearchedScopes().contains(moduleNode);
	}
	
	@Test
	public void testOverloads() throws Exception { testOverloads$(); }
	public void testOverloads$() throws Exception {
		
		testScopeOverloadResolutionLookup("scope_overload1.d", array(
			"void xxx;",
			"int xxx;"
		));
		
		// Test across multiple scopes
		testScopeOverloadResolutionLookup("scope_overload2.d", array(
			"void xxx;",
			"int xxx;"
		));
		
		// Test versus an secondary namespace match.
//		testScopeOverloadResolutionLookup("scope_overload3_vsImport.d", array(
//			"void xxx;"
//		));
//		
//		testScopeOverloadResolutionLookup("scope_overload3_vsImport.d", "/*MARKER2*/", array(
//			"import xxx;"
//		));
	}
	
	protected ResolutionLookup testScopeOverloadResolutionLookup(String file, String[] expectedResults) {
		return testScopeOverloadResolutionLookup(file, "/*MARKER*/", expectedResults);
	}
	
	protected ResolutionLookup testScopeOverloadResolutionLookup(String file, String markerString, 
			String[] expectedResults) {
		ResolutionLookup lookup = doResolutionInScopeTesterModule(file, markerString);
		ArrayList2<INamedElement> matchingElementEntry = lookup.getMatchingElementEntry();
		Object[] results = ArrayUtil.map(matchingElementEntry, new Function<INamedElement, String>() {
			@Override
			public String evaluate(INamedElement obj) {
				return ((SourceElement) obj).toStringAsCode();
			}
		});
		
		assertEqualArrays(results, expectedResults);
		
		return lookup;
	}
	
	protected ResolutionLookup doResolutionInScopeTesterModule(String fileName, String marker) {
		PickedElement<ASTNode> pickElement = pickElement(getTesterModule(fileName), marker, ASTNode.class);
		return doResolutionLookup(pickElement, "xxx");
	}
	
}