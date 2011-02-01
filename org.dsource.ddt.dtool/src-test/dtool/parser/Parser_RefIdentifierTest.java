package dtool.parser;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import org.junit.Test;

import dtool.ast.references.RefIdentifier;
import dtool.ast.references.Reference;

public class Parser_RefIdentifierTest extends Parser_Reference_CommonTest {
	
	@Test
	public void testParseBasic() {
		runCommonTest("foo");
	}
	
	@Test
	public void testInvalid() throws Exception { testInvalid$(); }
	public void testInvalid$() throws Exception {
		runCommonTestForInvalidFragment("foo.");
	}
	
	@Override
	protected void checkReference(Reference ref, String nodeCode) {
		super.checkReference(ref, nodeCode);
		assertTrue(ref instanceof RefIdentifier);
	}
	
}
