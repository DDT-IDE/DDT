package mmrnmhrm.ui.views;

import mmrnmhrm.tests.ui.BaseDeeUITest;

import org.junit.Test;

import dtool.ast.declarations.PartialPackageDefUnit;
import dtool.ast.definitions.INamedElement;
import dtool.ast.references.RefModule.LightweightModuleProxy;
import dtool.resolver.DefUnitSearch;
import dtool.resolver.NativesScope;
import dtool.resolver.ReferenceResolver;
import dtool.resolver.api.NullModuleResolver;

public class DeeDefUnitLabelProvider_Test extends BaseDeeUITest {
	
	@Test
	public void testBasic() throws Exception { testBasic$(); }
	public void testBasic$() throws Exception {
		
		INamedElement defElement;
		defElement = new LightweightModuleProxy("foo", null);
		assertEquals(DeeElementLabelProvider.getLabelForHoverSignature(defElement), "foo");
		assertEquals(DeeElementLabelProvider.getLabelForContentAssistPopup(defElement), "foo");
		
		defElement = new LightweightModuleProxy("pack.mod", null);
		assertEquals(DeeElementLabelProvider.getLabelForHoverSignature(defElement), "pack.mod");
		assertEquals(DeeElementLabelProvider.getLabelForContentAssistPopup(defElement), "pack.mod");
		
		defElement = PartialPackageDefUnit.createPartialDefUnits(array("pack"), null, 
			new LightweightModuleProxy("modA", null));
		assertEquals(DeeElementLabelProvider.getLabelForHoverSignature(defElement), "pack");
		assertEquals(DeeElementLabelProvider.getLabelForContentAssistPopup(defElement), "pack");

		defElement = PartialPackageDefUnit.createPartialDefUnits(array("pack", "sub"), null, 
			new LightweightModuleProxy("modA", null));
		assertEquals(DeeElementLabelProvider.getLabelForHoverSignature(defElement), "pack");
		assertEquals(DeeElementLabelProvider.getLabelForContentAssistPopup(defElement), "pack");
		
		
		DefUnitSearch search = new DefUnitSearch("int", null, -1, true, new NullModuleResolver());
		ReferenceResolver.findDefUnitInScope(NativesScope.nativesScope, search);
		defElement = search.getMatchedElements().iterator().next();
		
		assertEquals(DeeElementLabelProvider.getLabelForHoverSignature(defElement), "int");
		assertEquals(DeeElementLabelProvider.getLabelForContentAssistPopup(defElement), "int");
		
	}
	
}