package dtool.resolver;

import java.util.ArrayList;

import dtool.ast.definitions.DefUnit;
import dtool.contentassist.CompletionSession;
import dtool.refmodel.PrefixSearchOptions;
import dtool.refmodel.PrefixDefUnitSearch.IDefUnitMatchAccepter;

public class CompletionCollectorSession extends CompletionSession implements IDefUnitMatchAccepter {
	
	public final ArrayList<DefUnit> results;
	
	public CompletionCollectorSession() {
		this.results = new ArrayList<DefUnit>();
	}
	
	@Override
	public void accept(DefUnit defUnit, PrefixSearchOptions searchOptions) {
		results.add(defUnit);
	}
	
}