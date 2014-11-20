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
import melnorme.lang.tooling.bundles.ISemanticContext;
import melnorme.lang.tooling.engine.PickedElement;
import melnorme.lang.tooling.engine.resolver.IResolvable;
import melnorme.lang.tooling.engine.resolver.IResolvableSemantics;

import org.junit.Test;

import dtool.ast.references.RefPrimitive;
import dtool.dub.BundlePath;
import dtool.engine.ResolvedModule;
import dtool.parser.DeeTokens;
import dtool.parser.common.Token;

public class Resolvables_SemanticsTest extends CommonNodeSemanticsTest {
	
	public final BundlePath LIB_FOO = bundlePath(SEMANTICS_TEST_BUNDLES, "lib_foo");
	public final BundlePath LIB_TPL = bundlePath(SEMANTICS_TEST_BUNDLES, "lib_tpl");
	
	protected static PickedElement<IResolvable> pickRef(IResolvable ref, ISemanticContext context) {
		return PickedElement.create(ref, context);
	}
	
	protected void testResolveElement(PickedElement<IResolvable> refElement) {
		IResolvable ref = refElement.element;
		ISemanticContext context = refElement.context;
		
		// Test caching
		IResolvableSemantics semantics = ref.getSemantics();
		assertTrue(semantics == ref.getSemantics());
		assertTrue(semantics.resolveTargetElement(context) == semantics.resolveTargetElement(context));
//		assertTrue(ref.resolveTargetElement(br) == ref.resolveTargetElement(br));
	}
	
	@Test
	public void testResolveRef() throws Exception { testResolveRef$(); }
	public void testResolveRef$() throws Exception {
		
		ResolvedModule moduleRes = parseModule("int ref_int;");
		
		testResolveElement(pickRef(findNode(moduleRes, 0, RefPrimitive.class), moduleRes.getSemanticContext()));
		ISemanticContext moduleContext = moduleRes.getSemanticContext();
		testResolveElement(pickRef(new RefPrimitive(new Token(DeeTokens.KW_INT, "int", 0)), moduleContext));
		
		/* FIXME: test rest*/
		
//		StandardLibraryResolution stdLibBR = defaultSemMgr.getUpdatedStdLibResolution(DEFAULT_DMD_INSTALL_EXE_PATH);
//		INamedElement intElement = parseSourceAndFindNode("int ref_int;", 0, RefPrimitive.class).
//				resolveTargetElement(stdLibBR).getSingleResult();
//		INamedElement singleResult = intElement;
		
	}
	
}