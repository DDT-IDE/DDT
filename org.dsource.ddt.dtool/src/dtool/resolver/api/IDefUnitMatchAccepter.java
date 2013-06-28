package dtool.resolver.api;

import dtool.ast.definitions.DefUnit;

public interface IDefUnitMatchAccepter {
	
	void accept(DefUnit defUnit, PrefixSearchOptions searchOptions);
	
}