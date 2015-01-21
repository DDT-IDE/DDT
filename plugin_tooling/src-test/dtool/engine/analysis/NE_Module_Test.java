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

import static melnorme.lang.tooling.engine.resolver.NamedElementSemantics.NotAValueErrorElement.ERROR_IS_NOT_A_VALUE;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.util.concurrent.ExecutionException;

import melnorme.lang.tooling.engine.PickedElement;
import melnorme.lang.tooling.engine.resolver.NamedElementSemantics;
import melnorme.lang.tooling.engine.resolver.ReferenceResult;
import melnorme.lang.tooling.symbols.INamedElement;
import melnorme.lang.tooling.symbols.PackageNamespace;

import org.junit.Test;

import dtool.ast.definitions.Module;
import dtool.ast.references.NamedReference;
import dtool.engine.ResolvedModule;

public class NE_Module_Test extends NamedElement_CommonTest {
	
	@Override
	public void test_NamedElement________() throws Exception {
		test_resolveConcreteElement(parseNamedElement("module xxx;"), null);
		test_resolveConcreteElement(parseNamedElement("module pack.xxx;"), null);
		
		test_resolveSearchInMembersScope(parseNamedElement("module xxx;") );
		test_resolveSearchInMembersScope(parseNamedElement("module xxx.foo;") );
		
		test_resolveSearchInMembersScope(
			parseSourceAndPickFromRefResolving("import xxx; auto _dummy = xxx/*M*/;")
		);
		
		test_resolveSearchInMembersScope(
			parseSourceAndPickFromRefResolving("import xxx.foo; auto _dummy = xxx/*M*/;"),
			"xxx.foo/"
		);
		
		testModuleProxy_fromImport();
		testPackageNamespace_fromImport();
	}
	
	protected void testModuleProxy_fromImport() throws ExecutionException {
		PickedElement<INamedElement> pickedElement = parseSourceAndPickFromRefResolving(
			"import foo; auto _dummy = foo; ", "foo;");
		ModuleProxy moduleProxy = assertCast(pickedElement.element, ModuleProxy.class);
		
		assertTrue(moduleProxy.getModuleElement() == moduleProxy);
		assertCast(moduleProxy.resolveUnderlyingNode(), Module.class);
		
		assertTrue(((NamedElementSemantics) moduleProxy.getSemantics(pickedElement.context)).isResolved());
		
		test_NamedElement(pickedElement, "$foo/", expectNotAValue("foo"), 
			array("foo/foo_member", "foo/foo_member2"));
	}
	
	protected void testPackageNamespace_fromImport() throws ExecutionException {
		PickedElement<INamedElement> pickedElement = parseSourceAndPickFromRefResolving(
			"import xxx.foo; auto _ = xxx;", "xxx;");
		assertTrue(pickedElement.element instanceof PackageNamespace);
		
		test_NamedElement(pickedElement, null, ERROR_IS_NOT_A_VALUE + ":xxx", array("xxx.foo/"));
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
	
	protected void testModuleSyntheticUnit____(String preSource, String elemName) {
		ResolvedModule resolvedModule = parseModule_(
			preSource + "; int _dummy = " + elemName + "/*A*/ ~ " + elemName + "/*B*/;");
		PickedElement<NamedReference> pickA = pickElement(resolvedModule, elemName + "/*A*/", NamedReference.class);
		PickedElement<NamedReference> pickB = pickElement(resolvedModule, elemName + "/*B*/", NamedReference.class);
		assertTrue(pickA.element != pickB.element);
		
		ReferenceResult resultA = Resolvables_SemanticsTest.testResolveElement(pickA);
		ReferenceResult resultB = Resolvables_SemanticsTest.testResolveElement(pickB);
		
		assertTrue(resultA.result == resultB.result);
	}
	
}