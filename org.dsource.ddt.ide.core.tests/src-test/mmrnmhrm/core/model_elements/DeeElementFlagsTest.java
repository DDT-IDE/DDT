package mmrnmhrm.core.model_elements;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import org.junit.Test;

import dtool.ast.definitions.EArcheType;
import dtool.ast.definitions.IntrinsicDefUnit;
import dtool.resolver.LanguageIntrinsics;

public class DeeElementFlagsTest {
	
	@Test
	public void testBasic() throws Exception { testBasic$(); }
	public void testBasic$() throws Exception {
		for (IntrinsicDefUnit nativeDefUnit : LanguageIntrinsics.d_2_063_intrinsics.primitivesScope.intrinsics) {
			assertTrue(new DefElementDescriptor(nativeDefUnit).isNative());
		}
		
		for (EArcheType archeType : EArcheType.values()) {
			testArchetype(archeType);
		}
	}
	
	public void testArchetype(EArcheType archeType) {
		int elementFlags = DefElementFlagsUtil.elementFlagsForArchetype(archeType);
		assertTrue(archeType == DefElementFlagsUtil.elementFlagsToArcheType(elementFlags));
	}
	
}