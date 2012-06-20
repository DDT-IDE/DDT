package mmrnmhrm.core.search;

import static java.lang.Math.min;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import static melnorme.utilbox.core.CoreUtil.areEqualArrays;
import static melnorme.utilbox.core.CoreUtil.blindCast;
import static melnorme.utilbox.core.CoreUtil.downCast;
import static mmrnmhrm.core.search.DeeSearchEngineTestUtils.getSourceModule;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import melnorme.utilbox.misc.Pair;
import mmrnmhrm.core.codeassist.DeeProjectModuleResolver;

import org.dsource.ddt.ide.core.DeeLanguageToolkit;
import org.dsource.ddt.ide.core.model.DeeModuleParsingUtil;
import org.dsource.ddt.ide.core.model.engine.DeeModelEngine;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.dltk.compiler.CharOperation;
import org.eclipse.dltk.core.IDLTKLanguageToolkit;
import org.eclipse.dltk.core.IMember;
import org.eclipse.dltk.core.IMethod;
import org.eclipse.dltk.core.IModelElement;
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
import org.eclipse.dltk.internal.core.search.matching.FieldPattern;
import org.junit.Test;

import dtool.ast.ASTNeoNode;
import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.DefinitionVariable;
import dtool.ast.definitions.Module;
import dtool.ast.references.Reference;
import dtool.tests.MiscNodeUtils;

// TODO consider out of buildpath cases
public class DeeSearchEngine_Test extends BaseDeeSearchEngineTest implements IDLTKSearchConstants {
	
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
		SearchEngine engine = new SearchEngine();
		SearchParticipant defaultSearchParticipant = SearchEngine.getDefaultSearchParticipant();
		IDLTKSearchScope scope = SearchEngine.createSearchScope(searchProj);
		
