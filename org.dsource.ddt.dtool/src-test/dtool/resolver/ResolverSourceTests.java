package dtool.resolver;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import static melnorme.utilbox.core.CoreUtil.areEqual;
import static melnorme.utilbox.misc.MiscUtil.nullToOther;
import static melnorme.utilbox.misc.StringUtil.emptyAsNull;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import melnorme.utilbox.core.Predicate;
import melnorme.utilbox.misc.ArrayUtil;
import melnorme.utilbox.misc.StringUtil;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runners.Parameterized.Parameters;

import dtool.ast.definitions.DefUnit;
import dtool.parser.CommonTemplatedSourceBasedTest;
import dtool.parser.DeeParser;
import dtool.parser.DeeParserResult;
import dtool.parser.DeeParserSourceTests;
import dtool.resolver.ReferenceResolver.DirectDefUnitResolve;
import dtool.resolver.api.IModuleResolver;
import dtool.resolver.api.NullModuleResolver;
import dtool.resolver.api.PrefixDefUnitSearchBase.ECompletionResultStatus;
import dtool.sourcegen.AnnotatedSource;
import dtool.sourcegen.AnnotatedSource.MetadataEntry;
import dtool.sourcegen.TemplatedSourceProcessorParser.TspExpansionElement;
import dtool.util.NewUtils;

public class ResolverSourceTests extends CommonTemplatedSourceBasedTest {
	
	protected static final String TESTFILESDIR = "resolver";
	
	protected static Map<String, TspExpansionElement> commonDefinitions;
	
	@BeforeClass
	public static void initCommonDefinitions() throws IOException {
		if(commonDefinitions == null) {
			commonDefinitions = new HashMap<>();
			 // Add parser common data
			addCommonDefinitions(ResolverSourceTests.class, DeeParserSourceTests.TESTFILESDIR, commonDefinitions);
			addCommonDefinitions(ResolverSourceTests.class, TESTFILESDIR, commonDefinitions);
		}
	}
	
