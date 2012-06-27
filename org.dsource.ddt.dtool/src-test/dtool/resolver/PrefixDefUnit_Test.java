package dtool.resolver;

import org.junit.Test;

import dtool.contentassist.CompletionSession;
import dtool.refmodel.PrefixDefUnitSearch;

public class PrefixDefUnit_Test extends Resolver_BaseTest {
	
	@Test
	public void testCompletionAtLimit() throws Exception { testCompletionAtLimit$(); }
	public void testCompletionAtLimit$() throws Exception {
		String source = readResolverTestFile("prefixSearch_rplTest.d");
		
		CompletionSession session = new CompletionSession();
		DefUnitArrayListCollector defUnitAccepter = new DefUnitArrayListCollector();
		PrefixDefUnitSearch.doCompletionSearch(session, "_unnamed_", source, source.length(), 
				new NullModuleResolver(), defUnitAccepter);
	}
	
}