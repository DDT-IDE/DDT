package dtool.ast.references;

import java.util.Collection;

import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.NativeDefUnit;
import dtool.ast.expressions.Resolvable;
import dtool.resolver.CommonDefUnitSearch;
import dtool.resolver.IDefUnitReference;
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
	
	public static void resolveSearchInReferedMembersScope(CommonDefUnitSearch search, IDefUnitReference reference) {
		if(reference == null) {
			return;
		}
		
		IModuleResolver moduleResolver = search.getModuleResolver();
		Collection<DefUnit> defunits = reference.findTargetDefUnits(moduleResolver, true);
		if(defunits == null || defunits.isEmpty())
			return;
		// if several defUnits found, search in first only
		DefUnit resolvedType = defunits.iterator().next();
		resolvedType.resolveSearchInMembersScope(search);
	}
	
	public abstract boolean canMatch(DefUnitDescriptor defunit);
	
}