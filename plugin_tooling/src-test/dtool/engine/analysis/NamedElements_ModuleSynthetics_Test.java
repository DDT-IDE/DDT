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

import static melnorme.lang.tooling.engine.NotFoundErrorElement.NOT_FOUND__NAME;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.util.concurrent.ExecutionException;

import melnorme.lang.tooling.engine.PickedElement;
import melnorme.lang.tooling.engine.resolver.ResolvableResult;
import melnorme.lang.tooling.symbols.INamedElement;

import org.junit.Test;

import dtool.ast.declarations.ModuleProxy;
import dtool.ast.declarations.PackageNamespace;
import dtool.ast.references.NamedReference;
import dtool.engine.ResolvedModule;

public class NamedElements_ModuleSynthetics_Test extends NamedElement_CommonTest {
	
	@Override
	public void test_resolveElement________() throws Exception {
		testModuleProxy();
		testPackageNamespace();
	}
	
	protected void testModuleProxy() throws ExecutionException {
		PickedElement<INamedElement> pickedElement = parseSourceAndPickFromRefResolving(
			"import target; auto _ = target;", "target;");
		assertTrue(pickedElement.element instanceof ModuleProxy);
		
		test_resolveElement(pickedElement, "target", "target", true);
	}
	
	protected void testPackageNamespace() throws ExecutionException {
		PickedElement<INamedElement> pickedElement = parseSourceAndPickFromRefResolving(
			"import xxx.foo; auto _ = xxx;", "xxx;");
		assertTrue(pickedElement.element instanceof PackageNamespace);
		
		test_resolveElement(pickedElement, NOT_FOUND__NAME, "xxx", true);
	}
	
	/* -----------------  ----------------- */
	
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
		assertTrue(pickA.element != pickB.element);
		
		ResolvableResult resultA = Resolvables_SemanticsTest.testResolveElement(pickA);
		ResolvableResult resultB = Resolvables_SemanticsTest.testResolveElement(pickB);
		
		assertTrue(resultA.result == resultB.result);
	}
	
	@Override
	public void test_resolveSearchInMembersScope________() throws Exception {
		
		test_resolveSearchInMembersScope(parseNamedElement("module xxx;") );
		test_resolveSearchInMembersScope(parseNamedElement("module xxx.foo;") );
		
		
		test_resolveSearchInMembersScope(
			parseSourceAndPickFromRefResolving("module xxx; auto _dummy = xxx/*M*/;") 
		);
		
		test_resolveSearchInMembersScope(
			parseSourceAndPickFromRefResolving("module xxx.foo; auto _dummy = xxx/*M*/;"),
			"foo"
		);
		
		test_resolveSearchInMembersScope(
			parseSourceAndPickFromRefResolving("import xxx; auto _dummy = xxx/*M*/;")
		);
		
		test_resolveSearchInMembersScope(
			parseSourceAndPickFromRefResolving("import xxx.foo; auto _dummy = xxx/*M*/;"),
			"foo"
		);
		
	}
	
}