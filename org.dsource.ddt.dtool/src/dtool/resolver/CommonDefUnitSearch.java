package dtool.resolver;


import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.INamedElement;
import dtool.ast.definitions.Module;
import dtool.engine.modules.IModuleResolver;

public abstract class CommonDefUnitSearch {

	public static final Collection<DefUnit> NO_DEFUNITS = Collections.emptySet();

	/** Flag for stop searching when suitable matches are found. */
	protected final boolean findOnlyOne;
	/** The offset of the reference. 
	 * Used to check availability in statement scopes. */
	protected final int refOffset;
	/** Module Resolver */
	protected final IModuleResolver modResolver;
	/** The module where the search started. */
	protected Module refOriginModule;
	/** The scopes that have already been searched */
	protected ArrayList<IScopeProvider> searchedScopes;
	
	
	public CommonDefUnitSearch(Module refOriginModule, int refOffset, IModuleResolver moduleResolver) {
		this(refOriginModule, refOffset, false, moduleResolver);
	}
	
	public CommonDefUnitSearch(Module refOriginModule, int refOffset, boolean findOneOnly, 
		IModuleResolver moduleResolver) { 
		this.searchedScopes = new ArrayList<>(4);
		this.refOffset = refOffset;
		this.findOnlyOne = findOneOnly;
		this.modResolver = assertNotNull(moduleResolver);
		this.refOriginModule = refOriginModule;
	}
	
	public IModuleResolver getModuleResolver() {
		return modResolver;
	}
	
	public Set<String> findModulesWithPrefix(String fqNamePrefix) {
		return modResolver.findModules(fqNamePrefix);
	}
	
	public Module resolveModule(String[] packages, String module) {
		return ReferenceResolver.findModuleUnchecked(modResolver, packages, module);
	}
	
	/** Return whether we have already search the given scope or not. */
	public boolean hasSearched(IScopeProvider scope) {
		// TODO: shit performance here, make it a hash, or sorted search
		if(searchedScopes.contains(scope))
			return true;
		return false;
	}

	/** Indicate we are now searching the given new scope. */
	public void enterNewScope(IScopeProvider scope) {
		// TODO: keep only the named scopes?
		// how about partial scopes?
		searchedScopes.add(scope);
	}
	
	/** @return the Module of the node or position where this search originates. */
	public Module getSearchOriginModule() {
		return refOriginModule;
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
	
	// TODO: subside into #matches(DefUnit)
	public boolean matches(INamedElement defElement) {
		return matchesName(defElement.getName());
	}
	
	/** Returns whether this search matches the given name or not. */
	public abstract boolean matchesName(String defName);
	
	
	/** Adds the matched defunit. */
	public abstract void addMatch(INamedElement defElement);
	
	@Override
	public String toString() {
		return getClass().getName() + " ---\n";
	}
	
}