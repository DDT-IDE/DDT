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

import melnorme.lang.tooling.context.ISemanticContext;
import melnorme.lang.tooling.engine.completion.CompletionSearchResult.ECompletionResultStatus;
import melnorme.lang.tooling.symbols.INamedElement;
import melnorme.utilbox.core.fntypes.Predicate;
import melnorme.utilbox.misc.ArrayUtil;
import melnorme.utilbox.misc.SimpleLogger;
import melnorme.utilbox.misc.StringUtil;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runners.Parameterized.Parameters;

import dtool.ast.definitions.DefUnit;
import dtool.parser.CommonTemplatedSourceBasedTest;
import dtool.parser.DeeParserSourceTests;
import dtool.sourcegen.AnnotatedSource;
import dtool.sourcegen.AnnotatedSource.MetadataEntry;
import dtool.sourcegen.TemplatedSourceProcessorParser.TspExpansionElement;
import dtool.tests.DToolTestResources;

public abstract class BaseResolverSourceTests extends CommonTemplatedSourceBasedTest {
	
	public static SimpleLogger resolverTestsLog = SimpleLogger.create("ResolverSourceTests");
	
	public static final String TESTFILESDIR = "resolver";
	
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
			expandedTestCaseLog.println(">-----------");
		}
		String caseSource = AnnotatedSource.printSourceWithMetadata(testCase);
		expandedTestCaseLog.println(caseSource);
	}
	
	public File getProjectDirectory(String projectFolderName) {
		File projectFolder = new File(file.getParent(), assertNotNull(projectFolderName));
		if(projectFolder.exists() == false) {
			File commonDir = DToolTestResources.getTestResourceFile(TESTFILESDIR, "0_common"); 
			assertTrue(commonDir.exists());
			projectFolder = new File(commonDir, assertNotNull(projectFolderName));
		}
		assertTrue(projectFolder.exists());
		return projectFolder;
	}
	
	/*------------------------------*/
	
	public static final String DEFAULT_MODULE_NAME = "_dummy";
	
	protected static HashMap<String, TestsSimpleModuleResolver> moduleResolvers = new HashMap<>();
	
	protected AnnotatedSource testCase;
	protected String testsModuleName;
	protected String testsProjectDirName;
	protected boolean ignoreStdLibObject = true;
	
	protected ISemanticContext mr;
	
	protected Map<String, MetadataEntry> markers;
	
	public int getMarkerPosition(String markerName) {
		return assertNotNull(markers.get(markerName)).offset;
	}
	
	@Override
	protected final void runAnnotatedSourceTest(AnnotatedSource testCase) {
		try {
			processTestAnnotations(testCase);
		} finally {
			cleanupTestCase();
		}
	}
	
	protected void processTestAnnotations(AnnotatedSource testCase) {
		this.testCase = testCase;
	
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
			} else if(mde.name.equals("include_object_module")) {
				ignoreStdLibObject = false;
			}
		}
		
		if(projectDescription != null) {
			testsModuleName = StringUtil.segmentUntilMatch(projectDescription, "@");
			testsProjectDirName = StringUtil.substringAfterMatch(projectDescription, "@");
			testsProjectDirName = emptyAsNull(testsProjectDirName);
		} else {
			testsModuleName = null;
			testsProjectDirName = null;
		}
		
		prepareTestCase(testsModuleName, testsProjectDirName, testCase);
		
		resolverTestsLog.println("-----");
		processResolverTestMetadata(testCase);
		resolverTestsLog.println("----------  ----------");
	}
	
	public abstract void prepareTestCase(String moduleName, String projectFolderName, AnnotatedSource testCase);
	
	public void cleanupTestCase() {
	}
	
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
		resolverTestsLog.println(mde);
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
	
	protected final void checkResults(Collection<INamedElement> resultDefUnitsOriginal, String[] expectedResults) {
		boolean ignoreNativeResults = true;
		for (String expectedResult : expectedResults) {
			if(expectedResult.startsWith("/")) {
				ignoreNativeResults = false;
				break;
			}
		}
		checkResults(resultDefUnitsOriginal, expectedResults, true, ignoreNativeResults, ignoreStdLibObject);
	}
	
	public void checkResults(Collection<INamedElement> resultElementsOriginal, String[] expectedResults,
		boolean ignoreDummyResults, boolean ignoreNativeResults, boolean ignoreStdLibObject) {
		
		if(resultElementsOriginal != null) {
			precheckOriginalResults(resultElementsOriginal);
		}
		
		DefUnitResultsChecker defUnitResultsChecker = new DefUnitResultsChecker(resultElementsOriginal);
		
		defUnitResultsChecker.removeIgnoredDefUnits(ignoreDummyResults, ignoreNativeResults);
		if(ignoreStdLibObject) {
			defUnitResultsChecker.removeStdLibObjectDefUnits();
		}
		
		removeDefUnitsFromExpected(defUnitResultsChecker.resultElements);
		defUnitResultsChecker.checkResults(expectedResults, markers);
	}
	
	/** Run these extra functions to test that they don't crash.
	 * TODO: Ideally we would also check the results of these functions, but it's too much work for now. */
	public void precheckOriginalResults(Collection<INamedElement> resultElementsOriginal) {
		for (INamedElement elem : resultElementsOriginal) {
			elem.getExtendedName();
			elem.getModuleFullyQualifiedName();
			if(elem instanceof DefUnit) {
				DefUnit defUnit = (DefUnit) elem;
				defUnit.getModuleNode();
			}
		}
	}
	
	@SuppressWarnings("unused")
	public void removeDefUnitsFromExpected(Collection<INamedElement> resultElements) {
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