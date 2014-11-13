package mmrnmhrm.ui.views;

import melnorme.lang.tooling.ast_actual.ILangNamedElement;
import melnorme.lang.tooling.bundles.MockSemanticResolution;
import mmrnmhrm.ui.CommonDeeUITest;

import org.junit.Test;

import dtool.ast.declarations.ModuleProxy;
import dtool.ast.declarations.PackageNamespace;
import dtool.ddoc.TextUI;
import dtool.resolver.DefUnitSearch;
import dtool.resolver.LanguageIntrinsics;
import dtool.resolver.ReferenceResolver;

public class DeeElementLabelProvider_Test extends CommonDeeUITest {
	
	@Test
	public void testBasic() throws Exception { testBasic$(); }
	public void testBasic$() throws Exception {
		
		ILangNamedElement defElement;
		defElement = new ModuleProxy("foo", null);
		assertEquals(TextUI.getLabelForHoverSignature(defElement), "foo");
		assertEquals(DeeElementLabelProvider.getLabelForContentAssistPopup(defElement), "foo");
		
		defElement = new ModuleProxy("pack.mod", null);
		assertEquals(TextUI.getLabelForHoverSignature(defElement), "pack.mod");
		assertEquals(DeeElementLabelProvider.getLabelForContentAssistPopup(defElement), "mod");
		
		defElement = new ModuleProxy("pack.sub.mod", null);
		assertEquals(TextUI.getLabelForHoverSignature(defElement), "pack.sub.mod");
		assertEquals(DeeElementLabelProvider.getLabelForContentAssistPopup(defElement), "mod");
		
		
		defElement = PackageNamespace.createPartialDefUnits(array("pack"), new ModuleProxy("modA", null));
		assertEquals(TextUI.getLabelForHoverSignature(defElement), "pack");
		assertEquals(DeeElementLabelProvider.getLabelForContentAssistPopup(defElement), "pack");

		defElement = PackageNamespace.createPartialDefUnits(array("pack", "sub"), new ModuleProxy("modA", null));
		assertEquals(TextUI.getLabelForHoverSignature(defElement), "pack");
		assertEquals(DeeElementLabelProvider.getLabelForContentAssistPopup(defElement), "pack");
		
		
		DefUnitSearch search = new DefUnitSearch("int", null, -1, true, new MockSemanticResolution());
		ReferenceResolver.findDefUnitInScope(LanguageIntrinsics.D2_063_intrinsics.primitivesScope, search);
		defElement = search.getMatchedElements().iterator().next();
		
		assertEquals(TextUI.getLabelForHoverSignature(defElement), "int");
		assertEquals(DeeElementLabelProvider.getLabelForContentAssistPopup(defElement), "int");
		
	}
	
}