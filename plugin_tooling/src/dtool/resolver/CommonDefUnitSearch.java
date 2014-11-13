package dtool.resolver;


import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;

import java.util.ArrayList;
import java.util.Set;

import melnorme.lang.tooling.ast_actual.ILangNamedElement;
import melnorme.lang.tooling.bundles.IModuleResolver;
import melnorme.lang.tooling.engine.scoping.IScopeProvider;
import melnorme.lang.tooling.engine.scoping.NamedElementsVisitor;
import melnorme.utilbox.core.fntypes.Function;
import melnorme.utilbox.misc.StringUtil;
import dtool.ast.definitions.Module;
import dtool.ast.util.NamedElementUtil;

public abstract class CommonDefUnitSearch extends NamedElementsVisitor {
	
	/** Flag for stop searching when suitable matches are found. */
	protected final boolean findOnlyOne;
	/** The offset of the reference. 
	 * Used to check availability in statement scopes. */
	protected final int refOffset;
	/** Module Resolver */
	protected final IModuleResolver modResolver;
	/** The module where the search started. */
	protected final Module refOriginModule;
	
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
	
	public boolean isSequentialLookup() {
		return refOffset >= 0;
	}
	
	public Set<String> findModulesWithPrefix(String fqNamePrefix) {
		return modResolver.findModules(fqNamePrefix);
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

	@Override
	public String toString() {
		return getClass().getName() + " ---\n" + toString_matches();
	}
	
	public String toString_matches() {
		return StringUtil.iterToString(matches, "\n", new Function<ILangNamedElement, String>() {
			@Override
			public String evaluate(ILangNamedElement obj) {
				return NamedElementUtil.getElementTypedQualification(obj); 
			}
		});
	}
	
}