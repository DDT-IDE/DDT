package dtool.refmodel;

import java.util.Collection;

import dtool.ast.definitions.DefUnit;
import dtool.refmodel.api.IModuleResolver;

/** 
 * A reference to a DefUnit.
 * TODO rename to IResolvable? 
 * */
public interface IDefUnitReference {
	
	/** Finds the DefUnits matching this reference. 
	 * If no results are found, return null. */
	Collection<DefUnit> findTargetDefUnits(IModuleResolver moduleResolver, boolean findFirstOnly);
	
	String toStringAsElement();
	
}