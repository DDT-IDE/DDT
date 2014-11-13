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
package dtool.engine;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;

import org.junit.Test;

import dtool.ast.definitions.DefinitionVariable;
import dtool.ast.definitions.INamedElement;
import dtool.ast.references.RefPrimitive;
import dtool.ast.references.Reference;
import dtool.dub.BundlePath;
import dtool.engine.ModuleParseCache.ParseSourceException;
import dtool.parser.DeeTokens;
import dtool.parser.common.Token;

public class ElementResolution_Test extends CommonSemanticManagerTest {
	
	public final BundlePath LIB_FOO = bundlePath(SEMMODEL_TEST_BUNDLES, "lib_foo");
	public final BundlePath LIB_TPL = bundlePath(SEMMODEL_TEST_BUNDLES, "lib_tpl");
	
	@Test
	public void testResolveRef() throws Exception { testResolveRef$(); }
	public void testResolveRef$() throws Exception {
		
		___initSemanticManager();
		
		BundleResolution libFooSR = sm.getUpdatedResolution(LIB_FOO);
		
		testRefResolve(libFooSR, new RefPrimitive(new Token(DeeTokens.KW_INT, "int", 0)), 
			"int");
		
		testRefResolve(libFooSR, getSampleType(libFooSR, "test.ref_int"), 
			"int");
		
		testRefResolve(libFooSR, getSampleType(libFooSR, "test.ref_Foo"), 
			"lib_foo.mod.Foo");
		testRefResolve(libFooSR, getSampleType(libFooSR, "test.ref_Foo_2"), 
			"lib_foo.mod.Foo");
//		testRefResolve(libFooSR, getSampleType(libFooSR, "test.ref_Foo_3"), 
//			"lib_foo.mod.Foo");
		
		Object foo;
		// TODO: tests for caching
	}
	
	protected Reference getSampleType(BundleResolution sr, String elementName) throws ParseSourceException {
		INamedElement element = sr.findContainedElement(elementName);
		assertNotNull(element);
		return assertCast(element, DefinitionVariable.class).type;
	}
	
	protected void testRefResolve(BundleResolution libFooSR, Reference ref, String elementName) {
		INamedElement result = ref.resolveTargetElement(libFooSR).getSingleResult();
		assertAreEqual(result.getFullyQualifiedName(), elementName);
	}
	
}