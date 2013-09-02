package dtool.resolver;

import java.util.Collection;

import dtool.ast.definitions.INamedElement;
import dtool.resolver.api.IModuleResolver;

/** 
 * A reference to a DefUnit.
 * TODO rename to IResolvable? 
 * */
public interface IDefUnitReference {
	
	/** Finds the DefUnits matching this reference. 
	 * If no results are found, return null. */
	Collection<INamedElement> findTargetDefElements(IModuleResolver moduleResolver, boolean findFirstOnly);
	
}