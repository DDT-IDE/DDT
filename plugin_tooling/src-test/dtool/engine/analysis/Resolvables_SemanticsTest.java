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
import static melnorme.utilbox.core.CoreUtil.areEqual;
import melnorme.lang.tooling.context.ISemanticContext;
import melnorme.lang.tooling.engine.ErrorElement;
import melnorme.lang.tooling.engine.PickedElement;
import melnorme.lang.tooling.engine.intrinsics.ModuleQualifiedReference;
import melnorme.lang.tooling.engine.resolver.IResolvable;
import melnorme.lang.tooling.engine.resolver.ResolvableResult;
import melnorme.lang.tooling.engine.resolver.ResolvableSemantics;
import melnorme.lang.tooling.symbols.INamedElement;

import org.junit.Test;

import dtool.ast.references.NamedReference;
import dtool.ast.references.RefIdentifier;
import dtool.ast.references.RefModule;
import dtool.ast.references.RefPrimitive;
import dtool.dub.BundlePath;
import dtool.engine.StandardLibraryResolution.MissingStandardLibraryResolution;

public class Resolvables_SemanticsTest extends CommonNodeSemanticsTest {
	
	public final BundlePath LIB_FOO = bundlePath(SEMANTICS_TEST_BUNDLES, "lib_foo");
	public final BundlePath LIB_TPL = bundlePath(SEMANTICS_TEST_BUNDLES, "lib_tpl");
	
	protected static final String NOT_FOUND_SpecialMarker = "not_found";
	
	protected static PickedElement<IResolvable> pickRef(IResolvable ref, ISemanticContext context) {
		return new PickedElement<>(ref, context);
	}
	
	protected static ResolvableResult testResolveElement(PickedElement<? extends NamedReference> refElement) {
		NamedReference namedRef = refElement.element;
		
		String expectedName = namedRef.getCoreReferenceName();
		if(namedRef instanceof RefModule) {
			RefModule refModule = (RefModule) refElement.element;
			expectedName = refModule.getRefModuleFullyQualifiedName();
		} else if(areEqual(expectedName, NOT_FOUND_SpecialMarker)) {
			expectedName = ErrorElement.NOT_FOUND__Name;
		}
		
		return testResolveElement(refElement, expectedName);
	}
	
	protected static ResolvableResult testResolveElement(PickedElement<? extends IResolvable> refElement, 
			String expectedName) {
		IResolvable ref = refElement.element;
		ISemanticContext context = refElement.context;
		
		//refElement.internal_resetSemanticResolutions();
		
		// Test caching
		ResolvableSemantics semantics = ref.getSemantics(context);
		ResolvableResult resolveTargetElement = semantics.resolveTargetElement();
		assertTrue(semantics == ref.getSemantics(context));
		assertTrue(resolveTargetElement == semantics.resolveTargetElement());
		
		INamedElement result = resolveTargetElement.result;
		assertTrue(result != null && areEqual(result.getName(), expectedName));
		
		return resolveTargetElement;
	}
	
	@Test
	public void testResolveRef() throws Exception { testResolveRef$(); }
	public void testResolveRef$() throws Exception {
		
		testResolveElement(parseElement("int ref_int;", "int", RefPrimitive.class));
		
		testResolveElement(parseElement("class target { }; target bar;", "target bar", RefIdentifier.class));
		
		testResolveElement(parseElement("not_found foo;", "not_found", RefIdentifier.class));
		
		testResolveElement(new PickedElement<IResolvable>(
				new ModuleQualifiedReference("object", "TypeInfo_Class"), 
				new MissingStandardLibraryResolution(defaultSemMgr)), "TypeInfo_Class");
		
		testResolveElement(parseElement("import target;", "target", RefModule.class));
		
		/* TODO: test rest of reference classes */
		
//		StandardLibraryResolution stdLibBR = defaultSemMgr.getUpdatedStdLibResolution(DEFAULT_DMD_INSTALL_EXE_PATH);
//		INamedElement intElement = parseSourceAndFindNode("int ref_int;", 0, RefPrimitive.class).
//				resolveTargetElement(stdLibBR).getSingleResult();
//		INamedElement singleResult = intElement;
		
	}
	
}