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
		while(true) {
			int offset = fullSource.indexOf("/+_#split");
			
			if(offset == -1) {
				runTestCommands(fullSource, defaultModuleName);
				break;
			}
			
			String splitSource = fullSource.substring(0, offset);
			runTestCommands(splitSource, defaultModuleName);
			
			offset = fullSource.indexOf("_+/", offset) + 3;
			fullSource = fullSource.substring(offset);
		}
	}
	
	
	protected static int getMarkerOffset(String source, String targetMarkerName) {
		int targetOffset = -1;
		
		int offset = 0;
		while(true) {
			offset = source.indexOf("/+", offset);
			if(offset == -1)
				break;
			int commentStartOffset = offset;
			offset += 2;
			
			offset = source.indexOf("+/", offset);
			if(offset == -1)
				break;
			offset += 2;
			
			String markerName = source.substring(commentStartOffset+2, offset-2);
			if(markerName.length() < 2) 
				continue;
			
			int markerOffset;
			if(markerName.charAt(0) == '@') {
				markerOffset = commentStartOffset;
				markerName = markerName.substring(1);
			} else if(markerName.charAt(markerName.length()-1) == '@') {
				markerOffset = offset;
				markerName = markerName.substring(0, markerName.length()-1);
			} else {
				continue;
			}
			
			if(targetMarkerName.equals(markerName)) {
				assertTrue(targetOffset == -1); // can only be found once.
				targetOffset = markerOffset;
			}
		}
		assertTrue(targetOffset != -1);
		return targetOffset;
	}
	
	protected static String upUntil(String source, int offset, String string) {
		int endIx = source.indexOf(string, offset);
		return source.substring(offset, endIx);
	}
	
	protected void runTestCommands(String source, String defaultModuleName) {
		Module testModule = Parser__CommonTest.parseSource(source, false, false, defaultModuleName).neoModule;
		
		int offset = 0;
		while(true) {
			offset = source.indexOf("/+#", offset);
			
			if(offset == -1) 
				break;
			
			offset += 3;
			
			final int commandOffset = offset;
			offset = source.indexOf("(", offset);
			String command = source.substring(commandOffset, offset);
			offset += 1;
			
			String paramList = upUntil(source, offset, ")");
			offset += paramList.length() + 1;
			
			int commandEndOffset = source.indexOf("+/", offset);
			commandEndOffset += 2;
			
			if(command.equals("find")) {
				assertTrue(source.charAt(offset) == '@');
				
				ASTNeoNode node = ASTNodeFinder.findElement(testModule, commandEndOffset, true);
				Reference ref = assertInstance(node, Reference.class);
				Collection<DefUnit> results = ref.findTargetDefUnits(new NullModuleResolver(), false);
				
				if(paramList.equals(":null")) {
					assertTrue(results == null);
				} else {
					String markerName = upUntil(source, offset+1, "+/");
					int targetOffset = markerName.equals(":synthetic") ? -2 : getMarkerOffset(source, markerName);
					String targetName;
					if(paramList.equals("=")) {
						targetName = ref.toStringAsElement();
						checkSingleResult(results, targetOffset, targetName);
					} else {
						throw assertFail();
					}
				}
			} else if(command.equals("complete")) {
				
				CompletionSession session = new CompletionSession();
				DefUnitArrayListCollector defUnitAccepter = new DefUnitArrayListCollector();
				PrefixDefUnitSearch.doCompletionSearch(session, "_unnamed_", source, 
						commandEndOffset, new NullModuleResolver(), defUnitAccepter);
				
				String[] expectedResults = paramList.split(",");
				for (int i = 0; i < expectedResults.length; i++) {
					expectedResults[i] = expectedResults[i].trim();
				}
				List<DefUnit> filteredResults = removeDummyDefinitions(defUnitAccepter.results, "_dummy");
				
				CompareDefUnits.checkResults(filteredResults, expectedResults, false);
				
			} else {
				assertFail();
			}
			
			offset = commandEndOffset;
		}
	}
	
	protected List<DefUnit> removeDummyDefinitions(ArrayList<DefUnit> list, final String dummyName) {
		return CollectionUtil.filter(list, new Predicate<DefUnit>() {
			
			@Override
			public boolean evaluate(DefUnit obj) {
				return areEqual(obj.getName(), dummyName);
			}
		});
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
	
}