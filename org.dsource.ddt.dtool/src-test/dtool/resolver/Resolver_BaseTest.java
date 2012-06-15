package dtool.resolver;


import java.io.IOException;

import dtool.ast.definitions.Module;
import dtool.parser.Parser__CommonTest;
import dtool.tests.DToolBaseTest;

public class Resolver_BaseTest extends DToolBaseTest {
	
	protected static final String TESTFILESDIR = "resolver/";
	
	public static Module parseTestFile(String filename) throws IOException {
		return Parser__CommonTest.testDtoolParse(readTestResourceFile(TESTFILESDIR + filename));
	}
	
}