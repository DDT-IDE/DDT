package dtool.ast.references;

import dtool.ast.definitions.DefUnit;
import dtool.refmodel.PrefixDefUnitSearch;

/** 
 * A reference based on an identifier. These references also 
 * allow doing a search based on their lookup rules.
 */
public abstract class NamedReference extends Reference {
 
	/** Perform a search using the lookup rules of this reference. */
	public abstract void doSearch(PrefixDefUnitSearch search);

	
	/** Return wheter this reference can match the given defunit.
	 * This is a very lightweight method that only compares the defunit's 
	 * name with the identifier of this reference, if any.
	 * XXX: Qualified refs, how should they compare? 
	 */
	@Override
	public final boolean canMatch(DefUnit defunit) {
		return getReferenceName().equals(defunit.getName());
	}

	protected String getReferenceName() {
		return toStringAsElement();
	}

}
