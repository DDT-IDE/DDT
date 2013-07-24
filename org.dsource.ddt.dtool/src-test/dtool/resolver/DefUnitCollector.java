package dtool.resolver;

import java.util.ArrayList;

import dtool.ast.definitions.DefUnit;
import dtool.resolver.api.IDefUnitMatchAccepter;
import dtool.resolver.api.PrefixSearchOptions;

public class DefUnitCollector implements IDefUnitMatchAccepter {
	
	public final ArrayList<DefUnit> results;
	
	public DefUnitCollector() {
		this.results = new ArrayList<DefUnit>();
	}
	
	@Override
	public void accept(DefUnit defUnit, PrefixSearchOptions searchOptions) {
		results.add(defUnit);
	}
	
}