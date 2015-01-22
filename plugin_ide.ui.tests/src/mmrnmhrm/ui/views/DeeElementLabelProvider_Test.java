/*******************************************************************************
 * Copyright (c) 2012, 2015 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package mmrnmhrm.ui.views;

import static melnorme.lang.tooling.symbols.PackageNamespace.createNamespaceElement;
import melnorme.lang.tooling.context.EmptySemanticResolution;
import melnorme.lang.tooling.engine.scoping.ResolutionLookup;
import melnorme.lang.tooling.symbols.INamedElement;
import mmrnmhrm.ui.CommonDeeUITest;

import org.junit.Test;

import dtool.ast.references.RefModule;
import dtool.ddoc.TextUI;
import dtool.engine.analysis.CommonNodeSemanticsTest;
import dtool.engine.analysis.DeeLanguageIntrinsics;
import dtool.engine.analysis.ModuleProxy;

public class DeeElementLabelProvider_Test extends CommonDeeUITest {
	
	@Test
	public void testBasic() throws Exception { testBasic$(); }
	public void testBasic$() throws Exception {
		
		checkLabel(new ModuleProxy("foo", null, getMockRefModule()), "foo", "foo");
		
		checkLabel(new ModuleProxy("pack.mod", null, getMockRefModule()), "pack.mod", "mod");
		
		checkLabel(new ModuleProxy("pack.sub.mod", null, getMockRefModule()), "pack.sub.mod", "mod");
		
		
		checkLabel(createNamespaceElement(array("pack"), new ModuleProxy("modA", null, getMockRefModule())), 
			"pack", "pack");

		checkLabel(createNamespaceElement(array("pack", "sub"), new ModuleProxy("modA", null, getMockRefModule())), 
			"pack", "pack");
		
		
		ResolutionLookup search = new ResolutionLookup("int", -1, new EmptySemanticResolution());
		search.evaluateScope(DeeLanguageIntrinsics.D2_063_intrinsics.primitivesScope);
		
		checkLabel(search.getMatchedElement(), "int", "int");
		
		/* -----------------  ----------------- */
		
		checkLabel(CommonNodeSemanticsTest.parseElement("alias xxx = ;", "xx", INamedElement.class).element, 
			"xxx", 
			"xxx -> ? - _tests");
	}
	
	protected RefModule getMockRefModule() {
		return CommonNodeSemanticsTest.parseElement("import foo/*M*/;", RefModule.class).element;
	}
	
	protected void checkLabel(INamedElement defElement, String hoverSignatureLabel, String contentAssistPopupLabel) {
		assertEquals(TextUI.getLabelForHoverSignature(defElement), hoverSignatureLabel);
		assertEquals(DeeElementLabelProvider.getLabelForContentAssistPopup(defElement), contentAssistPopupLabel);
	}
	
}