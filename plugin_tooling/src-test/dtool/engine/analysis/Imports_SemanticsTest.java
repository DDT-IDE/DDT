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

import static dtool.engine.analysis.Resolvables_SemanticsTest.testResolveElement;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.util.concurrent.ExecutionException;

import melnorme.lang.tooling.engine.ErrorElement;
import melnorme.lang.tooling.engine.PickedElement;
import melnorme.lang.tooling.engine.completion.CompletionScopeLookup;
import melnorme.lang.tooling.engine.resolver.ResolvableResult;
import melnorme.lang.tooling.symbols.IConcreteNamedElement;
import melnorme.lang.tooling.symbols.INamedElement;

import org.junit.Test;

import dtool.ast.definitions.Module;
import dtool.ast.references.NamedReference;
import dtool.ast.references.RefModule;


public class Imports_SemanticsTest extends CommonNodeSemanticsTest {
	
	@Test
	public void testImports() throws Exception { testImports$(); }
	public void testImports$() throws Exception {
		
		testRefModule(parseElement("import target;", "target", RefModule.class), "target");
		testRefModule(parseElement("import pack.target;", "pack.target", RefModule.class), "pack.target");
		
		testRefModule(parseElement("import not_found;", "not_found", RefModule.class), 
			Resolvables_SemanticsTest.NOT_FOUND_SpecialMarker);
		
		// Test package refs.
		testPackageRef(parseRef("import pack.target; auto x = pack;", "pack;"), "pack");
		testPackageRef(parseRef("import pack.subpack.target; auto x = pack;", "pack;"), "pack");
		
		testPackageRef(parseRef("import pack.subpack.target; auto x = pack.subpack/**/;", "subpack/**/"), 
			"pack.subpack");
		
	}
	
	protected PickedElement<NamedReference> parseRef(String source, String marker) throws ExecutionException {
		return parseElement(source, marker, NamedReference.class);
	}
	
	protected void testRefModule(PickedElement<RefModule> refModuleElement, String fqn) {
		ResolvableResult resolution = testResolveElement(refModuleElement);
		
		INamedElement result = resolution.result;
		assertTrue(result.getFullyQualifiedName().equals(fqn));
		
		// Test that it resolves to an alias of the actual module unit. 
		// This is an optimization to not parse the module until really necessary. 
		assertTrue(result instanceof ModuleProxy); 
		
		IConcreteNamedElement moduleTarget = result.resolveConcreteElement(refModuleElement.context);
		
		if(fqn.equals(Resolvables_SemanticsTest.NOT_FOUND_SpecialMarker)) {
			assertTrue(moduleTarget instanceof ErrorElement);
			assertTrue(moduleTarget.getFullyQualifiedName().equals(ErrorElement.NOT_FOUND__Name));
		} else {
			assertTrue(moduleTarget instanceof Module);
			assertTrue(moduleTarget.getFullyQualifiedName().equals(fqn));
		}
	}
	
	protected void testPackageRef(PickedElement<? extends NamedReference> refToPackage, String fqn) {
		ResolvableResult resolution = testResolveElement(refToPackage);
		
		INamedElement result = resolution.result;
		assertTrue(result.getFullyQualifiedName().equals(fqn));
		
		if(fqn == ErrorElement.NOT_FOUND__Name) {
			return;
		}
		
		assertTrue(result instanceof PackageNamespaceFragment); 
		
		IConcreteNamedElement concreteTarget = result.resolveConcreteElement(refToPackage.context);
		assertTrue(concreteTarget instanceof PackageNamespaceFragment);
		assertTrue(concreteTarget.getFullyQualifiedName().equals(fqn));
	}
	
	
	@Test
	public void testShadowing() throws Exception { testShadowing$(); }
	public void testShadowing$() throws Exception {
		PickedElement<Module> modulePick = parseElement("import foo; class foo_member; ", "", Module.class);
		
		Module node = modulePick.element;
		CompletionScopeLookup lookup = new CompletionScopeLookup(node, node.getEndPos(), modulePick.context);
		node.performNameLookup(lookup);
		
		resultsChecker(lookup).checkResults(array(
			"_tests/", "foo/", "everywhere/",
			
			"_tests/foo_member",
			"foo/foo_member2",
			
			"everywhere/everywhere_member"
		));
		
	}
	
}