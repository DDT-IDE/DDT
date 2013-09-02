package mmrnmhrm.core.search;

import java.util.Collection;
import java.util.Iterator;

import mmrnmhrm.core.DeeCore;
import mmrnmhrm.core.codeassist.DeeProjectModuleResolver;
import mmrnmhrm.core.model_elements.DeeModelEngine;

import org.eclipse.dltk.core.IMember;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.core.search.matching.PatternLocator;

import dtool.ast.ASTNode;
import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.INamedElement;
import dtool.ast.references.CommonRefQualified;
import dtool.ast.references.NamedReference;
import dtool.resolver.api.DefUnitDescriptor;

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
	public boolean match(ASTNode node, ISourceModule sourceModule) {
		
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
		Collection<INamedElement> defUnits = ref.findTargetDefElements(moduleResolver, false);
		if(defUnits == null)
			return;
		
		for (Iterator<INamedElement> iter = defUnits.iterator(); iter.hasNext();) {
			INamedElement targetDefElement = iter.next();
			DefUnit targetDefUnit = targetDefElement.resolveDefUnit();
			
			try {
				IMember targetModelElement = DeeModelEngine.findCorrespondingModelElement(targetDefUnit, 
					moduleResolver);

				if(targetModelElement != null && modelElement.equals(targetModelElement)) {
					deeMatchLocator.addMatch(ref, PatternLocator.ACCURATE_MATCH, sourceModule);
					return;
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