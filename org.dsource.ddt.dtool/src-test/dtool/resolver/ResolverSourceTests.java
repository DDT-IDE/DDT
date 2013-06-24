package dtool.resolver;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import static melnorme.utilbox.core.CoreUtil.areEqual;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import melnorme.utilbox.core.Predicate;
import melnorme.utilbox.misc.ArrayUtil;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runners.Parameterized.Parameters;

import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.Module;
import dtool.parser.DeeParserSourceTests;
import dtool.parser.DeeTemplatedSourceBasedTest;
import dtool.refmodel.PrefixDefUnitSearch;
import dtool.refmodel.pluginadapters.IModuleResolver;
import dtool.sourcegen.AnnotatedSource;
import dtool.sourcegen.AnnotatedSource.MetadataEntry;
import dtool.sourcegen.TemplateSourceProcessorParser.TspExpansionElement;
import dtool.util.NewUtils;

public class ResolverSourceTests extends DeeTemplatedSourceBasedTest {
	
	protected static final String TESTFILESDIR = "resolver";
	
	protected static Map<String, TspExpansionElement> commonDefinitions = new HashMap<>();
	
	@BeforeClass
	public static void initCommonDefinitions() throws IOException {
		 // Add parser common data
		addCommonDefinitions(ResolverSourceTests.class, DeeParserSourceTests.TESTFILESDIR, commonDefinitions);
		addCommonDefinitions(ResolverSourceTests.class, TESTFILESDIR, commonDefinitions);
	}
	
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
		runAnnotatedTests(getTestCasesFromFile(commonDefinitions));
	}
	
	@Override
	protected void runAnnotatedSourceTest(AnnotatedSource testCase) {
		String source = testCase.source;
		NullModuleResolver modResolver = new NullModuleResolver();
		int defaultOffset = -1;
		
		for (MetadataEntry mde : testCase.metadata) {
			if(mde.name.startsWith("marker")) {
				if(mde.name.equals("marker_default")) {
					assertTrue(defaultOffset == -1);
					defaultOffset = mde.offset;
				}
				
			} else if(mde.name.equals("REFSEARCH")) {
				runRefSearchTest___________(source, modResolver, defaultOffset, mde);
			} else if(!(areEqual(mde.value, "flag") || areEqual(mde.name, "comment"))) {
				assertFail("Unknown metadata");
			}
		}
	}
	
	public void runRefSearchTest___________(String source, NullModuleResolver mr, int defaultOffset,
		MetadataEntry mde) {
		String[] expectedResults = mde.sourceValue.split("(\\\r?\\\n)"+"|▪|◘");
		for (int i = 0; i < expectedResults.length; i++) {
			expectedResults[i] = expectedResults[i].trim();
		}
		expectedResults = removeEmptyStrings(expectedResults); 
		
		String defaultModuleName = "__resolver_tests";
		int offset = defaultOffset != -1 ? defaultOffset : mde.offset;
		
		CompletionCollectorSession session = runCompletionSearch(source, defaultModuleName, mr, offset);
		
		List<DefUnit> results = session.results;
		CompareDefUnits.checkResults(results, expectedResults, false);
	}
	
	public CompletionCollectorSession runCompletionSearch(String source, String defaultModuleName,
		NullModuleResolver modResolver, int offset) {
		CompletionCollectorSession session = new CompletionCollectorSession();
		PrefixDefUnitSearch.doCompletionSearch(session, defaultModuleName, source, offset, 
			modResolver, session);
		return session;
	}
	
	public String[] removeEmptyStrings(String[] expectedResults) {
		expectedResults = ArrayUtil.filter(expectedResults, new Predicate<String>() {
			@Override
			public boolean evaluate(String obj) {
				return !obj.isEmpty();
			}
		});
		return expectedResults;
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