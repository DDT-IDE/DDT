package dtool.resolver;

import static dtool.resolver.ResolverUtil.findModule_unchecked;
import static dtool.tests.MiscDeeTestUtils.fnDefUnitToStringAsElement;
import static dtool.tests.MiscDeeTestUtils.fnDefUnitToStringAsName;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import static melnorme.utilbox.core.CoreUtil.areEqual;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import melnorme.utilbox.core.Predicate;
import melnorme.utilbox.misc.ArrayUtil;
import melnorme.utilbox.misc.StringUtil;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runners.Parameterized.Parameters;

import dtool.ast.NodeUtil;
import dtool.ast.declarations.PartialPackageDefUnit;
import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.Module;
import dtool.contentassist.CompletionSession.ECompletionResultStatus;
import dtool.parser.CommonTemplatedSourceBasedTest;
import dtool.parser.DeeParser;
import dtool.parser.DeeParserResult;
import dtool.parser.DeeParserSourceTests;
import dtool.resolver.ReferenceResolver.DirectDefUnitResolve;
import dtool.resolver.api.IModuleResolver;
import dtool.sourcegen.AnnotatedSource;
import dtool.sourcegen.AnnotatedSource.MetadataEntry;
import dtool.sourcegen.TemplatedSourceProcessorParser.TspExpansionElement;
import dtool.util.NewUtils;

public class ResolverSourceTests extends CommonTemplatedSourceBasedTest {
	
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
	public void printTestCaseSource(AnnotatedSource testCase, boolean printCaseSeparator) {
		if(printCaseSeparator) {
			testsLogger.println(">-----------");
		}
		String caseSource = AnnotatedSource.printSourceWithMetadata(testCase);
		testsLogger.println(caseSource);
	}
	
	/*------------------------------*/
	
	public static final class NullModuleResolver implements ITestsModuleResolver {
		@Override
		public String[] findModules(String fqNamePrefix) throws Exception {
			return NewUtils.EMPTY_STRING_ARRAY;
		}
		
		@Override
		public Module findModule(String[] packages, String module) throws Exception {
			return null;
		}
		
		@Override
		public void doCleanup() {
		}
	}
	
	public interface ITestsModuleResolver extends IModuleResolver {
		void doCleanup() throws Exception;
	}
	
	public static final String DEFAULT_MODULE_NAME = "__resolver_tests";
	
	protected static HashMap<String, ITestsModuleResolver> moduleResolvers = new HashMap<>();
	
	protected Map<String, MetadataEntry> markers;
	protected ITestsModuleResolver mr;
	
	@Override
	protected final void runAnnotatedSourceTest(AnnotatedSource testCase) {
		mr = new NullModuleResolver();
		try {
			runAnnotatedSourceTestDo(testCase);
		} finally {
			try {
				mr.doCleanup();
			} catch(Exception e) {
				throw melnorme.utilbox.core.ExceptionAdapter.unchecked(e);
			}
		}
	}
	
	protected void runAnnotatedSourceTestDo(AnnotatedSource testCase) {
		String source = testCase.source;
		int defaultOffset = -1;
		
		
		markers = new HashMap<>();
		for (MetadataEntry mde : testCase.metadata) {
			if(mde.name.startsWith("marker")) {
				if(mde.name.equals("marker_default")) {
					assertTrue(defaultOffset == -1);
					defaultOffset = mde.offset;
				}
				if(mde.value != null) {
					markers.put(mde.value, mde);
				}
				
			}
		}
		
		DeeParserResult parseResult = DeeParser.parseSource(source, DEFAULT_MODULE_NAME);
		
		testsLogger.println("-----");
		
		for (MetadataEntry mde : testCase.metadata) {
			if(mde.name.startsWith("marker")) {
				// already processed
			} else if(mde.name.equals("PROJECT")) {
				assertTrue(mr instanceof NullModuleResolver); // Set only once
				parseResult = setupInstrumentedModuleResolver(mde, parseResult);
			} else if(mde.name.equals("REFSEARCH")) {
				testsLogger.println(mde);
				runRefSearchTest_________(parseResult, mr, defaultOffset, mde);
			} else if(mde.name.equals("FIND")) {
				testsLogger.println(mde);
				runFindTest_________(parseResult, mr, mde);
			} else if(mde.name.equals("FINDMISSING")) {
				testsLogger.println(mde);
				runFindMissingTest_________(parseResult, mr, mde);
			} else if(mde.name.equals("FINDFAIL")) {
				testsLogger.println(mde);
				runFindFailTest_________(parseResult, mde);
			} else if(!(areEqual(mde.value, "flag") || areEqual(mde.name, "comment"))) {
				assertFail("Unknown metadata");
			}
		}
	}
	
