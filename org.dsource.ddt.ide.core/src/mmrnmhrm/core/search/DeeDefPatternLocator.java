package mmrnmhrm.core.search;

import java.util.Collection;
import java.util.Iterator;

import mmrnmhrm.core.codeassist.DeeProjectModuleResolver;

import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.search.matching.PatternLocator;

import dtool.ast.ASTNeoNode;
import dtool.ast.DefUnitDescriptor;
import dtool.ast.definitions.DefUnit;
import dtool.ast.references.CommonRefQualified;
import dtool.ast.references.Reference;

/// XXX: get rid of this class
@Deprecated
public class DeeDefPatternLocator extends AbstractNodePatternMatcher {
	
	/** XXX: DLTK limitation: A global needed to pass parameters for the search.*/
	public static DefUnit GLOBAL_param_defunit;
	
	public final DefUnit defunit;
	protected final DefUnitDescriptor defUnitDescriptor;
	
	public DeeDefPatternLocator(DeeMatchLocator deeMatchLocator) {
		super(deeMatchLocator, false, true);
		this.defunit = GLOBAL_param_defunit;
		defUnitDescriptor = new DefUnitDescriptor(defunit.getName());
	}
	
	@Override
	public boolean match(ASTNeoNode node, ISourceModule sourceModule) {
		if(node instanceof Reference) {
			// don't match qualifieds, the match will be made in its children
			if(node instanceof CommonRefQualified)
				return true;
			
			Reference ref = (Reference) node;
			if(!ref.canMatch(defUnitDescriptor))
				return true;
			
			Collection<DefUnit> defUnits = ref.findTargetDefUnits(new DeeProjectModuleResolver(sourceModule), false);
			if(defUnits == null)
				return true;
			for (Iterator<DefUnit> iter = defUnits.iterator(); iter.hasNext();) {
				DefUnit targetdefunit = iter.next();
				if(defunit.equals(targetdefunit)) {
					deeMatchLocator.addMatch(ref, PatternLocator.ACCURATE_MATCH, sourceModule);
					return true;
				}
			}
		}
		return true;
	}
	
}