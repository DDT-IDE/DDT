package dtool.ast.references;

import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.NativeDefUnit;
import dtool.ast.expressions.Resolvable;
import dtool.refmodel.DefUnitDescriptor;
import dtool.refmodel.IDefUnitReference;
import dtool.refmodel.IScopeNode;
import dtool.refmodel.pluginadapters.IModuleResolver;

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
	
}