	public int getMarkerPosition(String relexStartPosTargetMarker) {
		return assertNotNull(markers.get(relexStartPosTargetMarker)).offset;
	}
	
	public DeeParserResult setupInstrumentedModuleResolver(MetadataEntry mde, DeeParserResult parseResult) {
		String projectDescription = mde.value;
		String moduleName = StringUtil.segmentUntilMatch(projectDescription, "@");
		String projectFolderName = StringUtil.substringAfterMatch(projectDescription, "@");
		
		ITestsModuleResolver existingMR = moduleResolvers.get(projectFolderName);
		mr = updateInstrumentModuleResolver(projectFolderName, moduleName, parseResult, existingMR);
		assertNotNull(mr);
		moduleResolvers.put(projectFolderName, mr); // Cache the MR data
		
		Module resolvedModule = findModule_unchecked(mr, parseResult.module.getFullyQualifiedName());
		if(moduleName != null) {
			assertTrue(resolvedModule != null);
			if(resolvedModule != parseResult.module) {
				String source = parseResult.source;
				return new DeeParserResult(source, resolvedModule, parseResult.ruleBroken, parseResult.errors);
			}
		}
		return parseResult;
	}
	
	public ITestsModuleResolver updateInstrumentModuleResolver(String projectFolderName, String moduleName,
		DeeParserResult parseResult, ITestsModuleResolver existingMR) {
		InstrumentedModuleResolver testMR = (InstrumentedModuleResolver) existingMR;
		if(testMR == null) {
			File projectFolder = new File(file.getParent(), assertNotNull(projectFolderName));
			testMR = new InstrumentedModuleResolver(projectFolder);
		}
		testMR.setExtraModule(moduleName, parseResult);
		return testMR;
	}
	
	/*----------------------------------------*/
	
	public void runRefSearchTest_________(DeeParserResult parseResult, IModuleResolver mr,
		int defaultOffset, MetadataEntry mde) {
		String testStringDescriptor = mde.sourceValue;
		
		ECompletionResultStatus expectedStatusCode = ECompletionResultStatus.RESULT_OK;
		String[] expectedResults = null;
		String relexStartPosMarker = null;
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
		
		int offset = defaultOffset != -1 ? defaultOffset : mde.offset;
		
		CompletionCollectorSession session = runCompletionSearch(parseResult, offset, mr, relexStartPosMarker);
		
		assertTrue(session.resultCode == expectedStatusCode);
		if(expectedResults != null) {
			checkResults(session.results, expectedResults);
		}
	}
	
	public CompletionCollectorSession runCompletionSearch(DeeParserResult parseResult, int offset,
		IModuleResolver mr, String relexStartPosTargetMarker) {
		CompletionCollectorSession session = new CompletionCollectorSession();
		PrefixDefUnitSearch search = PrefixDefUnitSearch.doCompletionSearch(session, parseResult, offset, mr, session);
		assertTrue(relexStartPosTargetMarker == null || 
			search.relexStartPos == getMarkerPosition(relexStartPosTargetMarker));
		return session;
	}
	
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
	
	protected void checkResults(Collection<DefUnit> results, String[] expectedProposalsArr) {
		HashSet<String> resultProposals = prepareResultProposals(results, true);
		HashSet<String> expectedProposals = hashSet(expectedProposalsArr);
		CompareDefUnits.assertEqualSet(resultProposals, expectedProposals);
	}
	
	protected HashSet<String> prepareResultProposals(Collection<DefUnit> results, boolean compareUsingName) {
		HashSet<String> resultProposals = hashSet(strmap(results, 
			compareUsingName ? fnDefUnitToStringAsName(0) : fnDefUnitToStringAsElement(0)));
		
		// To make tests simpler we discard these ones from expected results:
		resultProposals.remove("_dummy");
		resultProposals.remove("_dummy()");
		return resultProposals;
	}
	
	public DirectDefUnitResolve runFindTest_________(DeeParserResult parseResult, IModuleResolver mr, 
		MetadataEntry mde) {
		String[] expectedResults = splitValues(mde.sourceValue);
		return doRunFindTest(parseResult, mr, mde.offset, expectedResults);
	}
	
	public void runFindFailTest_________(DeeParserResult parseResult, MetadataEntry mde) {
		DirectDefUnitResolve resolveResult = ReferenceResolver.resolveAtOffset(parseResult, mde.offset, null);
		assertTrue(resolveResult.pickedRef == null || resolveResult.invalidPickRef);
	}
	
	public void runFindMissingTest_________(DeeParserResult parseResult, IModuleResolver mr, MetadataEntry mde) {
		assertTrue(mde.sourceValue == null);
		DirectDefUnitResolve result = doRunFindTest(parseResult, mr, mde.offset, NewUtils.EMPTY_STRING_ARRAY);
		assertTrue(result.pickedRef.syntaxIsMissingIdentifier());
	}
	
