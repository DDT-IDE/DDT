package dtool.resolver;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import static melnorme.utilbox.core.CoreUtil.areEqual;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import melnorme.utilbox.core.Predicate;
import melnorme.utilbox.misc.CollectionUtil;
import dtool.ast.ASTNeoNode;
import dtool.ast.ASTNodeFinder;
import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.Module;
import dtool.ast.references.Reference;
import dtool.contentassist.CompletionSession;
import dtool.parser.Parser__CommonTest;
import dtool.refmodel.PrefixDefUnitSearch;


public class ResolverCommandBasedTest extends Resolver_BaseTest {
	
	protected void splitSourceAndRunTestCommands(String fullSource, String defaultModuleName) throws IOException {
		String[] splitSources = splitSourceBasedTests(fullSource);
		for (String splitSource : splitSources) {
			runTestCommands(splitSource, defaultModuleName);
		}
	}
	
	
	protected static String upUntil(String source, int offset, String string) {
		int endIx = source.indexOf(string, offset);
		return source.substring(offset, endIx);
	}
	
	protected void runTestCommands(String source, String defaultModuleName) {
		Module testModule = Parser__CommonTest.parseSource(source, false, false, defaultModuleName).neoModule;
		
		int offset = 0;
		while(true) {
			// Look for the test command marker
			offset = source.indexOf("/+#", offset);
			
			if(offset == -1) 
				break;
			
			offset += 3;
			
			final int commandOffset = offset;
			offset = source.indexOf("(", offset);
			// read the command name and parameter list
			String command = source.substring(commandOffset, offset);
			offset += 1;
			
			String paramList = upUntil(source, offset, ")");
			offset += paramList.length() + 1;
			
			int commandEndOffset = source.indexOf("+/", offset);
			commandEndOffset += 2;
			
			if(command.equals("find")) {
				assertTrue(source.charAt(offset) == '@');
				String markerName = upUntil(source, offset+1, "+/");
				
				doFindTest(source, testModule, paramList, commandEndOffset, markerName);
			} else if(command.equals("complete")) {
				doCompletionTest(source, defaultModuleName, paramList, commandEndOffset);
			} else {
				assertFail();
			}
			
			offset = commandEndOffset;
		}
	}
	
	protected void doFindTest(String source, Module testModule, String paramList, int commandEndOffset, String markerName) {
		ASTNeoNode node = ASTNodeFinder.findElement(testModule, commandEndOffset, true);
		Reference ref = assertInstance(node, Reference.class);
		Collection<DefUnit> results = ref.findTargetDefUnits(new NullModuleResolver(), false);
		
		if(paramList.equals(":null")) {
			assertTrue(results == null);
			assertTrue(markerName.isEmpty());
		} else {
			int targetOffset = markerName.equals(":synthetic") ? -2 : getMarkerOffset(source, markerName);
			String targetName;
			if(paramList.equals("=")) {
				targetName = ref.toStringAsElement();
				checkSingleResult(results, targetOffset, targetName);
			} else {
				throw assertFail();
			}
		}
	}
	
	protected void checkSingleResult(Collection<DefUnit> results, int expectedOffset, String targetName) {
		DefUnit defUnit = getSingleElement(results);
		if(expectedOffset == -2) {
			assertTrue(defUnit.isSynthetic());
		} else {
			assertEquals(defUnit.getOffset(), expectedOffset);
		}
		if(targetName != null) {
			assertEquals(targetName, defUnit.getName());
		}
	}
	
	public static <T> T getSingleElement(Collection<? extends T> results) {
		assertNotNull(results);
		assertTrue(results.size() == 1);
		return results.iterator().next();
	}
	
	protected void doCompletionTest(String source, String defaultModuleName, String paramList, int commandEndOffset) {
		CompletionSession session = new CompletionSession();
		DefUnitArrayListCollector defUnitAccepter = new DefUnitArrayListCollector();
		PrefixDefUnitSearch.doCompletionSearch(session, defaultModuleName, source, 
				commandEndOffset, new NullModuleResolver(), defUnitAccepter);
		
		String[] expectedResults = paramList.split(",");
		for (int i = 0; i < expectedResults.length; i++) {
			expectedResults[i] = expectedResults[i].trim();
		}
		List<DefUnit> filteredResults = removeDummyDefinitions(defUnitAccepter.results, "_dummy");
		
		CompareDefUnits.checkResults(filteredResults, expectedResults, false);
	}
	
	protected List<DefUnit> removeDummyDefinitions(ArrayList<DefUnit> list, final String dummyName) {
		return CollectionUtil.filter(list, new Predicate<DefUnit>() {
			
			@Override
			public boolean evaluate(DefUnit obj) {
				return areEqual(obj.getName(), dummyName);
			}
		});
	}
	
}