package dtool.resolver;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import static melnorme.utilbox.core.CoreUtil.areEqual;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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

import dtool.ast.NodeUtil;
import dtool.ast.declarations.PartialPackageDefUnit;
import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.Module;
import dtool.contentassist.CompletionSession.ECompletionResultStatus;
import dtool.parser.DeeParser;
import dtool.parser.DeeParserResult;
import dtool.parser.DeeParserSourceTests;
import dtool.parser.DeeTemplatedSourceBasedTest;
import dtool.resolver.ReferenceResolver.DirectDefUnitResolve;
import dtool.resolver.api.IModuleResolver;
import dtool.sourcegen.AnnotatedSource;
import dtool.sourcegen.AnnotatedSource.MetadataEntry;
import dtool.sourcegen.TemplatedSourceProcessorParser.TspExpansionElement;
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
	
	/*------------------------------*/
	
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
	
	public static final String DEFAULT_MODULE_NAME = "__resolver_tests";
	
	protected Map<String, MetadataEntry> markers;
	
	@Override
	protected void runAnnotatedSourceTest(AnnotatedSource testCase) {
		String source = testCase.source;
		IModuleResolver mr = new NullModuleResolver();
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
				assertTrue(mr instanceof NullModuleResolver);
				mr = createInstrumentedModuleResolver(mde, parseResult);
			} else if(mde.name.equals("REFSEARCH")) {
				testsLogger.println(mde);
				runRefSearchTest___________(parseResult, mr, defaultOffset, mde);
			} else if(mde.name.equals("FIND")) {
				testsLogger.println(mde);
				runFindTest(parseResult, mr, mde);
			} else if(mde.name.equals("FINDMISSING")) {
				testsLogger.println(mde);
				runFindMissingTest(parseResult, mr, mde);
			} else if(mde.name.equals("FINDFAIL")) {
				testsLogger.println(mde);
				runFindFailTest(parseResult, mde);
			} else if(!(areEqual(mde.value, "flag") || areEqual(mde.name, "comment"))) {
				assertFail("Unknown metadata");
			}
		}
	}
	
	public IModuleResolver createInstrumentedModuleResolver(MetadataEntry mde, DeeParserResult parseResult) {
		String projectDescription = mde.value;
		String moduleName = StringUtil.segmentUntilMatch(projectDescription, "@");
		String projectFolderName = StringUtil.substringAfterMatch(projectDescription, "@");

		File projectFolder = new File(file.getParent(), assertNotNull(projectFolderName));
		return new InstrumentedModuleResolver(projectFolder, moduleName, parseResult);
	}
	
	public void runRefSearchTest___________(DeeParserResult parseResult, IModuleResolver mr,
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
			CompareDefUnits.checkResults(session.results, expectedResults, false, true);
		}
	}
	
	public static String[] splitValues(String string) {
		return string.isEmpty() ? new String[0] : string.split("(\\\r?\\\n)"+"|▪|◘");
	}
	
	public CompletionCollectorSession runCompletionSearch(DeeParserResult parseResult, int offset,
		IModuleResolver mr, String relexStartPosTargetMarker) {
		CompletionCollectorSession session = new CompletionCollectorSession();
		PrefixDefUnitSearch search = PrefixDefUnitSearch.doCompletionSearch(session, parseResult, offset, mr, session);
		assertTrue(relexStartPosTargetMarker == null || 
			search.relexStartPos == getMarkerPosition(relexStartPosTargetMarker));
		return session;
	}
	
	public int getMarkerPosition(String relexStartPosTargetMarker) {
		return assertNotNull(markers.get(relexStartPosTargetMarker)).offset;
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
	
	public void runFindFailTest(DeeParserResult parseResult, MetadataEntry mde) {
		DirectDefUnitResolve resolveResult = ReferenceResolver.resolveAtOffset(parseResult, mde.offset, null);
		assertTrue(resolveResult.pickedRef == null || resolveResult.invalidPickRef);
	}
	
	public void runFindTest(DeeParserResult parseResult, IModuleResolver mr, MetadataEntry mde) {
		String[] expectedResults = splitValues(mde.sourceValue);
		runFindTest_________(parseResult, mr, mde.offset, expectedResults);
	}
	
	public void runFindMissingTest(DeeParserResult parseResult, IModuleResolver mr, MetadataEntry mde) {
		assertTrue(mde.sourceValue == null);
		DirectDefUnitResolve result = runFindTest_________(parseResult, mr, mde.offset, NewUtils.EMPTY_STRING_ARRAY);
		assertTrue(result.pickedRef.syntaxIsMissingIdentifier());
	}
	
	public DirectDefUnitResolve runFindTest_________(DeeParserResult parseResult, IModuleResolver mr, int offset, 
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