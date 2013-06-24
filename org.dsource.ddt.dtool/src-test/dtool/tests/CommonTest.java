package dtool.tests;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

/**
 *  Recommended base class for all tests.
 */
public class CommonTest extends CommonTestUtils {
	
	public CommonTest() {
		if(isJUnitTest()) {
			String klassName = getClass().getSimpleName();
			// Check proper tests nomenclature:
			// This to make sure the Maven build picks up the same tests as the the Eclipse JUnit launchers
			assertTrue(klassName.startsWith("Test") || klassName.endsWith("Test") || klassName.endsWith("Tests"));
		}
	}
	
	public boolean isJUnitTest() {
		return true;
	}
	
}