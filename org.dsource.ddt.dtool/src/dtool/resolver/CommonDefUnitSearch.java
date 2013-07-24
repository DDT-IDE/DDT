package dtool.resolver;


import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.Module;
import dtool.resolver.api.IModuleResolver;

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
	/** Module Resolver */
	protected final IModuleResolver modResolver;
	/** Cached value of the reference's module scope. */
	protected Module searchRefModule; 
	/** The scopes that have already been searched */
	protected ArrayList<IScope> searchedScopes;


	public CommonDefUnitSearch(IScopeNode refScope, int refOffset, IModuleResolver moduleResolver) {
		this(refScope, refOffset, false, moduleResolver);
	}
	
	public CommonDefUnitSearch(IScopeNode refScope, int refOffset, boolean findOneOnly, 
		IModuleResolver moduleResolver) { 
		this.searchedScopes = new ArrayList<IScope>(4);
		this.refScope = assertNotNull(refScope);
		this.refOffset = refOffset;
		this.findOnlyOne = findOneOnly;
		this.modResolver = assertNotNull(moduleResolver);
		
		this.searchRefModule = assertNotNull(refScope.asNode().getModuleNode());
	}
	
	public IModuleResolver getModResolver() {
		return modResolver;
	}
	
	public String[] resolveModules(String fqNamePrefix) {
		try {
			return modResolver.findModules(fqNamePrefix);
		} catch (Exception e) {
			throw melnorme.utilbox.core.ExceptionAdapter.unchecked(e);
		}
	}
	
	public Module resolveModule(String[] packages, String module) {
		try {
			return modResolver.findModule(packages, module);
		} catch (Exception e) {
			throw melnorme.utilbox.core.ExceptionAdapter.unchecked(e);
		}
	}
	
	/** Return whether we have already search the given scope or not. */
	public boolean hasSearched(IScope scope) {
		// TODO: shit performance here, make it a hash, or sorted search
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
	public Module getSearchReferenceModule() {
		return searchRefModule;
	}
	
	/** Return whether the search has found all matches. */
	public abstract boolean isFinished();

	/** Returns whether this search matches the given defUnit or not. */
	public boolean matches(DefUnit defUnit) {
		if(!defUnit.availableInRegularNamespace()) {
			return false;
		}
		if(defUnit.syntaxIsMissingName()) {
			return false;
		}
		return matchesName(defUnit.getName());
	}
	
	/** Returns whether this search matches the given name or not. */
	public abstract boolean matchesName(String defName);

	
	/** Adds the matched defunit. */
	public abstract void addMatch(DefUnit defUnit);

}