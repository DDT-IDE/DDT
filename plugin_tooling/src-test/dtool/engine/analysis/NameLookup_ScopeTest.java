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

import melnorme.lang.tooling.ast.IModuleNode;
import melnorme.lang.tooling.ast.SourceElement;
import melnorme.lang.tooling.ast_actual.ASTNode;
import melnorme.lang.tooling.engine.OverloadedNamedElement;
import melnorme.lang.tooling.engine.PickedElement;
import melnorme.lang.tooling.engine.scoping.CommonScopeLookup;
import melnorme.lang.tooling.engine.scoping.IScopeElement;
import melnorme.lang.tooling.engine.scoping.ResolutionLookup;
import melnorme.lang.tooling.symbols.INamedElement;
import melnorme.utilbox.collections.ArrayList2;
import melnorme.utilbox.core.fntypes.Function;
import melnorme.utilbox.misc.ArrayUtil;

import org.junit.Test;

import dtool.engine.ResolvedModule;

public class NameLookup_ScopeTest extends CommonNodeSemanticsTest {
	
	protected static final String DEFAULT_MARKER = "/*MARKER*/";
	
	protected ASTNode pickedNode;
	
	protected ResolutionLookup doResolutionLookup(String source, String offsetMarker) throws ExecutionException {
		ResolvedModule resolvedModule = parseModule_(source);
		return doResolutionLookup(resolvedModule, offsetMarker);
	}
	
	protected ResolutionLookup doResolutionLookup(ResolvedModule resolvedModule, String offsetMarker) {
		PickedElement<ASTNode> pick = pickElement(resolvedModule, offsetMarker, ASTNode.class);
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
		Set<IScopeElement> searchedScopes = lookup.getSearchedScopes();
		searchedScopes.remove(ASTNode.getPrimitivesScope());
		assertTrue(searchedScopes.size() == 1);
		assertTrue(containsModuleScope(lookup, pickedNode) == false);
	}
	
	protected boolean containsModuleScope(CommonScopeLookup lookup, ASTNode node) {
		IScopeElement moduleNode = (IScopeElement) node.getModuleNode();
		return lookup.getSearchedScopes().contains(moduleNode);
	}
	
	/* -----------------  ----------------- */
	
	protected ResolutionLookup testNameOverloadFromFile(String file, String[] expectedResults) {
		return testNameOverloadFromFile(file, DEFAULT_MARKER, expectedResults);
	}
	
	protected ResolutionLookup testNameOverloadFromFile(String file, String markerString, 
			String[] expectedResults) {
		ResolutionLookup lookup = doResolutionLookup(getTesterModule(file), markerString);
		return checkLookupResult(lookup, expectedResults);
	}
	
	protected ResolutionLookup testNameOverload(String source, String markerString, 
			String[] expectedResults) {
		ResolvedModule resolvedModule = parseModule_(source, TESTER_TestsBundle.getPath());
		ResolutionLookup lookup = doResolutionLookup(resolvedModule, markerString);
		return checkLookupResult(lookup, expectedResults);
	}
	
	@Test
	public void testOverloads() throws Exception { testOverloads$(); }
	public void testOverloads$() throws Exception {
		
		testNameOverloadFromFile("scope_overload1.d", array(
			"void xxx;",
			"int xxx;"
		));
		
		// Test across multiple scopes
		testNameOverloadFromFile("scope_overload2.d", array(
			"void xxx;",
			"int xxx;"
		));
		
		// Test versus a secondary namespace match.
		testNameOverloadFromFile("scope_overload3_vsImport.d", array(
			"void xxx;"
		));
		
		testNameOverloadFromFile("scope_overload3_vsImport.d", "/*MARKER2*/", array(
			"module[xxx]"
		));
		
		// Test namespace aggregation
		checkLookupResult(doResolutionLookup("import xxx.foo; import xxx.bar; import xxx.; /*M*/", "/*M*/"), 
			array(
			"PNamespace[xxx]"
		));
	}
	
	protected ResolutionLookup checkLookupResult(ResolutionLookup lookup, String[] expectedResults) {
		INamedElement matchedElement = lookup.getMatchedElement();
		ArrayList2<INamedElement> overloadedElements;
		
		if(matchedElement instanceof OverloadedNamedElement) {
			OverloadedNamedElement overloadedNamedElement = (OverloadedNamedElement) matchedElement;
			overloadedElements = overloadedNamedElement.getOverloadedElements();
		} else {
			overloadedElements = new ArrayList2<>(matchedElement);
		}
		
		Object[] results = ArrayUtil.map(overloadedElements, new Function<INamedElement, String>() {
			@Override
			public String evaluate(INamedElement namedElement) {
				if(namedElement instanceof SourceElement) {
					SourceElement sourceElement = (SourceElement) namedElement;
					return sourceElement.toStringAsCode();
				} else {
					return namedElement.toString();
				}
			}
		});
		
		assertEqualArrays(results, expectedResults);
		
		return lookup;
	}
	
}