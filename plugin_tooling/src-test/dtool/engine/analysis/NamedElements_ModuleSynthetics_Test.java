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

import melnorme.lang.tooling.engine.PickedElement;
import melnorme.lang.tooling.engine.resolver.ResolvableResult;

import org.junit.Test;

import dtool.ast.references.NamedReference;
import dtool.engine.ResolvedModule;

/* FIXME: use NamedElement_CommonTest */
public class NamedElements_ModuleSynthetics_Test extends CommonNodeSemanticsTest {
	
	@Test
	public void testModuleSyntheticUnits() throws Exception { testModuleSyntheticUnits$(); }
	public void testModuleSyntheticUnits$() throws Exception {
		
		testModuleSyntheticUnit____("module foo;", "foo");
		testModuleSyntheticUnit____("", "_tests");
		
		testModuleSyntheticUnit____("module pack.foo;", "pack.foo");
		testModuleSyntheticUnit____("module pack.subpack.foo;", "pack.subpack.foo");
	}
	
	protected void testModuleSyntheticUnit____(String preSource, String elemName) throws ExecutionException {
		ResolvedModule resolvedModule = parseModule(
			preSource + "; int _dummy = " + elemName + "/*A*/ ~ " + elemName + "/*B*/;");
		PickedElement<NamedReference> pickA = pickElement(resolvedModule, elemName + "/*A*/", NamedReference.class);
		PickedElement<NamedReference> pickB = pickElement(resolvedModule, elemName + "/*B*/", NamedReference.class);
		
		ResolvableResult resultA = Resolvables_SemanticsTest.testResolveElement(pickA);
		ResolvableResult resultB = Resolvables_SemanticsTest.testResolveElement(pickB);
		
		assertTrue(pickA.element != pickB.element);
		assertTrue(resultA.result == resultB.result);
	}
	
}