	public DirectDefUnitResolve doRunFindTest(DeeParserResult parseResult, IModuleResolver mr, int offset,
		String[] expectedResults) {
		DirectDefUnitResolve resolveResult = ReferenceResolver.resolveAtOffset(parseResult, offset, mr);
		Collection<DefUnit> resultDefUnitsOriginal = resolveResult.getResolvedDefUnits();
		
		Collection<DefUnit> resultDefUnits = new ArrayList<>(nullToEmpty(resultDefUnitsOriginal));
		for (String expectedTarget : expectedResults) {
			if(expectedTarget.startsWith("@") ) {
				String markerName = expectedTarget.substring(1);
				removedDefUnitByEndMarker(markerName, resultDefUnits);
			} else {
				String moduleName = StringUtil.segmentUntilMatch(expectedTarget, "/");
				String defUnitModuleQualifiedName = StringUtil.substringAfterMatch(expectedTarget, "/");
				
				removeDefUnitByName(resultDefUnits, moduleName, defUnitModuleQualifiedName);
			}
		}
		assertTrue(resultDefUnits.isEmpty());
		return resolveResult;
	}
	
	public void removeDefUnitByName(Collection<DefUnit> resolvedDefUnits, 
		String moduleName, String moduleQualifiedName) {
		String expectedFullyTypedName = moduleName + (moduleQualifiedName != null ? "/" + moduleQualifiedName : "");
		
		for (Iterator<DefUnit> iterator = resolvedDefUnits.iterator(); iterator.hasNext(); ) {
			DefUnit defUnit = iterator.next();
			
			if(moduleName != null ) {
				String defUnitFullyTypedName = getDefUnitFullyTypedName(defUnit);
				if(defUnitFullyTypedName.equals(expectedFullyTypedName)) {
					iterator.remove();
					removeDuplicatedInstances(iterator, defUnit); // TODO: might not be necessary in future
					return;
				} else {
					continue; // Not a match
				}
			} else {
				if(getDefUnitModuleQualifedName(defUnit).equals(moduleQualifiedName)) {
					iterator.remove();
					return;
				}
			}
		}
		assertFail(); // Must find a matching result
	}
	
	public void removeDuplicatedInstances(Iterator<DefUnit> iterator, DefUnit defUnit) {
		while(iterator.hasNext()) {
			DefUnit next = iterator.next();
			if(next == defUnit || isSamePackageNamespace(defUnit, next)) {
				iterator.remove();
			}
		}
	}
	
	public boolean isSamePackageNamespace(DefUnit defUnit, DefUnit next) {
		return (next instanceof PartialPackageDefUnit) && (defUnit instanceof PartialPackageDefUnit)
			&& next.getName().equals(defUnit.getName());
	}
	
	// TODO: review this
	public static String getDefUnitFullyTypedName(DefUnit defUnit) {
		String base = getDefUnitFullyQualifedName(defUnit);
		switch(defUnit.getArcheType()) {
		case Package:
			base += "/";
			break;
		default:
		}
		return base;
	}
	
	public static String getDefUnitFullyQualifedName(DefUnit defUnit) {
		if(defUnit instanceof Module) {
			return ((Module) defUnit).getFullyQualifiedName() + "/";
		}
		
		DefUnit parentDefUnit = NodeUtil.getParentDefUnit(defUnit);
		if(parentDefUnit == null) {
			return defUnit.getName();
		}
		String sep = parentDefUnit instanceof Module ? "" : ".";
		String parentQualifedName = getDefUnitFullyQualifedName(parentDefUnit);
		return parentQualifedName  + sep + defUnit.getName();
	}

	public String getDefUnitModuleQualifedName(DefUnit defUnit) {
		if(defUnit instanceof Module) {
			return "";
		}
		DefUnit parentDefUnit = NodeUtil.getParentDefUnit(defUnit);
		String parentQualifedName = getDefUnitModuleQualifedName(parentDefUnit);
		if(parentQualifedName == "") {
			return defUnit.getName();
		}
		return parentQualifedName + "." + defUnit.getName();
	}
	
	protected void removedDefUnitByEndMarker(String markerName, Collection<DefUnit> resolvedDefUnits) {
		MetadataEntry marker = assertNotNull(markers.get(markerName));
		
		for (Iterator<DefUnit> iterator = resolvedDefUnits.iterator(); iterator.hasNext(); ) {
			DefUnit defUnit = iterator.next();
			if(defUnit.defname.getEndPos() == marker.offset || defUnit.defname.getStartPos() == marker.offset) {
				iterator.remove();
				return;
			}
		}
		assertFail();
	}
	
}