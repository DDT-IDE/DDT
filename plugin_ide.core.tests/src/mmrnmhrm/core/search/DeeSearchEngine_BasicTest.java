package mmrnmhrm.core.search;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;

import melnorme.lang.tooling.bundles.ISemanticContext;
import mmrnmhrm.core.DLTKUtils;
import mmrnmhrm.core.engine_client.DToolClient;

import org.eclipse.dltk.core.IMember;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.IType;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.core.search.IDLTKSearchConstants;
import org.eclipse.dltk.core.search.SearchMatch;
import org.eclipse.dltk.core.search.SearchPattern;
import org.junit.Test;

import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.DefinitionVariable;
import dtool.ast.definitions.Module;
import dtool.parser.DeeParserResult.ParsedModule;
import dtool.tests.utils.MiscNodeUtils;

public class DeeSearchEngine_BasicTest extends DeeSearchEngine_Test {
	
	@Test
	public void searchType() throws Exception { searchType$(); }
	public void searchType$() throws Exception {
		testSearchForElement(getElement(searchProj, "srcA", "pack", "mod1").getType("Mod1Class"));
		testSearchForElement(getElement(searchProj, "srcA", "pack", "mod1"));
		
		// The test boundaries we are exploring here mostly relate to the package name
		testSearchForElement(getElement(searchProj, "srcA", "pack/subpack", "mod3").getType("Mod3Class"));
		testSearchForElement(getElement(searchProj, "srcA", "", "mod0").getType("Mod0Class"));
		testSearchForElement(getElement(searchProj, "srcA", "", "mod0"));
	}
	
	@Test
	public void searchVar() throws Exception { searchVar$(); }
	public void searchVar$() throws Exception {
		testSearchForElement(getElement(searchProj, "srcA", "pack", "mod1").getField("mod1Var"));
		testSearchForElement(getElement(searchProj, "srcA", "pack", "mod1").getType("Mod1Class").getField("foo"));
		
		// The test boundaries we are exploring here mostly relate to the package name
		testSearchForElement(getElement(searchProj, "srcA", "pack/subpack", "mod3").getField("mod3Var"));
		testSearchForElement(getElement(searchProj, "srcA", "", "mod0").getField("mod0Var"));
	}
	
	@Test
	public void searchMethod() throws Exception { searchMethod$(); }
	public void searchMethod$() throws Exception {
		IType mod1 = getElement(searchProj, "srcA", "pack", "mod1");
		testSearchForElement(mod1.getMethod("mod1Func"), true);
		testSearchForElement(mod1.getType("Mod1Class").getMethod("methodA"), true);
		
		// The test boundaries we are exploring here mostly relate to the package name
		testSearchForElement(getElement(searchProj, "srcA", "pack/subpack", "mod3").getMethod("mod3Func"), true);
		testSearchForElement(getElement(searchProj, "srcA", "", "mod0").getMethod("mod0Func"), true);
		
		// TODO: test search with homonym methods with different parameters
	}
	
	@Test
	public void searchOther() throws Exception { searchOther$(); }
	public void searchOther$() throws Exception {
		IType mod0 = getElement(searchProj, "srcA", "", "mod0");
		
		testSearch(createStringPattern("mod0", IDLTKSearchConstants.TYPE, DECLARATIONS), elementSet(mod0));
		testNameSearch(createStringPattern("mod0", IDLTKSearchConstants.TYPE, DECLARATIONS), elementSet(mod0));
		
		SearchPattern searchPattern = createStringPattern("pack", IDLTKSearchConstants.TYPE, REFERENCES);
//		SearchRequestorResultCollector requestor = 
				executeSearch(searchPattern);
		// TODO test this more
	}
	
	/*------------------  Test References   -------------------*/
	
	@Test
	public void searchTypeRefs() throws Exception { searchTypeRefs$(); }
	public void searchTypeRefs$() throws Exception {
		IMember element = getElement(searchProj, "srcB", "", "sampledefs").getType("Class");
		SearchRequestorResultCollector collector = testSearchForElementReferences(element);
		
		ISourceModule module = getModule(searchProj, "srcB", "", "sampledefs_refs");
		
		checkMarkers(collector, module, "/*sampledefs.Class*/");
	}
	
	@Test
	public void searchVarRefs() throws Exception { searchVarRefs$(); }
	public void searchVarRefs$() throws Exception {
		IMember element = getElement(searchProj, "srcB", "", "sampledefs").getType("Class").getField("fieldA");
		SearchRequestorResultCollector collector = testSearchForElementReferences(element);
		
		ISourceModule module = getModule(searchProj, "srcB", "", "sampledefs_refs");
		
		checkMarkers(collector, module, "/*sampledefs.Class.fieldA*/");
	}
	
	@Test
	public void searchMethodRefs() throws Exception { searchMethodRefs$(); }
	public void searchMethodRefs$() throws Exception {
		IMember element = getElement(searchProj, "srcB", "", "sampledefs").getType("Class").getMethod("methodB");
		SearchRequestorResultCollector collector = testSearchForElementReferences(element);
		//printIndexDebugInfo(searchProj.getProject())
		ISourceModule module = getModule(searchProj, "srcB", "", "sampledefs_refs");
		
		checkMarkers(collector, module, "/*sampledefs.Class.methodB*/");
	}
	
	protected void checkMarkers(SearchRequestorResultCollector collector, ISourceModule module, String key) 
		throws ModelException {
		ArrayList<Integer> offsets = getMarkersEnd(module, key);
		assertTrue(collector.results.size() >= offsets.size());
		
		for (Integer markerOffset : offsets) {
			boolean matchFound = false;
			for (Iterator<SearchMatch> iterator = collector.matches.iterator(); iterator.hasNext(); ) {
				SearchMatch match = iterator.next();
				
				ISourceModule matchModule = assertInstance(match.getElement(), IMember.class).getSourceModule();
				if(DeeSearchEngineTestUtils.getSourceModuleFQName(matchModule).equals("sampledefs_refs")) {
					if(match.getOffset() == markerOffset) {
						// This marker offset is accounted for
						matchFound = true;
						iterator.remove();
						break;
					}
				}
			}
			assertTrue(matchFound);
		}
	}
	
	protected ArrayList<Integer> getMarkersEnd(ISourceModule module, String string) throws ModelException {
		String contents = module.getBuffer().getContents();
		ArrayList<Integer> offsets = new ArrayList<Integer>();
		
		int indexOf = 0;
		do {
			indexOf = contents.indexOf(string, indexOf);
			if(indexOf != -1) {
				offsets.add(indexOf + string.length());
				indexOf++;
			} else {
				assertTrue(!offsets.isEmpty());
				return offsets;
			}
		} while (true);
	}
	
	@Test
	public void testTestData() throws Exception { testTestData$(); }
	public void testTestData$() throws Exception {
		ISourceModule srcModule = getModule(searchProj, "srcB", "", "search2");
		Path filePath = DLTKUtils.getFilePath(srcModule.getResource().getLocation());
		ParsedModule parseModule = DToolClient.getDefaultModuleCache().getParsedModuleOrNull(filePath);
		Module module = parseModule.module;
		
		DefUnit defUnit = MiscNodeUtils.getDefUniFromScope(module.getChildren(), "xxxTestUnboundRef");
		ISemanticContext mr = DToolClient.getDefault().getResolvedModule(filePath).getSemanticContext();
		DefinitionVariable defVar = assertInstance(defUnit, DefinitionVariable.class);
		assertTrue(defVar.type.findTargetDefElement(mr) == null);
	}
	
}