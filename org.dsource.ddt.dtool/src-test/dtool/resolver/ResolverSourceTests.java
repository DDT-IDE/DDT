package dtool.resolver;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import static melnorme.utilbox.core.CoreUtil.areEqual;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.junit.Test;
import org.junit.runners.Parameterized.Parameters;

import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.Module;
import dtool.contentassist.CompletionSession;
import dtool.parser.DeeTemplatedSourceBasedTest;
import dtool.refmodel.PrefixDefUnitSearch;
import dtool.refmodel.pluginadapters.IModuleResolver;
import dtool.sourcegen.AnnotatedSource;
import dtool.sourcegen.AnnotatedSource.MetadataEntry;
import dtool.util.NewUtils;

public class ResolverSourceTests extends DeeTemplatedSourceBasedTest {
	
	protected static final String TESTFILESDIR = "resolver";
	
	@Parameters(name="{index}: {0}")
	public static Collection<Object[]> testParameterList() throws IOException {
		return createTestFileParameters(TESTFILESDIR);
	}
	
	public ResolverSourceTests(String testUIDescription, File file) {
		super(testUIDescription, file);
	}
	
	@Test
	public void runSourceBasedTests() throws Exception { runSourceBasedTests$(); }
	public void runSourceBasedTests$() throws Exception {
		runAnnotatedTests(getTestCasesFromFile(null));
	}
	
	@Override
	protected void runAnnotatedSourceTest(AnnotatedSource testCase) {
		String source = testCase.source;
		NullModuleResolver modResolver = new NullModuleResolver();
		
		for (MetadataEntry mde : testCase.metadata) {
			if(mde.name.equals("REFSEARCH")) {
				runRefSearchTest(source, modResolver, mde);
			} else if(!(areEqual(mde.value, "flag") || areEqual(mde.name, "comment"))) {
				assertFail("Unknown metadata");
			}
		}
	}
	
	public void runRefSearchTest(String source, NullModuleResolver modResolver, MetadataEntry mde) {
		String[] expectedResults = mde.sourceValue.split("(\\\r?\\\n)"+"|(\\|)");
		for (int i = 0; i < expectedResults.length; i++) {
			expectedResults[i] = expectedResults[i].trim();
		}
		
		String defaultModuleName = "__resolver_tests";
		int offset = mde.offset;
		
		CompletionSession session = new CompletionSession();
		DefUnitArrayListCollector defUnitAccepter = new DefUnitArrayListCollector();
		PrefixDefUnitSearch.doCompletionSearch(session, defaultModuleName, source, offset, 
			modResolver, defUnitAccepter);
		
		List<DefUnit> results = defUnitAccepter.results;
		CompareDefUnits.checkResults(results, expectedResults, false);
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