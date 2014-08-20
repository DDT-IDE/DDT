package mmrnmhrm.core.search;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Iterator;

import mmrnmhrm.core.engine_client.DToolClient_Bad;

import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.search.matching.PatternLocator;

import dtool.ast.ASTNode;
import dtool.ast.definitions.INamedElement;
import dtool.ast.references.CommonRefQualified;
import dtool.ast.references.NamedReference;
import dtool.engine.modules.IModuleResolver;

/// XXX: get rid of this class
@Deprecated
public class DeeDefPatternLocator extends AbstractNodePatternMatcher {
	
	/** XXX: DLTK limitation: A global needed to pass parameters for the search.*/
	public static INamedElement GLOBAL_param_defunit;
	
	public final INamedElement defunit;
	
	public DeeDefPatternLocator(DeeMatchLocator deeMatchLocator) {
		super(deeMatchLocator, false, true);
		this.defunit = GLOBAL_param_defunit;
	}
	
	@Override
	public boolean match(ASTNode node, ISourceModule sourceModule, Path filePath) {
		if(node instanceof NamedReference) {
			NamedReference ref = (NamedReference) node;
			
			// don't match qualifieds, the match will be made in its children
			if(node instanceof CommonRefQualified)
				return true;
			
			if(!ref.canMatch(defunit.getName()))
				return true;
			
			IModuleResolver mr = DToolClient_Bad.getResolverFor(filePath);
			Collection<INamedElement> defUnits = ref.findTargetDefElements(mr, false);
			if(defUnits == null)
				return true;
			for (Iterator<INamedElement> iter = defUnits.iterator(); iter.hasNext();) {
				INamedElement targetdefunit = iter.next();
				if(defunit.equals(targetdefunit)) {
					deeMatchLocator.addMatch(ref, PatternLocator.ACCURATE_MATCH, sourceModule);
					return true;
				}
			}
		}
		return true;
	}
	
}