package dtool.resolver.api;


import dtool.resolver.CommonDefUnitSearch;
import dtool.resolver.IScopeNode;

// TODO: cleanup this hierarchy
public abstract class PrefixDefUnitSearchBase extends CommonDefUnitSearch {
	
	public final PrefixSearchOptions searchOptions;
	
	public PrefixDefUnitSearchBase(IScopeNode refScope, int refOffset, IModuleResolver moduleResolver,
		PrefixSearchOptions searchOptions) {
		super(refScope, refOffset, moduleResolver);
		this.searchOptions = searchOptions;
	}
	
}