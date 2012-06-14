package dtool.resolver;

import static dtool.util.NewUtils.createArrayList;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.util.Collection;

import org.eclipse.dltk.core.ModelException;
import org.junit.Test;

import dtool.ast.ASTNodeFinder;
import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.Module;
import dtool.ast.references.Reference;
import dtool.contentassist.CompletionSession;
import dtool.parser.Parser__CommonTest;
import dtool.refmodel.PrefixDefUnitSearch;
import dtool.refmodel.pluginadapters.IModuleResolver;

public class DeclarationStaticIfTypeTest extends Resolver_BaseTest {
		
	private final class NullModuleResolver implements IModuleResolver {
		@Override
		public String[] findModules(Module refSourceModule, String fqNamePrefix) throws Exception {
			return null;
		}
		
		@Override
		public Module findModule(Module sourceRefModule, String[] packages, String module) throws Exception {
			return null;
		}
	}
	
	protected int getMarkerEndOffset(String source, String marker) throws ModelException {
		return source.indexOf(marker) + marker.length();
	}
	
	protected int getMarkerStartOffset(String source, String marker) throws ModelException {
		return source.indexOf(marker);
	}
	
	@Test
	public void test() throws Exception { test$(); }
	public void test$() throws Exception {
		String source = readTestResourceFile("resolving/decl_staticIfIsType_r.d");
		Module testParse = Parser__CommonTest.testParse(source, true, true);
		
		int marker1 = getMarkerEndOffset(source, "/+@marker1+/");
		int markerOut = getMarkerEndOffset(source, "/+@markerOUT+/");
		
		testRefResolve(source, testParse, "/+#find(T)@marker1+/", marker1);
		testRefResolve(source, testParse, "/+#find(T)@markerOUT+/", markerOut);
		
		int completionMarker = getMarkerStartOffset(source, "/+#completion+/");
		
		CompletionSession session = new CompletionSession();
		DefUnitArrayListCollector defUnitAccepter = new DefUnitArrayListCollector();
		PrefixDefUnitSearch.doCompletionSearch2(session, "_unnamed_", source, 
				completionMarker, null, new NullModuleResolver(), defUnitAccepter);
		
		CompareDefUnits.checkResults(defUnitAccepter.results,
			array("foo", "dummy1", "T"
			// These two should not appear, but semantic engine is not smart enough to figure it out:
				,"var", "dummy2" 
			), 
			false
		);
	}
	
	protected void testRefResolve(String source, Module module, String refMarker, int markerOfTarget)
			throws ModelException {
		int findMarker = getMarkerEndOffset(source, refMarker);
		Reference ref = (Reference) ASTNodeFinder.findElement(module, findMarker, true);
		checkSingleResult(ref, markerOfTarget);
	}
	
	protected void checkSingleResult(Reference ref, int marker) {
		Collection<DefUnit> results = ref.findTargetDefUnits(false);
		assertTrue(results.size() == 1);
		assertEquals(createArrayList(results).get(0).getOffset(), marker);
	}
	
}