		assertNotNull(searchPattern);
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
	protected void testNameSearch(SearchPattern searchPattern, HashSet<IModelElement> expectedContains, final String name)
			throws CoreException {
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
		SearchRequestorResultCollector requestor = executeSearch(searchPattern);
		// TODO test this more
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
	
	protected void checkMarkers(SearchRequestorResultCollector collector, ISourceModule module, String key) throws ModelException {
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
		Module module = DeeModuleParsingUtil.getParsedDeeModule(srcModule);
		
		DefUnit defUnit = MiscNodeUtils.getDefUniFromScope(module.getChildren(), "xxxTestUnboundRef");
		DeeProjectModuleResolver mr = new DeeProjectModuleResolver(srcModule.getScriptProject());
		assertTrue(assertInstance(defUnit, DefinitionVariable.class).type.findTargetDefUnit(mr) == null);
	}
	
	/* ---- En mass test ---- */
	
	@Test
	public void testSearchForAllModelElement() throws Exception { testSearchForAllModelElement$(); }
	public void testSearchForAllModelElement$() throws Exception {
		new DeeSearchEngineTestUtils.ElementsAndDefUnitVisitor() {
			@Override
			protected void visitMember(IMember element) throws CoreException {
				testSearchForElement(element);
			}
			
			@Override
			protected void visitNode(ASTNeoNode node, ISourceModule sourceModule) {
				if(node instanceof DefUnit) {
					// All DefUnits must be searchable
					DefUnit defUnit = (DefUnit) node;
					try {
						IMember element = DeeModelEngine.findCorrespondingModelElement(defUnit, sourceModule);
						if(element != null) {
							testSearchForElement(element);
						}
					} catch (CoreException e) {
						throw melnorme.utilbox.core.ExceptionAdapter.unchecked(e);
					}
				}
			}
			
		}.visitElementsAndNodes(searchProj, 10);
	}
	
	@Test
	public void testSearchForAllModelElementRefs() throws Exception { testSearchForAllModelElementRefs$(); }
	public void testSearchForAllModelElementRefs$() throws Exception {
		
		final HashMap<Pair<ISourceModule, ?>, HashSet<Reference>> defUnitToReferencesMap 
		= new HashMap<Pair<ISourceModule, ?>, HashSet<Reference>>();
		
		new DeeSearchEngineTestUtils.ElementsAndDefUnitVisitor() {
			@Override
			protected void visitNode(ASTNeoNode node, ISourceModule sourceModule) {
				if(node instanceof Reference) {
					Reference reference = (Reference) node;
					
					DeeProjectModuleResolver mr = new DeeProjectModuleResolver(sourceModule.getScriptProject());
					Collection<DefUnit> targetDefUnits = reference.findTargetDefUnits(mr, false);
					if(targetDefUnits == null) {
						return;
					}
					
					for (DefUnit defUnit : targetDefUnits) {
						ArrayList<Integer> nodeTreePath = DeeSearchEngineTestUtils.getNodeTreePath(defUnit);
						
						Module moduleNode = defUnit.getModuleNode();
						if(moduleNode == null) {
							continue; // consider this case more
						}
							
						ISourceModule defUnitSrcModule = findSourceModule(moduleNode, searchProj);
						
						Pair<ISourceModule, ?> key = Pair.create(defUnitSrcModule, nodeTreePath);
						
						if(defUnitToReferencesMap.get(key) == null) {
							defUnitToReferencesMap.put(key, new HashSet<Reference>());
						}
						
						defUnitToReferencesMap.get(key).add(reference);
					}
				}
			}
		}.visitElementsAndNodes(getSrcFolder(searchProj, "srcA"), 10);
		
		
		
		for (Pair<ISourceModule, ?> key : defUnitToReferencesMap.keySet()) {
			ISourceModule sourceModule = key.getFirst();
			ArrayList<Integer> nodeTreePath = blindCast(key.getSecond());
			
			Module deeModule = DeeModuleParsingUtil.parseAndGetAST(sourceModule);
			ASTNeoNode node = DeeSearchEngineTestUtils.getNodeFromPath(deeModule, nodeTreePath);
			
			final DefUnit defUnit = (DefUnit) node;
			final HashSet<Reference> expectedReferences = defUnitToReferencesMap.get(key);
			
			IMember element = DeeModelEngine.findCorrespondingModelElement(defUnit, sourceModule);
//			if(element == null) {
//				// TODO: consider this case
//				continue;
//			}
			
			final String keyIdentifier = DeeSearchEngineTestUtils.getModelElementFQName(element);
			
			doTestSearchForElementReferences(element, new MatchChecker(){
				@Override
				public void checkMatch(SearchMatch match) throws CoreException {
					IMember refElement = assertInstance(match.getElement(), IMember.class);
					ISourceModule module = getSourceModule(refElement);
					checkKey(module, match.getOffset(), keyIdentifier);
					
					checkReferences(expectedReferences, match);
				}
				
				private void checkReferences(final HashSet<Reference> expectedReferences, SearchMatch referenceMatch) {
					// Search for referenceMatch in expectedReferences, then remove it
					
					for (Reference pair : expectedReferences) {
						Reference reference = pair;
						
						String[] refModuleName = DeeSearchEngineTestUtils.getModuleFQName(reference.getModuleNode());
						
						IModelElement modelElement = downCast(referenceMatch.getElement());
						ISourceModule matchSrcModule = DeeSearchEngineTestUtils.getSourceModule(modelElement);
						
						String[] matchModuleName = DeeSearchEngineTestUtils.getModelElementFQNameArray(matchSrcModule);
						
						if( areEqualArrays(refModuleName, matchModuleName) &&
							reference.getOffset() == referenceMatch.getOffset() &&
							reference.getLength() == referenceMatch.getLength()
						) {
							expectedReferences.remove(pair);
							return;
						}
					}
					assertFail();
				}
			});
			
		}
	}
	
	public static ISourceModule findSourceModule(Module module, IScriptProject searchProj) {
		try {
			// TODO: test this, consider multiple named source Packages
			return new DeeProjectModuleResolver(searchProj).findModuleUnit(module);
		} catch (ModelException e) {
			return null;
		}
	}
	
}