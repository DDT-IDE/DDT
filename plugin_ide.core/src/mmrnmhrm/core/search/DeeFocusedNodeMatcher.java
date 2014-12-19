package mmrnmhrm.core.search;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Iterator;

import melnorme.lang.tooling.ast_actual.ASTNode;
import melnorme.lang.tooling.context.ISemanticContext;
import melnorme.lang.tooling.symbols.INamedElement;
import mmrnmhrm.core.DeeCore;
import mmrnmhrm.core.engine_client.DToolClient_Bad;
import mmrnmhrm.core.model_elements.DeeModelEngine;

import org.eclipse.dltk.core.IMember;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.core.search.matching.PatternLocator;

import dtool.ast.definitions.DefUnit;
import dtool.ast.references.CommonQualifiedReference;
import dtool.ast.references.NamedReference;

public class DeeFocusedNodeMatcher extends AbstractNodePatternMatcher {
	
	protected final IModelElement modelElement;
	
	public DeeFocusedNodeMatcher(DeeMatchLocator deeMatchLocator, IModelElement focus, boolean findDecls,
			boolean findRefs) {
		super(deeMatchLocator, findDecls, findRefs);
		this.modelElement = focus;
	}
	
	@Override
	public boolean match(ASTNode node, ISourceModule sourceModule, Path filePath) {
		if(matchDeclarations && node instanceof DefUnit) {
			DefUnit definition = (DefUnit) node;
			matchDefUnit(definition, sourceModule, filePath);
		}
		
		if(matchReferences && node instanceof NamedReference) {
			NamedReference ref = (NamedReference) node;
			matchReferences(ref, sourceModule, filePath);
		}
		return true;
	}
	
	protected void matchReferences(final NamedReference ref, final ISourceModule sourceModule, Path filePath) {
		// don't match qualifieds, the match will be made in its children
		if(ref instanceof CommonQualifiedReference)
			return;
		
		if(!ref.canMatch(modelElement.getElementName()))
			return;
		
		ISemanticContext moduleResolver = DToolClient_Bad.getResolverFor(filePath);
		Collection<INamedElement> defUnits = ref.findTargetDefElements(moduleResolver);
		if(defUnits == null)
			return;
		
		for (Iterator<INamedElement> iter = defUnits.iterator(); iter.hasNext();) {
			INamedElement targetNamedElement = iter.next();
			
			try {
				IMember targetModelElement = DeeModelEngine.findCorrespondingModelElement(targetNamedElement, 
					sourceModule.getScriptProject());

				if(targetModelElement != null && modelElement.equals(targetModelElement)) {
					deeMatchLocator.addMatch(ref, PatternLocator.ACCURATE_MATCH, sourceModule);
					return;
				}
				
			} catch (ModelException e) {
				continue;
			}
		}
	}
	
	protected void matchDefUnit(DefUnit definition, ISourceModule sourceModule, Path filePath) {
		try {
			IMember targetModelElement = DeeModelEngine.findCorrespondingModelElement(definition, sourceModule);
			
			if(modelElement.equals(targetModelElement)) {
				deeMatchLocator.addMatch(definition, PatternLocator.ACCURATE_MATCH, sourceModule);
			}
		} catch (ModelException e) {
			DeeCore.logStatus(e);
			return;
		}
		
	}
	
}