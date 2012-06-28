package dtool.resolver;

import org.junit.Test;

public class ModuleName_ResolverTest extends ResolverCommandBasedTest {
	
	@Test
	public void test() throws Exception { test$(); }
	public void test$() throws Exception {
		splitSourceAndRunTestCommands(readResolverTestFile("module_tests.d"), "module_tests");
	}
	
}