package mmrnmhrm.core.search;

import static java.lang.Math.min;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import static mmrnmhrm.core.search.DeeSearchEngineTestUtils.getSourceModule;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import melnorme.utilbox.misc.StringUtil;

import org.dsource.ddt.ide.core.DeeLanguageToolkit;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.dltk.compiler.CharOperation;
import org.eclipse.dltk.core.IDLTKLanguageToolkit;
import org.eclipse.dltk.core.IMember;
import org.eclipse.dltk.core.IMethod;
import org.eclipse.dltk.core.IModelElement;
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
import org.eclipse.dltk.internal.core.search.matching.FieldPattern;
import org.junit.AfterClass;
import org.junit.BeforeClass;

// TODO consider out of buildpath cases
public abstract class DeeSearchEngine_Test extends DLTKIndexCheckTest implements IDLTKSearchConstants {
	
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
	
	
	@BeforeClass
	public static void setup() {
		enableDLTKIndexer(true);
	}
	
	@AfterClass
	public static void teardown() {
		disableDLTKIndexer();
	}
	
	/* ---------- Some debug helper ---------- */ 
	
	@SuppressWarnings("restriction")
	public static void printIndexDebugInfo(IProject prj) throws Exception {
		
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
		
		System.out.println("===== Query: Type Decl, * ====");
		debugPrintCategory(idx, IIndexConstants.TYPE_DECL);
		System.out.println("===== Query: Field Decl, * ====");
		debugPrintCategory(idx, IIndexConstants.FIELD_DECL);
		System.out.println("===== Query: Method Decl, * ====");
		debugPrintCategory(idx, IIndexConstants.METHOD_DECL);
		System.out.println("===== Query: Ref, * ====");
		debugPrintCategory(idx, IIndexConstants.REF);
		System.out.println("===== Query: Method Ref, * ====");
		debugPrintCategory(idx, IIndexConstants.METHOD_REF);
	}
	
	protected static void debugPrintCategory(Index idx, char[] category) throws IOException {
		char[][] categoryArray = {category};
		EntryResult[] query = idx.query(categoryArray, new char[]{'*'}, SearchPattern.R_PATTERN_MATCH);
		if(query == null) {
			System.out.println("__ null __");
			return;
		}
		for (EntryResult entryResult : query) {
			System.out.println(entryResult.getWord());
		}
	}
	
	/* ----------  ---------- */ 
	
	protected class SearchRequestorResultCollector extends SearchRequestor {
		
		public ArrayList<SearchMatch> matches = new ArrayList<SearchMatch>();
		public ArrayList<Object> results = new ArrayList<Object>(); 
		
		@Override
		public void acceptSearchMatch(SearchMatch match) throws CoreException {
			matches.add(match);
			results.add((Object) match.getElement());
		}
		
		@SuppressWarnings("unchecked")
		protected List<IModelElement> getAsElementResults() {
			return (List<IModelElement>) (Object) results;
		}
	}
	
	protected final IScriptProject searchProj = SampleSearchProject.defaultInstance.scriptProject;
	
	public DeeSearchEngine_Test() {
	}
	
	protected SearchPattern createFocusPattern(IModelElement element, int limitTo) {
		IDLTKLanguageToolkit toolkit = DeeLanguageToolkit.getDefault();
		return SearchPattern.createPattern(element, limitTo, SearchPattern.R_EXACT_MATCH, toolkit);
	}
	
	protected SearchPattern createFQNamePattern(IMember element, int limitTo) {
		String elementFQName = DeeSearchEngineTestUtils.getModelElementFQName(element);
		return createStringPattern(elementFQName, searchFor(element), limitTo);
	}
	
