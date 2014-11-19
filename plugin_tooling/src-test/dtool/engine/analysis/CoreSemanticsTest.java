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
import melnorme.lang.tooling.bundles.ModuleSourceException;
import melnorme.lang.tooling.symbols.INamedElement;

import org.junit.Test;

import dtool.ast.references.RefPrimitive;
import dtool.ast.references.Reference;
import dtool.dub.BundlePath;
import dtool.engine.AbstractBundleResolution;
import dtool.engine.BundleResolution;
import dtool.engine.ResolvedModule;
import dtool.parser.DeeTokens;
import dtool.parser.common.Token;

public class CoreSemanticsTest extends CommonNodeSemanticsTest {
	
	public final BundlePath LIB_FOO = bundlePath(SEMANTICS_TEST_BUNDLES, "lib_foo");
	public final BundlePath LIB_TPL = bundlePath(SEMANTICS_TEST_BUNDLES, "lib_tpl");
	
	@Test
	public void testSemanticsStorage() throws Exception { testSemanticsStorage$(); }
	public void testSemanticsStorage$() throws Exception {
		
		ResolvedModule moduleRes = parseModule("int ref_int;");
		testNamedElementSemantics(moduleRes);
		
		testReferenceResolve(moduleRes, findNode(moduleRes, 0, RefPrimitive.class));
		
//		StandardLibraryResolution stdLibBR = defaultSemMgr.getUpdatedStdLibResolution(DEFAULT_DMD_INSTALL_EXE_PATH);
//		INamedElement intElement = parseSourceAndFindNode("int ref_int;", 0, RefPrimitive.class).
//				resolveTargetElement(stdLibBR).getSingleResult();
//		INamedElement singleResult = intElement;
	}
	
	protected void testNamedElementSemantics(ResolvedModule moduleRes) 
			throws ModuleSourceException {
		AbstractBundleResolution sr = moduleRes.getSemanticResolution();
		INamedElement namedElement = moduleRes.getModuleNode();
		assertTrue(namedElement.getSemantics(sr) == namedElement.getSemantics(sr));
		assertTrue(namedElement.resolveConcreteElement(sr) == namedElement.resolveConcreteElement(sr));
	}
	
	protected void testReferenceResolve(ResolvedModule moduleRes) throws ModuleSourceException {
		RefPrimitive Reference = findNode(moduleRes, 0, RefPrimitive.class);
		testReferenceResolve(moduleRes, Reference);
	}
	
	protected void testReferenceResolve(ResolvedModule moduleRes, RefPrimitive ref) throws ModuleSourceException {
		AbstractBundleResolution br = moduleRes.getSemanticResolution();
		
		// Test caching
		assertTrue(ref.getSemantics(br) == ref.getSemantics(br));
		assertTrue(ref.resolveTargetElement(br) == ref.resolveTargetElement(br));
	}
	
	@Test
	public void testResolveRef() throws Exception { testResolveRef$(); }
	public void testResolveRef$() throws Exception {
		
		BundleResolution libFooSR = defaultSemMgr.getUpdatedResolution(LIB_FOO);
		
		testRefResolve(libFooSR, new RefPrimitive(new Token(DeeTokens.KW_INT, "int", 0)), 
			"int");
		
		testRefResolve(libFooSR, getSampleType(libFooSR, "test.ref_int"), 
			"int");
		
		testRefResolve(libFooSR, getSampleType(libFooSR, "test.ref_int"), 
				"int");

//		int ref_int;
//
//		alias Foo Foo_Alias;
//
//		lib_foo.mod.Foo ref_Foo;
//		Foo             ref_Foo_2;
//		Foo_Alias       ref_Foo_3;
		
		testRefResolve(libFooSR, getSampleType(libFooSR, "test.ref_Foo"), 
			"lib_foo.mod.Foo");
		testRefResolve(libFooSR, getSampleType(libFooSR, "test.ref_Foo_2"), 
			"lib_foo.mod.Foo");
//		testRefResolve(libFooSR, getSampleType(libFooSR, "test.ref_Foo_3"), 
//			"lib_foo.mod.Foo");
		
		Object foo;
		// TODO: tests for caching
	}
	
	protected Reference getSampleType(BundleResolution sr, String elementName) throws ModuleSourceException {
//		INamedElement element = sr.findContainedElement(elementName);
//		assertNotNull(element);
//		return assertCast(element, DefinitionVariable.class).type;
		return null;
	}
	
	protected void testRefResolve(BundleResolution libFooSR, Reference ref, String elementName) {
		INamedElement result = ref.resolveTargetElement(libFooSR).getSingleResult();
		assertAreEqual(result.getFullyQualifiedName(), elementName);
	}
	
}