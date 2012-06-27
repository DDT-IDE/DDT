package dtool.resolver;


import java.io.IOException;

import dtool.ast.definitions.Module;
import dtool.parser.Parser__CommonTest;
import dtool.refmodel.pluginadapters.IModuleResolver;
import dtool.tests.DToolBaseTest;
import dtool.util.NewUtils;

public class Resolver_BaseTest extends DToolBaseTest {
	
	protected static final String TESTFILESDIR = "resolver/";
	
	public static String readResolverTestFile(String filePath) throws IOException {
		return readTestResourceFile(TESTFILESDIR + filePath);
	}
	
	public static Module parseTestFile(String filename) throws IOException {
		return Parser__CommonTest.testDtoolParse(readTestResourceFile(TESTFILESDIR + filename));
	}
	
	public static final class NullModuleResolver implements IModuleResolver {
		@Override
		public String[] findModules(String fqNamePrefix) throws Exception {
			return NewUtils.EMPTY_STRING_ARRAY;
		}
		
		@Override
		public Module findModule(String[] packages, String module) throws Exception {
			return null;
		}
	}
	
}