package mmrnmhrm.core.search;

import java.util.Collection;
import java.util.Iterator;

import mmrnmhrm.core.codeassist.DeeProjectModuleResolver;

import org.dsource.ddt.ide.core.model.engine.DeeModelEngine;
import org.eclipse.dltk.core.IMember;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.core.search.matching.PatternLocator;

import dtool.ast.ASTNeoNode;
import dtool.ast.DefUnitDescriptor;
import dtool.ast.definitions.DefUnit;
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
	
	protected void matchReferences(NamedReference ref, ISourceModule sourceModule) {
		// don't match qualifieds, the match will be made in its children
		if(ref instanceof CommonRefQualified)
			return;
		
		if(!ref.canMatch(defUnitDescriptor))
			return;
		
		Collection<DefUnit> defUnits = ref.findTargetDefUnits(new DeeProjectModuleResolver(sourceModule), false);
		if(defUnits == null)
			return;
		
		for (Iterator<DefUnit> iter = defUnits.iterator(); iter.hasNext();) {
			DefUnit targetdefunit = iter.next();
			
			IScriptProject scriptProject = sourceModule.getScriptProject();
			ISourceModule targetSrcModule = DeeModelEngine.getSourceModule(targetdefunit, scriptProject);
			
			IMember targetModelElement = findCorrespondingModelElement(targetdefunit, targetSrcModule);
			
			if(modelElement.equals(targetModelElement)) {
				deeMatchLocator.addMatch(ref, PatternLocator.ACCURATE_MATCH, sourceModule);
				return;
			}
		}
	}
	
	protected void matchDefUnit(DefUnit definition, ISourceModule sourceModule) {
		IMember targetModelElement = findCorrespondingModelElement(definition, sourceModule);
		
		if(modelElement.equals(targetModelElement)) {
			deeMatchLocator.addMatch(definition, PatternLocator.ACCURATE_MATCH, sourceModule);
		}
	}
	
	protected IMember findCorrespondingModelElement(DefUnit definition, ISourceModule sourceModule){
		try {
			return DeeModelEngine.findCorrespondingModelElement(definition, sourceModule);
		} catch (ModelException e) {
			return null; // Hum, think about this exception more.
		}
	}
	
}