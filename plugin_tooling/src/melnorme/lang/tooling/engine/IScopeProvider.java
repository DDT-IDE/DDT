package melnorme.lang.tooling.engine;

import dtool.resolver.CommonDefUnitSearch;


/**
 * Simple scope provider interface
 * This class/interface is used as key to prevent the same scope to be resolved more than once
 * TODO: need to define a more precise behavior regarding the above
 */
public interface IScopeProvider {
	
	/** Resolve given reference search in this scope. */
	void resolveSearchInScope(CommonDefUnitSearch search);
	
}