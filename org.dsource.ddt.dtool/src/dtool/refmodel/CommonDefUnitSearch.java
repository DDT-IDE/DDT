package dtool.refmodel;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import dtool.ast.definitions.DefUnit;

public abstract class CommonDefUnitSearch {

	public static final Collection<DefUnit> NO_DEFUNITS = Collections.emptySet();

	/** Convenience method for wraping a single defunit as a search result. */
	public static Collection<DefUnit> wrapResult(DefUnit defunit) {
		if(defunit == null)
			return null;
		return Collections.singletonList(defunit);
	}
	
	/** Flag for stop searching when suitable matches are found. */
	protected final boolean findOnlyOne;
	/** The scope where the reference is located. 
	 * Used for protection access restrictions. */
	protected final IScopeNode refScope;
	/** The offset of the reference. 
	 * Used to check availability in statement scopes. */
	protected final int refOffset;
	/** Cached value of the reference's module scope. */
	protected IScope refModuleScope; 
	/** The scopes that have already been searched */
	protected ArrayList<IScope> searchedScopes;


	public CommonDefUnitSearch(IScopeNode refScope, int refOffset) {
		this(refScope, refOffset, false);
	}
	
	public CommonDefUnitSearch(IScopeNode refScope, int refOffset, boolean findOneOnly) { 
		this.searchedScopes = new ArrayList<IScope>(4);
		this.refScope = refScope;
		this.refOffset = refOffset;
		this.findOnlyOne = findOneOnly;
	}
	

	/** Return whether we have already search the given scope or not. */
	public boolean hasSearched(IScope scope) {
		if(searchedScopes.contains(scope))
			return true;
		return false;
	}

	/** Indicate we are now searching the given new scope. */
	public void enterNewScope(IScope scope) {
		// TODO: keep only the named scopes?
		// how about partial scopes?
		searchedScopes.add(scope);
	}
	
	/** Get the Module of the search's reference. */
	public IScope getReferenceModuleScope() {
		if(refModuleScope == null)
			refModuleScope = refScope.getModuleScope();
		return refModuleScope;
	}
	
	/** Return whether the search has found all matches. */
	public abstract boolean isFinished();

	/** Returns whether this search matches the given defUnit or not. */
	// DEPRECATE?
	public abstract boolean matches(DefUnit defUnit);

	/** Returns whether this search matches the given name or not. */
	public abstract boolean matchesName(String defName);

	
	/** Adds the matched defunit. */
	public abstract void addMatch(DefUnit defUnit);


}