package mmrnmhrm.core.model_elements;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import org.junit.Test;

import dtool.ast.declarations.SyntheticDefUnit;
import dtool.resolver.NativesScope;

public class ModelElementsModifierFlagsTest {
	
	@Test
	public void testBasic() throws Exception { testBasic$(); }
	public void testBasic$() throws Exception {
		for (SyntheticDefUnit nativeDefUnit : NativesScope.nativesScope.intrinsics) {
			assertTrue(new DefElementDescriptor(nativeDefUnit).isNative());
		}		
	}
	
}