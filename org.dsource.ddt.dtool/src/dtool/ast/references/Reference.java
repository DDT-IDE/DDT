package dtool.ast.references;

import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.NativeDefUnit;
import dtool.ast.expressions.Resolvable;
import dtool.resolver.IDefUnitReference;
import dtool.resolver.IScopeNode;
import dtool.resolver.api.DefUnitDescriptor;
import dtool.resolver.api.IModuleResolver;

/**
 * Common class for entity references.
 */
public abstract class Reference extends Resolvable {
	
	public static IDefUnitReference maybeNullReference(Reference ref) {
		if(ref != null)
			return ref;
		return NativeDefUnit.nullReference;
	}
	
	public IScopeNode getTargetScope(IModuleResolver moduleResolver) {
		DefUnit defunit = findTargetDefUnit(moduleResolver); 
		if(defunit == null)
			return null;
		return defunit.getMembersScope(moduleResolver);
	}
	
	/*public void performSearch(CommonDefUnitSearch search) {
		Collection<DefUnit> defunits = findLookupDefUnits();
		ANeoResolve.doSearchInDefUnits(defunits, search);
	}
	
	public abstract Collection<DefUnit> findLookupDefUnits();
	 */
	
	public abstract boolean canMatch(DefUnitDescriptor defunit);
	
	// How to handle composite references though? (like RefQualified)
	public boolean syntaxIsMissingIdentifier() {
		return false; // TODO /*BUG here for children*/
	}
	
}