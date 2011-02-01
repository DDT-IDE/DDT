package dtool.refmodel;

import java.util.Collection;

import dtool.ast.definitions.DefUnit;

/** A reference to a DefUnit. */
public interface IDefUnitReference {

	/** Finds the DefUnits matching this reference. 
	 * If no results are found, return null. */
	Collection<DefUnit> findTargetDefUnits(boolean findFirstOnly);
	
	String toStringAsElement();
}
