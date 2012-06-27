package dtool.resolver;

import org.junit.Test;

public class DeclarationStaticIfTypeTest extends ResolverCommandBasedTest {
	
	@Test
	public void test() throws Exception { test$(); }
	public void test$() throws Exception {
		String source = readResolverTestFile("decl_staticIfIsType_r.d");
		runTestCommands(source);
	}
	
}