	@Parameters(name="{index}: {0}")
	public static Collection<Object[]> prepareTestParameterList() throws IOException {
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
	
	public static final class TestsNullModuleResolver extends NullModuleResolver implements ITestsModuleResolver {
		@Override
		public void cleanupChanges() {
		}
	}
	
	public static final String DEFAULT_MODULE_NAME = "_dummy";
	
	protected static HashMap<String, TestsSimpleModuleResolver> moduleResolvers = new HashMap<>();
	
	protected AnnotatedSource testCase;
	protected ITestsModuleResolver mr;
	protected DeeParserResult parseResult;
	
	protected Map<String, MetadataEntry> markers;
	
	public int getMarkerPosition(String markerName) {
		return assertNotNull(markers.get(markerName)).offset;
	}
	
	public DeeParserResult getParseResult() {
		return assertNotNull(parseResult);
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
	}
	
	public void setupTestProject(String moduleName, String projectFolderName, AnnotatedSource testCase) {
		moduleName = nullToOther(moduleName, DEFAULT_MODULE_NAME);
		parseResult = DeeParser.parseSource(testCase.source, moduleName);
		
		if(projectFolderName == null || projectFolderName.isEmpty()) {
			mr = new TestsNullModuleResolver();
			return;
		}
		TestsSimpleModuleResolver existingMR = moduleResolvers.get(projectFolderName);
		if(existingMR == null) {
			File projectFolder = new File(file.getParent(), assertNotNull(projectFolderName));
			existingMR = new TestsSimpleModuleResolver(projectFolder);
			moduleResolvers.put(projectFolderName, existingMR); // Cache the MR data
		}
		if(moduleName != null) {
			existingMR.setExtraModule(moduleName, parseResult);
		}
		mr = existingMR;
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
		checkResults(resultDefUnitsOriginal, expectedResults, true);
	}
	
	public void checkResults(Collection<DefUnit> resultDefUnitsOriginal, String[] expectedResults,
		boolean removedDummyResults) {
		
		precheckOriginalResults(resultDefUnitsOriginal);
		
		DefUnitResultsChecker defUnitResultsChecker = new DefUnitResultsChecker(resultDefUnitsOriginal);
		
		if(removedDummyResults) {
			removeDummyDefUnits(defUnitResultsChecker.resultDefUnits);
		}
		
		defUnitResultsChecker.checkResults(expectedResults, markers);
	}
	
	/** Run these extra functions to test that they don't crash.
	 * TODO: Ideally we would also check the results of these functions, but it's too much work for now. */
	public void precheckOriginalResults(Collection<DefUnit> resultDefUnitsOriginal) {
		for (DefUnit defUnit : resultDefUnitsOriginal) {
			defUnit.toStringAsElement();
		}
	}
	
	public void removeDummyDefUnits(Collection<DefUnit> resultDefUnits) {
		for (Iterator<DefUnit> iterator = resultDefUnits.iterator(); iterator.hasNext(); ) {
			DefUnit defUnit = iterator.next();
			
			if(defUnit.getName().equals("_dummy")) {
				iterator.remove();
			}
		}
	}
	
	public void prepRefSearchTest_________(MetadataEntry mde) {
		String testStringDescriptor = mde.sourceValue;
		
		ECompletionResultStatus expectedStatusCode = ECompletionResultStatus.RESULT_OK;
		String[] expectedResults = null;
		String relexStartPosMarker = null;
		String searchParams = StringUtil.segmentAfterMatch(testStringDescriptor, ">>");
		testStringDescriptor = StringUtil.substringUntilMatch(testStringDescriptor, ">>");
		
		if(testStringDescriptor.startsWith("relexStartPos=")) {
			relexStartPosMarker = StringUtil.segmentAfterMatch(testStringDescriptor, "relexStartPos=");
			testStringDescriptor = null;
		} else if(testStringDescriptor.startsWith("!")) {
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
		
		int offset = mde.offset;
		
		runRefSearchTest(offset, searchParams, expectedStatusCode, expectedResults, relexStartPosMarker);
	}
	
	public void runRefSearchTest(int offset, @SuppressWarnings("unused") String searchParams, 
		ECompletionResultStatus expectedStatusCode, String[] expectedResults, String relexStartPosMarker) {
		
		DefUnitCollector collector = new DefUnitCollector();
		PrefixDefUnitSearch search = PrefixDefUnitSearch.doCompletionSearch(parseResult, offset, mr, collector);
		
		assertTrue(relexStartPosMarker == null || search.relexStartPos == getMarkerPosition(relexStartPosMarker));
		assertTrue(search.getResultCode() == expectedStatusCode);
		if(expectedResults != null) {
			checkResults(collector.results, expectedResults);
		}
	}
	
	public void runFindFailTest_________(MetadataEntry mde) {
		DirectDefUnitResolve resolveResult = resolveAtOffset(mde.offset);
		assertTrue(resolveResult.pickedRef == null || resolveResult.invalidPickRef);
	}
	
	public DirectDefUnitResolve resolveAtOffset(int offset) {
		return ReferenceResolver.resolveAtOffset(getParseResult(), offset, mr);
	}
	
	public void runFindTest_________(MetadataEntry mde) {
		doFindTest(mde);
	}
	
	public DirectDefUnitResolve doFindTest(MetadataEntry mde) {
		String[] expectedResults = splitValues(mde.sourceValue);
		return doRunFindTest(mde.offset, expectedResults);
	}
	
	public void runFindMissingTest_________(MetadataEntry mde) {
		assertTrue(mde.sourceValue == null);
		DirectDefUnitResolve result = doRunFindTest(mde.offset, NewUtils.EMPTY_STRING_ARRAY);
		assertTrue(result.pickedRef.syntaxIsMissingIdentifier());
	}
	
	public DirectDefUnitResolve doRunFindTest(int offset, String[] expectedResults) {
		DirectDefUnitResolve resolveResult = resolveAtOffset(offset);
		checkResults(resolveResult.getResolvedDefUnits(), expectedResults, false);
		return resolveResult;
	}
	
}