package dtool.parser;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import org.junit.Test;

import dtool.ast.references.RefModuleQualified;
import dtool.ast.references.Reference;

public class Parser_ReferenceModuleQualTest extends Parser_Reference_CommonTest {
	
	@Test
	public void testParseBasic() {
		runCommonTest(".foo");
	}
	
	@Test
	public void testInvalid() throws Exception { testInvalid$(); }
	public void testInvalid$() throws Exception {
		runCommonTestForInvalidFragment(".pack.");
		runCommonTestForInvalidFragment("..pack");
		runCommonTestForInvalidFragment(".pack.subpack.foo.");
		runCommonTestForInvalidFragment(".pack..subpack.foo.");
	}
	
	@Override
	protected void checkReference(Reference ref, String nodeCode) {
		super.checkReference(ref, nodeCode);
		assertTrue(ref instanceof RefModuleQualified);
	}
	
}
