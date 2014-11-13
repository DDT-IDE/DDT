package mmrnmhrm.core.model_elements;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import melnorme.lang.tooling.engine.intrinsics.IntrinsicDefUnit;

import org.junit.Test;

import dtool.ast.definitions.EArcheType;
import dtool.resolver.LanguageIntrinsics;

public class DeeElementFlagsTest {
	
	@Test
	public void testBasic() throws Exception { testBasic$(); }
	public void testBasic$() throws Exception {
		for (IntrinsicDefUnit nativeDefUnit : LanguageIntrinsics.D2_063_intrinsics.primitivesScope.members) {
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