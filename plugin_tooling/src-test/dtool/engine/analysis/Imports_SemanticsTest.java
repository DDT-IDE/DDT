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
import melnorme.lang.tooling.engine.PickedElement;
import melnorme.lang.tooling.engine.resolver.ResolvableResult;
import melnorme.lang.tooling.symbols.IConcreteNamedElement;
import melnorme.lang.tooling.symbols.INamedElement;

import org.junit.Test;

import dtool.ast.declarations.ModuleProxy;
import dtool.ast.definitions.Module;
import dtool.ast.references.RefModule;


public class Imports_SemanticsTest extends CommonNodeSemanticsTest {
	
	@Test
	public void testImports() throws Exception { testImports$(); }
	public void testImports$() throws Exception {
		
		testRefModule(parseTestElement("import target;", "target", RefModule.class), "target");
		testRefModule(parseTestElement("import pack.target;", "pack.target", RefModule.class), "pack.target");
		
//		testRefModule(parseTestElement("import foo.not_found;", "not_found", RefModule.class), "not_found");
	}
	
	protected void testRefModule(PickedElement<RefModule> refModuleElement, String fqn) {
		ResolvableResult resolution = testResolveElement(refModuleElement);
		
		INamedElement result = resolution.result;
		
		// Test that it resolves to an alias of the actual module unit. 
		// This is an optimization to not parse the module until really necessary. 
		assertTrue(result instanceof ModuleProxy); 
		
		IConcreteNamedElement moduleTarget = result.resolveConcreteElement(refModuleElement.context);
		assertTrue(moduleTarget instanceof Module);
		assertTrue(moduleTarget.getFullyQualifiedName().equals(fqn));
	}
	
}