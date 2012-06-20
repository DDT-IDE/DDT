package mmrnmhrm.core.search;

import java.util.Collection;
import java.util.Iterator;

import mmrnmhrm.core.DeeCore;
import mmrnmhrm.core.codeassist.DeeProjectModuleResolver;

import org.dsource.ddt.ide.core.model.engine.DeeModelEngine;
import org.eclipse.dltk.core.IMember;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.core.search.matching.PatternLocator;

import dtool.ast.ASTNeoNode;
import dtool.ast.DefUnitDescriptor;
import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.Module;
import dtool.ast.references.CommonRefQualified;
import dtool.ast.references.NamedReference;

public class DeeFocusedNodeMatcher extends AbstractNodePatternMatcher {
	
	protected final IModelElement modelElement;
	protected final DefUnitDescriptor defUnitDescriptor;
	
	public DeeFocusedNodeMatcher(DeeMatchLocator deeMatchLocator, IModelElement focus, boolean findDecls,
			boolean findRefs) {
		super(deeMatchLocator, findDecls, findRefs);
		this.modelElement = focus;
		this.defUnitDescriptor = new DefUnitDescriptor(modelElement.getElementName(), null);
	}
	
	@Override
	public boolean match(ASTNeoNode node, ISourceModule sourceModule) {
		
		if(matchDeclarations && node instanceof DefUnit) {
			DefUnit definition = (DefUnit) node;
			matchDefUnit(definition, sourceModule);
		}
		
		if(matchReferences && node instanceof NamedReference) {
			NamedReference ref = (NamedReference) node;
			matchReferences(ref, sourceModule);
		}
		return true;
	}
	
	protected void matchReferences(final NamedReference ref, final ISourceModule sourceModule) {
		// don't match qualifieds, the match will be made in its children
		if(ref instanceof CommonRefQualified)
			return;
		
		if(!ref.canMatch(defUnitDescriptor))
			return;
		
		DeeProjectModuleResolver moduleResolver = new DeeProjectModuleResolver(sourceModule);
		Collection<DefUnit> defUnits = ref.findTargetDefUnits(moduleResolver, false);
		if(defUnits == null)
			return;
		
		for (Iterator<DefUnit> iter = defUnits.iterator(); iter.hasNext();) {
			DefUnit targetDefUnit = iter.next();
			
			try {
				Module module = targetDefUnit.getModuleNode();
				// TODO: would be nice to have test for module == null path
				if(module != null) {
					ISourceModule targetSrcModule = moduleResolver.findModuleUnit(module); 
					// TODO: would be nice to have test for module == null path
					// TODO consider out of buildpath scenario
					if(targetSrcModule != null) {
						IMember targetModelElement = 
								DeeModelEngine.findCorrespondingModelElement(targetDefUnit, targetSrcModule);
						
						if(modelElement.equals(targetModelElement)) {
							deeMatchLocator.addMatch(ref, PatternLocator.ACCURATE_MATCH, sourceModule);
							return;
						}
					}
				}
				
			} catch (ModelException e) {
				continue;
			}
		}
	}
	
	protected void matchDefUnit(DefUnit definition, ISourceModule sourceModule) {
		try {
			IMember targetModelElement = DeeModelEngine.findCorrespondingModelElement(definition, sourceModule);
			
			if(modelElement.equals(targetModelElement)) {
				deeMatchLocator.addMatch(definition, PatternLocator.ACCURATE_MATCH, sourceModule);
			}
		} catch (ModelException e) {
			DeeCore.log(e);
			return;
		}
		
	}
	
}