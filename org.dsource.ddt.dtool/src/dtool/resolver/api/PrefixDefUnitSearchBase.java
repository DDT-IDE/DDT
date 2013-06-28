package dtool.resolver.api;


import dtool.contentassist.CompletionSession;
import dtool.resolver.CommonDefUnitSearch;
import dtool.resolver.IScopeNode;
import dtool.resolver.PrefixDefUnitSearch;

// TODO: cleanup this hierarchy
public abstract class PrefixDefUnitSearchBase extends CommonDefUnitSearch {
	
	public final PrefixSearchOptions searchOptions;
	
	public PrefixDefUnitSearchBase(IScopeNode refScope, int refOffset, IModuleResolver moduleResolver,
		PrefixSearchOptions searchOptions) {
		super(refScope, refOffset, moduleResolver);
		this.searchOptions = searchOptions;
	}
	
	public static PrefixDefUnitSearchBase runCompletionSearch(CompletionSession session, String defaultModuleName,
		String source, int offset, IModuleResolver mr, IDefUnitMatchAccepter defUnitAccepter) {
		return PrefixDefUnitSearch.doCompletionSearch(session, defaultModuleName, source, offset, mr, 
			defUnitAccepter);
	}
	
}