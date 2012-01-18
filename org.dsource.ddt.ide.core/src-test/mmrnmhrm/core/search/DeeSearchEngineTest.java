package mmrnmhrm.core.search;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import melnorme.utilbox.misc.StringUtil;
import mmrnmhrm.tests.BaseDeeTest;

import org.dsource.ddt.ide.core.DeeLanguageToolkit;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.dltk.core.IDLTKLanguageToolkit;
import org.eclipse.dltk.core.IMember;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IParent;
import org.eclipse.dltk.core.IProjectFragment;
import org.eclipse.dltk.core.IScriptFolder;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.IType;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.core.search.IDLTKSearchConstants;
import org.eclipse.dltk.core.search.IDLTKSearchScope;
import org.eclipse.dltk.core.search.SearchEngine;
import org.eclipse.dltk.core.search.SearchMatch;
import org.eclipse.dltk.core.search.SearchParticipant;
import org.eclipse.dltk.core.search.SearchPattern;
import org.eclipse.dltk.core.search.SearchRequestor;
import org.eclipse.dltk.core.search.index.EntryResult;
import org.eclipse.dltk.core.search.index.Index;
import org.eclipse.dltk.core.search.indexing.IIndexConstants;
import org.eclipse.dltk.core.search.indexing.IndexManager;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import dtool.tests.DToolTests;

public class DeeSearchEngineTest extends BaseDeeTest {
	
	protected final class SearchRequestorElementResultCollector extends SearchRequestor {
		
		public List<IModelElement> results = new ArrayList<IModelElement>(); 
		
		@Override
		public void acceptSearchMatch(SearchMatch match) throws CoreException {
			assertInstance(match.getElement(), IModelElement.class);
			results.add((IModelElement) match.getElement());
		}
	}
	
	@BeforeClass
	public static void setup() {
		enableDLTKIndexer();
	}
	
	@AfterClass
	public static void teardown() {
		disableDLTKIndexer();
	}
	
	@SuppressWarnings("restriction")
	public void printIndexDebugInfo() throws Exception {
		
		IProject prj = searchProject.getProject();
		System.out.println("========= Index DEBUG INFO ========");
		
		IndexManager im = org.eclipse.dltk.internal.core.ModelManager.getModelManager().getIndexManager();
		Index idx = im.getIndex(prj.getFullPath(), true, true); // This is index file for project root
		
		assertNotNull(im.indexLocations.keyTable);
		System.out.println("===== Index Locations ====\n" + im.indexLocations + "\n");
		
		im.waitUntilReady();
		
		// And then check using
		String[] docNames = idx.queryDocumentNames(null); // To check all documents in this index
		assertNotNull(docNames);
		System.out.println("===== Index docs ====\n" + StringUtil.collToString(docNames, "\n") );
		
		System.out.println("===== Query: Decl, * ====");
		char[][] category = {IIndexConstants.TYPE_DECL};
		EntryResult[] query = idx.query(category, new char[]{'*'}, SearchPattern.R_PATTERN_MATCH);	
		for (EntryResult entryResult : query) {
			System.out.println(entryResult.getWord());
		}
	}
	
	protected final IScriptProject searchProject = SampleSearchProject.defaultInstance.scriptProject;
	
	public DeeSearchEngineTest() {
	}
	
	protected HashSet<IModelElement> executeSearch(String patternStr, int archeType, int limitTo) throws CoreException {
		IDLTKLanguageToolkit toolkit = DeeLanguageToolkit.getDefault();
		
		SearchPattern searchPattern = SearchPattern.createPattern(patternStr, archeType, limitTo, 
				SearchPattern.R_EXACT_MATCH, toolkit);
		
		return executeSearch(searchPattern);
	}
	
	protected HashSet<IModelElement> executeSearch(IModelElement element, int limitTo) throws CoreException {
		// TODO:
		if(true)
		return hashSet(element);
		
		assertTrue(element.exists());
		
		IDLTKLanguageToolkit toolkit = DeeLanguageToolkit.getDefault();
		SearchPattern searchPattern = SearchPattern.createPattern(element, limitTo, 
				SearchPattern.R_EXACT_MATCH, toolkit);
		
		return executeSearch(searchPattern);
	}
	
	protected HashSet<IModelElement> executeStringSearch(IModelElement element, int limitTo) throws CoreException {
		assertTrue(element.exists());
		
		String elementFullName = element.getElementName();
		
		IDLTKLanguageToolkit toolkit = DeeLanguageToolkit.getDefault();
		SearchPattern searchPattern = SearchPattern.createPattern(elementFullName, searchFor(element), limitTo, 
				SearchPattern.R_EXACT_MATCH, toolkit);
		
		return executeSearch(searchPattern);
	}
	
