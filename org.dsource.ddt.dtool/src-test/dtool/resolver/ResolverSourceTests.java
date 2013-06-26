package dtool.resolver;

import static dtool.util.NewUtils.assertNotNull_;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import static melnorme.utilbox.core.CoreUtil.areEqual;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import melnorme.utilbox.core.Predicate;
import melnorme.utilbox.misc.ArrayUtil;
import melnorme.utilbox.misc.StringUtil;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runners.Parameterized.Parameters;

import dtool.ast.ASTNode;
import dtool.ast.ASTNodeFinder;
import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.Module;
import dtool.ast.references.Reference;
import dtool.parser.DeeParser;
import dtool.parser.DeeParserResult;
import dtool.parser.DeeParserSourceTests;
import dtool.parser.DeeTemplatedSourceBasedTest;
import dtool.refmodel.PrefixDefUnitSearch;
import dtool.refmodel.api.IModuleResolver;
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
		runAnnotatedTests(getTestCasesFromFile(commonDefinitions), false);
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
				File projectFolder = new File(file.getParent(), assertNotNull_(mde.value));
				mr = new InstrumentedModuleResolver(projectFolder);
			} else if(mde.name.equals("REFSEARCH")) {
				testsLogger.println("#REFSEARCH:" + mde);
				runRefSearchTest___________(parseResult, mr, defaultOffset, mde);
			} else if(mde.name.equals("FIND")) {
				testsLogger.println("#FIND:" + mde);
				runFindTest_________(parseResult, mr, mde);
			} else if(!(areEqual(mde.value, "flag") || areEqual(mde.name, "comment"))) {
				assertFail("Unknown metadata");
			}
		}
	}
	
	public void runRefSearchTest___________(DeeParserResult parseResult, IModuleResolver mr,
		int defaultOffset, MetadataEntry mde) {
		String[] expectedResults = splitValues(mde.sourceValue);
		for (int i = 0; i < expectedResults.length; i++) {
			expectedResults[i] = expectedResults[i].trim();
		}
		expectedResults = removeEmptyStrings(expectedResults); 
		
		int offset = defaultOffset != -1 ? defaultOffset : mde.offset;
		
		CompletionCollectorSession session = runCompletionSearch(parseResult, offset, mr);
		
		List<DefUnit> results = session.results;
		CompareDefUnits.checkResults(results, expectedResults, false);
	}
	
	public static String[] splitValues(String string) {
		return string.isEmpty() ? new String[0] : string.split("(\\\r?\\\n)"+"|▪|◘");
	}
	
	public CompletionCollectorSession runCompletionSearch(DeeParserResult parseResult, int offset,
		IModuleResolver mr) {
		CompletionCollectorSession session = new CompletionCollectorSession();
		PrefixDefUnitSearch.doCompletionSearch(session, parseResult, offset, mr, session);
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
	
	public void runFindTest_________(DeeParserResult parseResult, IModuleResolver mr, MetadataEntry mde) {
		ASTNode node = ASTNodeFinder.findElement(parseResult.module, mde.offset);
		Reference ref = assertCast(node, Reference.class);
		LinkedList<DefUnit> resolvedDefUnits = makeLinkedList(ref.findTargetDefUnits(mr, false));
		
		String[] expectedResults = splitValues(mde.sourceValue);
		
		for (String expectedTarget : expectedResults) {
			if(expectedTarget.startsWith("@") ) {
				String markerName = expectedTarget.substring(1);
				removedDefUnitByEndMarker(markerName, resolvedDefUnits);
			} else {
				String moduleName = null;
				String defUnitModuleQualifiedName = expectedTarget;
				if(expectedTarget.contains("/")) {
					moduleName = StringUtil.upUntil(expectedTarget, "/");
					defUnitModuleQualifiedName = StringUtil.fromAfterLastMatch(expectedTarget, "/");
				}
				
				removedDefUnitByName(resolvedDefUnits, moduleName, defUnitModuleQualifiedName);
			}
		}
		assertTrue(resolvedDefUnits.isEmpty());
	}
	
	public void removedDefUnitByName(Collection<DefUnit> resolvedDefUnits, 
		String moduleName, String moduleQualifiedName) {
		for (Iterator<DefUnit> iterator = resolvedDefUnits.iterator(); iterator.hasNext(); ) {
			DefUnit defUnit = iterator.next();
			
			if(moduleName != null ) {
				if(!defUnit.getModuleNode().getFullyQualifiedName().equals(moduleName)) {
					continue; // Not a match
				}
			}
			
			if(getDefUnitModuleQualifedName(defUnit).equals(moduleQualifiedName)) {
				iterator.remove();
				return;
			}
		}
		assertFail(); // Must find a matching result
	}
	
	public String getDefUnitModuleQualifedName(DefUnit defUnit) {
		if(defUnit instanceof Module) {
			return "";
		}
		DefUnit parentDefUnit;
		ASTNode parentNode = defUnit.getParent();
		while(true) {
			if(parentNode instanceof DefUnit) {
				parentDefUnit = (DefUnit) parentNode;
				break;
			}
			parentNode = parentNode.getParent();
		}
		String parentQualifedName = getDefUnitModuleQualifedName(parentDefUnit);
		if(parentQualifedName == "") {
			return defUnit.getName();
		}
		return parentQualifedName + "." + defUnit.getName();
	}
	
	protected void removedDefUnitByEndMarker(String markerName, Collection<DefUnit> resolvedDefUnits) {
		MetadataEntry marker = assertNotNull_(markers.get(markerName));
		
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