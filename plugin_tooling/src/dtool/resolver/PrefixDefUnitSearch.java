package dtool.resolver;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import melnorme.utilbox.core.fntypes.Function;
import melnorme.utilbox.misc.StringUtil;
import dtool.ast.definitions.INamedElement;
import dtool.ast.definitions.Module;
import dtool.ast.util.NamedElementUtil;
import dtool.engine.modules.IModuleResolver;
import dtool.engine.operations.CompletionSearchResult.PrefixSearchOptions;

/** 
 * Class that does a scoped name lookup for matches that start with a given prefix name. 
 * TODO: The matches with the same name as matches in a scope with higher 
 * priority are not added.
 */
public class PrefixDefUnitSearch extends CommonDefUnitSearch {
	
	public final PrefixSearchOptions searchOptions;
	protected final Set<String> addedDefElements = new HashSet<>();
	protected final ArrayList<INamedElement> results  = new ArrayList<>();
	
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
		results.add(namedElem);
	}
	
	public ArrayList<INamedElement> getResults() {
		return results;
	}
	
	@Override
	public String toString() {
		String str = super.toString();
		str += "searchPrefix: " + searchOptions.searchPrefix +"\n";
		str += "----- Results: -----\n";
		str += StringUtil.iterToString(results, "\n", new Function<INamedElement, String>() {
			@Override
			public String evaluate(INamedElement obj) {
				return NamedElementUtil.getElementTypedQualification(obj); 
			}
		});
		return str;
	}
}