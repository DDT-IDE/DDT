package mmrnmhrm.core.search;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import static melnorme.utilbox.core.CoreUtil.areEqual;
import static melnorme.utilbox.core.CoreUtil.downCast;
import static mmrnmhrm.core.search.DeeSearchEngineTestUtils.getSourceModule;

import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import melnorme.lang.tooling.ast.INamedElementNode;
import melnorme.lang.tooling.ast_actual.ASTNode;
import melnorme.lang.tooling.context.ISemanticContext;
import melnorme.lang.tooling.context.ModuleFullName;
import melnorme.lang.tooling.symbols.INamedElement;
import melnorme.utilbox.misc.Pair;
import mmrnmhrm.core.engine_client.DToolClient_Bad;
import mmrnmhrm.core.model_elements.DeeModelEngine;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.core.IMember;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.core.search.SearchMatch;
import org.junit.Test;

import dtool.ast.definitions.DefUnit;
import dtool.ast.references.Reference;

public class DeeSearchEngine_MassTest extends DeeSearchEngine_Test {
	
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
			protected void visitNode(ASTNode node, ISourceModule sourceModule) {
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
		
		final HashMap<Pair<ISourceModule, INamedElementNode>, HashSet<Reference>> defUnitToReferencesMap 
		= new HashMap<>();
		
		new DeeSearchEngineTestUtils.ElementsAndDefUnitVisitor() {
			@Override
			protected void visitNode(ASTNode node, ISourceModule sourceModule) {
				if(node instanceof Reference) {
					Reference ref = (Reference) node;
					Path filePath = DToolClient_Bad.getFilePathOrNull(sourceModule);
					if(filePath == null) {
						return;
					}
					
					ISemanticContext mr = DToolClient_Bad.getResolverFor(filePath);
					Collection<INamedElement> resolvedElements = ref.getSemantics(mr).findTargetDefElements(false);
					if(resolvedElements == null || resolvedElements.isEmpty()) {
						return;
					}
					
					for (INamedElement resolvedElement : resolvedElements) {
						INamedElementNode defUnit = resolvedElement.resolveUnderlyingNode();
						if(defUnit == null) {
							continue;
						}
						ModuleFullName moduleFullName = resolvedElement.getModuleFullName();
						if(moduleFullName == null) {
							continue; // consider this case more
						}
						
						ISourceModule defUnitSrcModule = findSourceModule(moduleFullName, searchProj);
						
						Pair<ISourceModule, INamedElementNode> key = Pair.create(defUnitSrcModule, defUnit);
						
						if(defUnitToReferencesMap.get(key) == null) {
							defUnitToReferencesMap.put(key, new HashSet<Reference>());
						}
						
						defUnitToReferencesMap.get(key).add(ref);
					}
				}
			}
		}.visitElementsAndNodes(getSrcFolder(searchProj, "srcA"), 10);
		
		
		
		for (Pair<ISourceModule, INamedElementNode> key : defUnitToReferencesMap.keySet()) {
			ISourceModule sourceModule = key.getFirst();
			INamedElement defUnit = key.getSecond();
			
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
						
						String refModuleName = reference.getModuleNode_().getModuleFullyQualifiedName();
						
						IModelElement modelElement = downCast(referenceMatch.getElement());
						ISourceModule matchSrcModule = DeeSearchEngineTestUtils.getSourceModule(modelElement);
						
						String matchModuleName = DeeSearchEngineTestUtils.getSourceModuleFQName(matchSrcModule);
						
						if(areEqual(refModuleName, matchModuleName) &&
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
	
	public static ISourceModule findSourceModule(ModuleFullName moduleFullName, IScriptProject searchProj) {
		try {
			// TODO: test this, consider multiple named source Packages
			return SourceModuleFinder.findModuleUnit(searchProj, moduleFullName);
		} catch (ModelException e) {
			return null;
		}
	}
	
}