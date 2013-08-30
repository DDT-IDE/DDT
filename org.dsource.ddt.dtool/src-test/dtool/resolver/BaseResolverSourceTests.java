package dtool.resolver;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import static melnorme.utilbox.core.CoreUtil.areEqual;
import static melnorme.utilbox.misc.StringUtil.emptyAsNull;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import melnorme.utilbox.core.Predicate;
import melnorme.utilbox.misc.ArrayUtil;
import melnorme.utilbox.misc.StringUtil;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runners.Parameterized.Parameters;

import dtool.ast.definitions.DefUnit;
import dtool.parser.CommonTemplatedSourceBasedTest;
import dtool.parser.DeeParserSourceTests;
import dtool.resolver.api.ECompletionResultStatus;
import dtool.resolver.api.IModuleResolver;
import dtool.sourcegen.AnnotatedSource;
import dtool.sourcegen.AnnotatedSource.MetadataEntry;
import dtool.sourcegen.TemplatedSourceProcessorParser.TspExpansionElement;

public abstract class BaseResolverSourceTests extends CommonTemplatedSourceBasedTest {
	
	protected static final String TESTFILESDIR = "resolver";
	
	protected static Map<String, TspExpansionElement> commonDefinitions;
	
	@BeforeClass
	public static void initCommonDefinitions() throws IOException {
		if(commonDefinitions == null) {
			commonDefinitions = new HashMap<>();
			 // Add parser common data
			addCommonDefinitions(BaseResolverSourceTests.class, DeeParserSourceTests.TESTFILESDIR, commonDefinitions);
			addCommonDefinitions(BaseResolverSourceTests.class, TESTFILESDIR, commonDefinitions);
		}
	}
	
	@Parameters(name="{index}: {0}")
	public static Collection<Object[]> prepareTestParameterList() throws IOException {
		return createTestFileParameters(TESTFILESDIR);
	}
	
	public BaseResolverSourceTests(String testUIDescription, File file) {
		super(testUIDescription, file);
	}
	
	@Test
	public void runSourceBasedTests() throws Exception { runSourceBasedTests$(); }
	public void runSourceBasedTests$() throws Exception {
		runAnnotatedTests(getTestCasesFromFile(commonDefinitions));
	}
	
	@Override
	public void printTestCaseSource(AnnotatedSource testCase, boolean printCaseSeparator) {
		if(printCaseSeparator) {
			testsLogger.println(">-----------");
		}
		String caseSource = AnnotatedSource.printSourceWithMetadata(testCase);
		testsLogger.println(caseSource);
	}
	
	/*------------------------------*/
	
	public interface ITestsModuleResolver extends IModuleResolver {
		void cleanupChanges();
	}
	
	public static final String DEFAULT_MODULE_NAME = "_dummy";
	
	protected static HashMap<String, TestsSimpleModuleResolver> moduleResolvers = new HashMap<>();
	
	protected AnnotatedSource testCase;
	protected ITestsModuleResolver mr;
	
	protected Map<String, MetadataEntry> markers;
	
	public int getMarkerPosition(String markerName) {
		return assertNotNull(markers.get(markerName)).offset;
	}
	
	@Override
	protected final void runAnnotatedSourceTest(AnnotatedSource testCase) {
		try {
			this.testCase = testCase;
			processTestAnnotations(testCase);
		} finally {
			doAnnotatedTestCleanup();
		}
	}
	
	public void doAnnotatedTestCleanup() {
		mr.cleanupChanges();
	}
	
	protected void processTestAnnotations(AnnotatedSource testCase) {
		
		markers = new HashMap<>();
		String projectDescription = null;
		
		for (MetadataEntry mde : testCase.metadata) {
			if(mde.name.equals("PROJECT")) {
				assertTrue(projectDescription == null); // Set only once
				projectDescription = mde.value;
			} else if(mde.name.startsWith("marker")) {
				if(mde.value != null) {
					markers.put(mde.value, mde);
				}
			}
		}
		
		String testsModuleName = null;
		String testsProjectDirName = null;
		
		if(projectDescription != null) {
			testsModuleName = StringUtil.segmentUntilMatch(projectDescription, "@");
			testsProjectDirName = StringUtil.substringAfterMatch(projectDescription, "@");
			testsProjectDirName = emptyAsNull(testsProjectDirName);
		}
		
		setupTestProject(testsModuleName, testsProjectDirName, testCase);
		assertNotNull(mr);
		
		testsLogger.println("-----");
		processResolverTestMetadata(testCase);
		testsLogger.println("----------  ----------");
	}
	
	public abstract void setupTestProject(String moduleName, String projectFolderName, AnnotatedSource testCase);
	
	/*----------------------------------------*/
	
