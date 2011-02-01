package mmrnmhrm.core.dltk.search;

import java.util.Collection;
import java.util.Iterator;

import org.eclipse.dltk.ast.ASTNode;
import org.eclipse.dltk.core.search.SearchPattern;
import org.eclipse.dltk.core.search.matching.PatternLocator;
import org.eclipse.dltk.internal.core.search.matching.MatchingNodeSet;

import dtool.ast.definitions.DefUnit;
import dtool.ast.references.CommonRefQualified;
import dtool.ast.references.Reference;

public class DeeDefPatternLocator extends PatternLocator {
	
	/** XXX: DLTK limitation: A global needed to pass parameters for 
	 * the search.*/
	public static DefUnit GLOBAL_param_defunit;
	
	public DefUnit defunit;

	public DeeDefPatternLocator(DefUnit defunit, SearchPattern pattern) {
		super(pattern);
		this.defunit = defunit;
	}
	
	@Override
	public int match(ASTNode node, MatchingNodeSet nodeSet) {
		if(node instanceof Reference) {
			// don't match qualifieds, the match will be made in its children
			if(node instanceof CommonRefQualified)
				return IMPOSSIBLE_MATCH;
			
			Reference ref = (Reference) node;
			if(!ref.canMatch(defunit))
				return IMPOSSIBLE_MATCH;
			
			Collection<DefUnit> defUnits = ref.findTargetDefUnits(false);
			if(defUnits == null)
				return IMPOSSIBLE_MATCH;
			for (Iterator<DefUnit> iter = defUnits.iterator(); iter.hasNext();) {
				DefUnit targetdefunit = iter.next();
				if(defunit.equals(targetdefunit))
					return nodeSet.addMatch(ref, ACCURATE_MATCH);
			}
		}
		return IMPOSSIBLE_MATCH;
	}


}