	protected SearchPattern createBaseNamePattern(IMember element, int limitTo) {
		return createStringPattern(element.getElementName(), searchFor(element), limitTo);
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
	
	protected SearchPattern createStringPattern(String patternStr, int searchFor, int limitTo) {
		int matchRule = SearchPattern.R_EXACT_MATCH | SearchPattern.R_CASE_SENSITIVE;
		return createStringPattern(patternStr, searchFor, limitTo, matchRule);
	}
	
	@SuppressWarnings("restriction")
	protected SearchPattern createStringPattern(String patternStr, int searchFor, int limitTo, int matchRule) {
		IDLTKLanguageToolkit toolkit = DeeLanguageToolkit.getDefault();
		SearchPattern pattern = SearchPattern.createPattern(patternStr, searchFor, limitTo, matchRule, toolkit);
		if(pattern instanceof FieldPattern) {
			FieldPattern fieldPattern = (FieldPattern) pattern;
			// we may have to work arround a DLTK bug here
			// -- we are still going to test this codepath, even though it's not accessible because of DLTK bug 
			char[] name = ((FieldPattern) pattern).name;
			int lastIx = CharOperation.lastIndexOf('.', name); 
			if(lastIx != -1) {
				// assume a problem occurred in DLTK where qualifiedName has not been split
				char[] qualification = CharOperation.subarray(fieldPattern.name, 0, lastIx);
				CharOperation.replace(qualification, '.', '$');
				char[] simpleName = CharOperation.subarray(fieldPattern.name, lastIx+1, -1);
				
				return new FieldPattern(fieldPattern.findDeclarations, 
						fieldPattern.findReferences, fieldPattern.findReferences, 
						simpleName, qualification, null, 
						null, null, matchRule, toolkit);
				
			}
		}
		
		return pattern;
	}
	
	/*-----------------------*/
	
	protected SearchRequestorResultCollector executeSearch(SearchPattern searchPattern) throws CoreException {
		return executeSearch(searchPattern, new SearchRequestorResultCollector());
	}
	
	private SearchRequestorResultCollector executeSearch(SearchPattern searchPattern, 
			SearchRequestorResultCollector requestor) throws CoreException {
		assertNotNull(searchPattern);
		
		SearchEngine engine = new SearchEngine();
		SearchParticipant defaultSearchParticipant = SearchEngine.getDefaultSearchParticipant();
		IDLTKSearchScope scope = SearchEngine.createSearchScope(searchProj);
		
		engine.search(searchPattern, array(defaultSearchParticipant), scope, requestor, new NullProgressMonitor());
		return requestor;
	}
	
	protected void testSearch(SearchPattern searchPattern, HashSet<IModelElement> expected) throws CoreException {
		SearchRequestorResultCollector requestor = executeSearch(searchPattern);
		assertEqualSet(new HashSet<Object>(requestor.results), expected);
	}
	
	protected void testPatternSearch(SearchPattern searchPattern, Collection<IModelElement> expectedContains)
			throws CoreException {
		SearchRequestorResultCollector requestor = executeSearch(searchPattern);
		assertTrue(requestor.results.containsAll(expectedContains));
	}
	
	protected void testNameSearch(SearchPattern searchPattern, HashSet<IModelElement> expectedContains)
			throws CoreException {
		final String name = expectedContains.iterator().next().getElementName();
		testNameSearch(searchPattern, expectedContains, name);
	}
	protected void testNameSearch(SearchPattern searchPattern, HashSet<IModelElement> expectedContains, 
		final String name) throws CoreException {
		SearchRequestorResultCollector requestor = executeSearch(searchPattern, new SearchRequestorResultCollector(){
			@Override
			public void acceptSearchMatch(SearchMatch match) throws CoreException {
				IModelElement modelElement = (IModelElement) match.getElement();
				assertTrue(modelElement.getElementName().equals(name));
				super.acceptSearchMatch(match);
			}
		});
		assertTrue(requestor.results.containsAll(expectedContains));
	}
	
	protected void testPrefixSearch(SearchPattern searchPattern, HashSet<IModelElement> expectedContains,
			final String prefix) throws CoreException {
		SearchRequestorResultCollector requestor = executeSearch(searchPattern, new SearchRequestorResultCollector(){
			@Override
			public void acceptSearchMatch(SearchMatch match) throws CoreException {
				IModelElement modelElement = (IModelElement) match.getElement();
				assertTrue(modelElement.getElementName().startsWith(prefix));
				super.acceptSearchMatch(match);
			}
		});
		assertTrue(requestor.results.containsAll(expectedContains));
	}
	
	protected static HashSet<IModelElement> elementSet(IModelElement element) {
		return hashSet(element);
	}
	
	
	protected static final int PREFIX_MATCH = SearchPattern.R_PREFIX_MATCH;
	protected static final int PREFIX_MATCH_CS = SearchPattern.R_PREFIX_MATCH | SearchPattern.R_CASE_SENSITIVE;
	protected static final int PATTERN_MATCH = SearchPattern.R_PATTERN_MATCH; 
	protected static final int REGEXP_MATCH = SearchPattern.R_REGEXP_MATCH; // TODO: test this
	
	protected void testSearchForElement(IMember element) throws CoreException {
		testSearchForElement(element, false);
	}
	
	protected void testSearchForElement(IMember element, boolean extraMethodTests) throws CoreException {
		assertTrue(element.exists());
		if (element instanceof IMethod && ((IMethod) element).isConstructor()) {
			// Constructors not definitions ATM
			return;
		}
		if (element.getElementName().equals("xxx")) {
			return; // Dont test these definitions
		}
		
		doTestSearchForElement(element);
		testSearchForElementReferences(element);
		
		if(extraMethodTests) {
			assertTrue(element instanceof IMethod);
			String elementFQName = DeeSearchEngineTestUtils.getModelElementFQName(element) + "()";
			testSearch(createStringPattern(elementFQName, searchFor(element), DECLARATIONS), elementSet(element));
			
			final String baseName = element.getElementName() + "()";
			testNameSearch(createStringPattern(baseName, searchFor(element), DECLARATIONS), elementSet(element));
		}
	}
	
	protected void doTestSearchForElement(IMember element) throws CoreException {
		testSearch(createFocusPattern(element, DECLARATIONS), elementSet(element));
		testSearch(createFQNamePattern(element, DECLARATIONS), elementSet(element));
		testNameSearch(createBaseNamePattern(element, DECLARATIONS), elementSet(element));
		
		int searchFor = searchFor(element);
		final String name = element.getElementName();
		final String prefix = name.substring(0, min(4, name.length()));
		SearchPattern stringPattern = createStringPattern(prefix, searchFor, DECLARATIONS, PREFIX_MATCH_CS);
		testPrefixSearch(stringPattern, elementSet(element), prefix);
		
		if(name.length() > 4) {
			String patternA = name.substring(0, 2) + "?" + name.substring(3, min(6, name.length())) + "*"; 
			testPatternSearch(createStringPattern(patternA, searchFor, DECLARATIONS, PATTERN_MATCH), elementSet(element));
			
			String patternB = name.substring(0, 2) + "*" + name.substring(4, name.length()-1) + "?"; 
			testPatternSearch(createStringPattern(patternB, searchFor, DECLARATIONS, PATTERN_MATCH), elementSet(element));
		}
	}
	
	/*------------------  Test References   -------------------*/
	
	protected SearchRequestorResultCollector testSearchForElementReferences(IMember element) 
			throws CoreException {
		assertTrue(element.exists());
		return doTestSearchForElementReferences(element, null);
	}
	
	protected static interface MatchChecker {
		public void checkMatch(SearchMatch match) throws CoreException;
	}
	
	protected SearchRequestorResultCollector doTestSearchForElementReferences(IMember element,
			final MatchChecker matchChecker) throws CoreException {
		return doTestSearchForElementReferences(element, matchChecker, false);
	}
	protected SearchRequestorResultCollector doTestSearchForElementReferences(IMember element,
			final MatchChecker matchChecker, boolean extraMethodTests) throws CoreException {
		
		final String keyIdentifier = DeeSearchEngineTestUtils.getModelElementFQName(element);
		final SearchRequestorResultCollector requestor = new SearchRequestorResultCollector(){
			@Override
			public void acceptSearchMatch(SearchMatch match) throws CoreException {
				IModelElement refElement = assertInstance(match.getElement(), IModelElement.class);
				ISourceModule module = getSourceModule(refElement);
				checkKey(module, match.getOffset(), keyIdentifier);
				
				if(matchChecker != null) {
					matchChecker.checkMatch(match);
				}
				
				super.acceptSearchMatch(match);
			}
		};
		
		executeSearch(createFocusPattern(element, REFERENCES), requestor);
		
		SearchRequestorResultCollector requestor2 = executeSearch(createFQNamePattern(element, REFERENCES));
		assertAreEqual(requestor.results, requestor2.results);
		
		SearchRequestorResultCollector requestor3 = executeSearch(createBaseNamePattern(element, REFERENCES));
		assertTrue(requestor3.results.containsAll(requestor.results));
		
		if(extraMethodTests) {
			assertTrue(element instanceof IMethod);
			
			String baseName = element.getElementName() + "()";
			SearchRequestorResultCollector requestor4 
				= executeSearch(createStringPattern(baseName, searchFor(element), REFERENCES));
			assertTrue(requestor4.results.containsAll(requestor.results));
		}
		
		return requestor;
	}
	
	protected void checkKey(ISourceModule module, final int offset, String targetFQName) throws ModelException {
		int length = targetFQName.length() + 4;
		String foundKey = module.getBuffer().getText(offset-length, length);
		if(foundKey.startsWith("/*") && foundKey.endsWith("*/")) {
			assertEquals("/*"+targetFQName+"*/", foundKey);
		}
	}
	
}