	public void processResolverTestMetadata(AnnotatedSource testCase) {
		for (MetadataEntry mde : testCase.metadata) {
			if(mde.name.startsWith("marker")) {
				// already processed
			} else if(mde.name.equals("PROJECT")) {
				// already processed
			} else if(mde.name.equals("REFSEARCH")) {
				printMDE(mde);
				prepRefSearchTest_________(mde);
			} else if(mde.name.equals("FIND")) {
				printMDE(mde);
				runFindTest_________(mde);
			} else if(mde.name.equals("FINDMISSING")) {
				printMDE(mde);
				runFindMissingTest_________(mde);
			} else if(mde.name.equals("FINDFAIL")) {
				printMDE(mde);
				runFindFailTest_________(mde);
			} else if(!(areEqual(mde.value, "flag") || areEqual(mde.name, "comment"))) {
				assertFail("Unknown metadata");
			}
		}
	}
	
	public void printMDE(MetadataEntry mde) {
		testsLogger.println(mde);
	}
	
	/* =============== */
	
	public static String[] splitValues(String string) {
		return string.isEmpty() ? new String[0] : string.split("(\\\r?\\\n)"+"|▪|◘");
	}
	
	public static String[] removeEmptyStrings(String[] expectedResults) {
		expectedResults = ArrayUtil.filter(expectedResults, new Predicate<String>() {
			@Override
			public boolean evaluate(String obj) {
				return !obj.isEmpty();
			}
		});
		return expectedResults;
	}
	
	protected final void checkResults(Collection<DefUnit> resultDefUnitsOriginal, String[] expectedResults) {
		boolean ignoreNativeResults = true;
		for (String expectedResult : expectedResults) {
			if(expectedResult.startsWith("/")) {
				ignoreNativeResults = false;
				break;
			}
		}
		checkResults(resultDefUnitsOriginal, expectedResults, true, ignoreNativeResults);
	}
	
	public void checkResults(Collection<DefUnit> resultDefUnitsOriginal, String[] expectedResults,
		boolean ignoreDummyResults, boolean ignoreNativeResults) {
		
		if(resultDefUnitsOriginal != null) {
			precheckOriginalResults(resultDefUnitsOriginal);
		}
		
		DefUnitResultsChecker defUnitResultsChecker = new DefUnitResultsChecker(resultDefUnitsOriginal);
		
		defUnitResultsChecker.removeIgnoredDefUnits(ignoreDummyResults, ignoreNativeResults);
		removeDefUnitsFromExpected(defUnitResultsChecker.resultDefUnits);
		defUnitResultsChecker.checkResults(expectedResults, markers);
	}
	
	/** Run these extra functions to test that they don't crash.
	 * TODO: Ideally we would also check the results of these functions, but it's too much work for now. */
	public void precheckOriginalResults(Collection<DefUnit> resultDefUnitsOriginal) {
		for (DefUnit defUnit : resultDefUnitsOriginal) {
			defUnit.getExtendedName();
			defUnit.getModuleNode();
			defUnit.getModuleFullyQualifiedName();
		}
	}
	
	@SuppressWarnings("unused")
	public void removeDefUnitsFromExpected(Collection<DefUnit> resultDefUnits) {
	}
	
	public void prepRefSearchTest_________(MetadataEntry mde) {
		int offset = mde.offset;
		String testStringDescriptor = mde.sourceValue;
		
		ECompletionResultStatus expectedStatusCode = ECompletionResultStatus.RESULT_OK;
		String[] expectedResults = null;
		String searchParams = StringUtil.segmentAfterMatch(testStringDescriptor, "►");
		testStringDescriptor = StringUtil.substringUntilMatch(testStringDescriptor, "►");
		
		if(testStringDescriptor.startsWith("!")) {
			String statusId = testStringDescriptor.substring(1); 
			expectedStatusCode = assertNotNull(ECompletionResultStatus.fromId(statusId));
			expectedResults = new String[0];
		} else {
			expectedResults = splitValues(testStringDescriptor);
			for (int i = 0; i < expectedResults.length; i++) {
				expectedResults[i] = expectedResults[i].trim();
			}
			expectedResults = removeEmptyStrings(expectedResults); 
		}
		
		RefSearchOptions refTester = new RefSearchOptions(offset, searchParams, expectedStatusCode, expectedResults);
		runRefSearchTest_________(refTester);
	}
	
	public static class RefSearchOptions {
		public final int offset;
		public final ECompletionResultStatus expectedStatusCode;
		public final String[] expectedResults;
		
		public final String searchParams;
		public final int rplLen;
		
		public RefSearchOptions(int offset, String searchParams, ECompletionResultStatus expectedStatusCode,
			String[] expectedResults) {
			this.offset = offset;
			this.searchParams = searchParams;
			this.expectedStatusCode = expectedStatusCode;
			this.expectedResults = assertNotNull(expectedResults);
			
			if(searchParams != null) {
				this.rplLen = Integer.parseInt(searchParams);
			} else {
				this.rplLen = 0;
			}
		}
		
	}
	
	protected abstract void runRefSearchTest_________(RefSearchOptions options);
	
	protected abstract void runFindFailTest_________(MetadataEntry mde);
	
	protected abstract void runFindTest_________(MetadataEntry mde);
	
	protected abstract void runFindMissingTest_________(MetadataEntry mde);
	
}