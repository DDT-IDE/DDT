package dtool.resolver;

import java.util.HashSet;
import java.util.Set;

import melnorme.lang.tooling.bundles.IModuleResolver;
import melnorme.lang.tooling.symbols.INamedElement;
import dtool.ast.definitions.Module;
import dtool.engine.operations.CompletionSearchResult.PrefixSearchOptions;

/** 
 * Class that does a scoped name lookup for matches that start with a given prefix name. 
 * TODO: The matches with the same name as matches in a scope with higher 
 * priority are not added.
 */
public class PrefixDefUnitSearch extends CommonDefUnitSearch {
	
	public final PrefixSearchOptions searchOptions;
	protected final Set<String> addedDefElements = new HashSet<>();
	
	public PrefixDefUnitSearch(Module refOriginModule, int refOffset, IModuleResolver moduleResolver) {
		this(refOriginModule, refOffset, moduleResolver, new PrefixSearchOptions());
	}
	
	public PrefixDefUnitSearch(Module refOriginModule, int refOffset, IModuleResolver moduleResolver, 
			PrefixSearchOptions searchOptions) {
		super(refOriginModule, refOffset, moduleResolver);
		this.searchOptions = searchOptions;
	}
	
	public int getOffset() {
		return refOffset;
	}
	
	@Override
	public boolean matchesName(String defName) {
		return defName.startsWith(searchOptions.searchPrefix);
	}
	
	@Override
	public boolean isFinished() {
		return false;
	}
	
	@Override
	public void addMatch(INamedElement namedElem) {
		String extendedName = namedElem.getExtendedName();
		
		if(addedDefElements.contains(extendedName)) {
			return;
		}
		addedDefElements.add(extendedName);
		addMatchDirectly(namedElem);
	}
	
	public void addMatchDirectly(INamedElement namedElem) {
		super.addMatch(namedElem);
	}
	
	@Override
	public String toString() {
		String str = getClass().getName() + " ---\n";
		str += "searchPrefix: " + searchOptions.searchPrefix +"\n";
		str += "----- Results: -----\n";
		str += toString_matches();
		return str;
	}
	
}