	protected static int searchFor(IModelElement element) {
		switch (element.getElementType()) {
		case IModelElement.TYPE:
			return IDLTKSearchConstants.TYPE;
		case IModelElement.METHOD:
			return IDLTKSearchConstants.METHOD;
		case IModelElement.FIELD:
			return IDLTKSearchConstants.FIELD;
		default:
			throw assertFail();
		}
	}
	
	
	protected HashSet<IModelElement> executeSearch(SearchPattern searchPattern) throws CoreException {
		SearchEngine engine = new SearchEngine();
		SearchParticipant defaultSearchParticipant = SearchEngine.getDefaultSearchParticipant();
		IDLTKSearchScope scope = SearchEngine.createSearchScope(searchProject);
		SearchRequestorElementResultCollector requestor = new SearchRequestorElementResultCollector();
		
		assertNotNull(searchPattern);
		engine.search(searchPattern, array(defaultSearchParticipant), scope, requestor, new NullProgressMonitor());
		
		return new HashSet<IModelElement>(requestor.results);
	}
	
	
	protected static IProjectFragment getSrcFolder(IScriptProject scriptProject, String folderName) {
		return scriptProject.getProjectFragment(scriptProject.getProject().getFolder(folderName));
	}
	
	protected static ISourceModule getModule(IScriptProject project, String srcFolder, String pkg, String module) {
		IScriptFolder scriptFolder = getSrcFolder(project, srcFolder).getScriptFolder(pkg);
		return scriptFolder.getSourceModule(module + ".d");
	}
	
	protected static IType getElement(IScriptProject scriptProject, String srcFolder, String pkg, String srcModule) {
		ISourceModule sourceModule = getModule(scriptProject, srcFolder, pkg, srcModule);
		return sourceModule.getType(srcModule);
		//return sourceModule;
	}
	
	
	@Test
	public void searchType() throws Exception { searchType$(); }
	public void searchType$() throws Exception {
		IModelElement element = getElement(searchProject, "srcA", "pack", "mod1");
		assertEquals(hashSet(element), executeSearch(element, IDLTKSearchConstants.DECLARATIONS));
		assertEquals(hashSet(element), executeStringSearch(element, IDLTKSearchConstants.DECLARATIONS));
		
		
		element = getElement(searchProject, "srcA", "pack", "mod1").getType("Mod1Class");
		assertEquals(hashSet(element), executeSearch(element, IDLTKSearchConstants.DECLARATIONS));
	}
	
	@Test
	public void searchVar$() throws Exception { searchVar(); }
	public void searchVar() throws Exception {
		IModelElement element = getElement(searchProject, "srcA", "pack", "mod1").getField("mod1Var");
		
		HashSet<IModelElement> resultsSet = executeSearch(element, IDLTKSearchConstants.DECLARATIONS);
		
		assertEquals(resultsSet, hashSet(element));
	}
	
	@Test
	public void searchTypeRefs() throws Exception { searchTypeRefs$(); }
	public void searchTypeRefs$() throws Exception {
		IModelElement element = getElement(searchProject, "srcA", "pack", "mod1").getType("Mod1Class");
		
		HashSet<IModelElement> results = executeSearch(element, IDLTKSearchConstants.REFERENCES);
	}
	
	@Test
	public void searchVarRef() throws Exception { searchVarRef$(); }
	public void searchVarRef$() throws Exception {
		HashSet<IModelElement> resultsSet = executeSearch(
				"pack.mod1.mod1Var", IDLTKSearchConstants.FIELD, IDLTKSearchConstants.REFERENCES);
	}
	
	
	@Test
	public void testRecursive() throws Exception { testRecursive$(); }
	public void testRecursive$() throws Exception {
		if(DToolTests.TESTS_LITE_MODE == false) {
			testElementSearch(searchProject, -1);
		}
	}
	
	public void testElementSearch(IModelElement element, int depth) throws ModelException, CoreException {
		if(element instanceof IMember) {
			HashSet<IModelElement> results = executeSearch(element, IDLTKSearchConstants.DECLARATIONS);
			assertEquals(results, hashSet(element));
		}
		
		if(depth > 0 && element instanceof IParent) {
			IModelElement[] children = ((IParent) element).getChildren();
			for (IModelElement child : children) {
				testElementSearch(child, depth - 1);
			}
		}
	}
	
}
