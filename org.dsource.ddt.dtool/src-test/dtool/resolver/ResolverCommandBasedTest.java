package dtool.resolver;

import static dtool.util.NewUtils.createArrayList;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.util.Collection;

import dtool.ast.ASTNodeFinder;
import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.Module;
import dtool.ast.references.Reference;
import dtool.contentassist.CompletionSession;
import dtool.parser.Parser__CommonTest;
import dtool.refmodel.PrefixDefUnitSearch;


public class ResolverCommandBasedTest extends Resolver_BaseTest {
	
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
		
		return targetOffset;
	}
	
	protected static String upUntil(String source, int offset, String string) {
		int endIx = source.indexOf(string, offset);
		return source.substring(offset, endIx);
	}
	
	public void runTestCommands(String source) {
		Module testModule = Parser__CommonTest.testParse(source, true, true);
		
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
				String targetName = paramList;
				
				assertTrue(source.charAt(offset) == '@');
				
				String markerName = upUntil(source, offset+1, "+/");
				int targetOffset = getMarkerOffset(source, markerName);
				
				
				Reference ref = (Reference) ASTNodeFinder.findElement(testModule, commandEndOffset, true);
				checkSingleResult(ref, targetOffset);
				
				assertTrue(ref.toStringAsElement().equals(targetName));
			} else if(command.equals("complete")) {
				
				CompletionSession session = new CompletionSession();
				DefUnitArrayListCollector defUnitAccepter = new DefUnitArrayListCollector();
				PrefixDefUnitSearch.doCompletionSearch(session, "_unnamed_", source, 
						commandEndOffset, new NullModuleResolver(), defUnitAccepter);
				
				String[] expectedResults = paramList.split(",");
				for (int i = 0; i < expectedResults.length; i++) {
					expectedResults[i] = expectedResults[i].trim();
				}
				
				CompareDefUnits.checkResults(defUnitAccepter.results, expectedResults, false);
				
			} else {
				assertFail();
			}
			
			offset = commandEndOffset;
		}
	}
	
	protected void checkSingleResult(Reference ref, int marker) {
		Collection<DefUnit> results = ref.findTargetDefUnits(new NullModuleResolver(), false);
		assertTrue(results.size() == 1);
		assertEquals(createArrayList(results).get(0).getOffset(), marker);
	}
	
}