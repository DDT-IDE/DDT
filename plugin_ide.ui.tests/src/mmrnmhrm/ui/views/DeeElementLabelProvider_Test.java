package mmrnmhrm.ui.views;

import static dtool.engine.analysis.PackageNamespaceFragment.createNamespaceFragments;
import melnorme.lang.tooling.context.EmptySemanticResolution;
import melnorme.lang.tooling.engine.scoping.ResolutionLookup;
import melnorme.lang.tooling.symbols.INamedElement;
import mmrnmhrm.ui.CommonDeeUITest;

import org.junit.Test;

import dtool.ddoc.TextUI;
import dtool.engine.analysis.DeeLanguageIntrinsics;
import dtool.engine.analysis.ModuleProxy;

public class DeeElementLabelProvider_Test extends CommonDeeUITest {
	
	@Test
	public void testBasic() throws Exception { testBasic$(); }
	public void testBasic$() throws Exception {
		
		INamedElement defElement;
		defElement = new ModuleProxy("foo", null, null);
		assertEquals(TextUI.getLabelForHoverSignature(defElement), "foo");
		assertEquals(DeeElementLabelProvider.getLabelForContentAssistPopup(defElement), "foo");
		
		defElement = new ModuleProxy("pack.mod", null, null);
		assertEquals(TextUI.getLabelForHoverSignature(defElement), "pack.mod");
		assertEquals(DeeElementLabelProvider.getLabelForContentAssistPopup(defElement), "mod");
		
		defElement = new ModuleProxy("pack.sub.mod", null, null);
		assertEquals(TextUI.getLabelForHoverSignature(defElement), "pack.sub.mod");
		assertEquals(DeeElementLabelProvider.getLabelForContentAssistPopup(defElement), "mod");
		
		
		defElement = createNamespaceFragments(array("pack"), new ModuleProxy("modA", null, null), null);
		assertEquals(TextUI.getLabelForHoverSignature(defElement), "pack");
		assertEquals(DeeElementLabelProvider.getLabelForContentAssistPopup(defElement), "pack");

		defElement = createNamespaceFragments(array("pack", "sub"), new ModuleProxy("modA", null, null), null);
		assertEquals(TextUI.getLabelForHoverSignature(defElement), "pack");
		assertEquals(DeeElementLabelProvider.getLabelForContentAssistPopup(defElement), "pack");
		
		
		ResolutionLookup search = new ResolutionLookup("int", null, -1, new EmptySemanticResolution());
		search.evaluateScope(DeeLanguageIntrinsics.D2_063_intrinsics.primitivesScope);
		defElement = search.getMatchedElement();
		
		assertEquals(TextUI.getLabelForHoverSignature(defElement), "int");
		assertEquals(DeeElementLabelProvider.getLabelForContentAssistPopup(defElement), "int");
		
	}
	
}