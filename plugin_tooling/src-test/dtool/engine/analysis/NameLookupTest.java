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

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import melnorme.lang.tooling.ast.IModuleNode;
import melnorme.lang.tooling.ast.SourceElement;
import melnorme.lang.tooling.ast_actual.ASTNode;
import melnorme.lang.tooling.context.EmptySemanticResolution;
import melnorme.lang.tooling.context.ISemanticContext;
import melnorme.lang.tooling.context.ModuleFullName;
import melnorme.lang.tooling.context.ModuleSourceException;
import melnorme.lang.tooling.engine.ErrorElement;
import melnorme.lang.tooling.engine.PickedElement;
import melnorme.lang.tooling.engine.scoping.CommonScopeLookup;
import melnorme.lang.tooling.engine.scoping.IScopeElement;
import melnorme.lang.tooling.engine.scoping.ResolutionLookup;
import melnorme.lang.tooling.symbols.IConcreteNamedElement;
import melnorme.lang.tooling.symbols.INamedElement;
import melnorme.utilbox.collections.ArrayList2;
import melnorme.utilbox.core.fntypes.Function;
import melnorme.utilbox.misc.ArrayUtil;

import org.junit.Test;

import dtool.ast.references.NamedReference;
import dtool.ast.references.RefIdentifier;
import dtool.engine.ResolvedModule;

public class NameLookupTest extends CommonNodeSemanticsTest {
	
	protected IConcreteNamedElement doResolveConcreteElement(String source, String marker) {
		PickedElement<INamedElement> pickedElement = parseElement(source, marker, INamedElement.class);
		ISemanticContext context = pickedElement.context;
		return pickedElement.element.resolveConcreteElement(context);
	}
	
	protected IConcreteNamedElement doResolveConcreteElementForRef(String source, String marker) {
		PickedElement<NamedReference> pickedElement = parseElement(source, marker, NamedReference.class);
		ISemanticContext context = pickedElement.context;
		return pickedElement.element.resolveTargetElement(context).resolveConcreteElement(context);
	}
	
	protected INamedElement doResolveNamedElementForRef(String source, String marker) {
		PickedElement<RefIdentifier> pickedElement = parseElement(source, marker, RefIdentifier.class);
		ISemanticContext context = pickedElement.context;
		return pickedElement.element.resolveTargetElement(context);
	}
	
	protected ASTNode pickedNode;
	
	protected ResolutionLookup doResolutionLookup(String source, String offsetMarker) throws ExecutionException {
		PickedElement<ASTNode> pick = parseElement(source, offsetMarker, ASTNode.class);
		return doResolutionLookup(pick, "xxx");
	}
	
	protected ResolutionLookup doResolutionLookup(PickedElement<ASTNode> pick, String name) {
		pickedNode = pick.element;
		IModuleNode moduleNode = pickedNode.getModuleNode();
		ResolutionLookup lookup = new ResolutionLookup(name, moduleNode, pickedNode.getOffset(), true, pick.context);
		pickedNode.performNameLookup(lookup);
		return lookup;
	}
	
	protected ResolutionLookup doResolutionInScopeTesterBundle(String fileName, String marker) {
		PickedElement<ASTNode> pickElement = pickElement(getTesterModule(fileName), marker, ASTNode.class);
		return doResolutionLookup(pickElement, "xxx");
	}
	
	@Test
	public void testNotFound() throws Exception { testNotFound_____(); }
	public void testNotFound_____() throws Exception {
		
		checkResultNotFound(
			doResolveNamedElementForRef("int blah = xxx;", "xxx"));

		checkResultNotFound(
			doResolveNamedElementForRef("alias A = B; alias B = xxx;", "xxx"));
		
		checkResultNotFound(
			doResolveConcreteElementForRef("alias A = B; alias B = xxx; alias _ = A/**/;", "A/**/;"));
		
		checkResultNotFound(
			doResolveConcreteElementForRef("import not_found; alias _ = not_found/**/;", "not_found/**/;"));
		
		testModuleParseException();
	}
	
	protected void checkResultNotFound(INamedElement result) {
		assertTrue(result.getName().equals(ErrorElement.NOT_FOUND__Name));
		assertTrue(result.getNameInRegularNamespace() == null);
	}
	
	protected void testModuleParseException() {
		NamedReference element = parseElement("import not_found;", "not_found;", NamedReference.class).element;
		ISemanticContext context = new EmptySemanticResolution() {
			
			@Override
			protected ResolvedModule getBundleResolvedModule(ModuleFullName moduleFullName)
					throws ModuleSourceException {
				throw new ModuleSourceException(new IOException("FAKE_IO_ERROR"));
			}
		};
		
		IConcreteNamedElement resolvedElement = element.resolveTargetElement(context).resolveConcreteElement(context);
		assertTrue(resolvedElement.getName().equals("not_found"));
		assertTrue(resolvedElement.getNameInRegularNamespace() == null);
		assertTrue(resolvedElement instanceof ErrorElement);
	}
	
	/* -----------------  ----------------- */
	
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
		ResolutionLookup lookup = doResolutionInScopeTesterBundle(file, markerString);
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
	
	/* -----------------  ----------------- */
	
	@Test
	public void testLoop() throws Exception { testLoop_____(); }
	public void testLoop_____() throws Exception {
		
		checkLoopResult(
			doResolveConcreteElementForRef("alias A= B; alias B = A/**/;", "A/**/"));
		
		checkLoopResult(
			doResolveConcreteElementForRef("alias A= B; alias B = C; alias C = A/**/;", "A/**/"));
		
		checkLoopResult(
			doResolveConcreteElement("alias A= B; alias B = C; alias C = A/**/;", "C = A"));
		
		checkResultNotFound(
			doResolveConcreteElementForRef("B A; A B; auto _ = A.xxx;", "xxx"));
		checkResultNotFound(
			doResolveConcreteElementForRef("alias A= B; alias B = A; auto _ = A.xxx;", "xxx"));
		
		
		checkResultNotFound(
			doResolveConcreteElementForRef("class A : A { }; auto _ = A.xxx;", "xxx"));
		checkResultNotFound(
			doResolveConcreteElementForRef("class A : B { }; class B : A { }; auto _ = A.xxx;", "xxx"));
		checkResultNotFound(
			doResolveConcreteElementForRef("class A : A; auto _ = A.xxx;", "xxx"));
	}
	
	protected void checkLoopResult(INamedElement result) {
		assertTrue(result.getName().equals(ErrorElement.LOOP_ERROR_ELEMENT__Name));
		assertTrue(result.getNameInRegularNamespace() == null);
	}
	
}