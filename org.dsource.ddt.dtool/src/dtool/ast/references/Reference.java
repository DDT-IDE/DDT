package dtool.ast.references;

import java.util.Collection;

import dtool.ast.definitions.INamedElement;
import dtool.ast.expressions.Resolvable;
import dtool.resolver.CommonDefUnitSearch;
import dtool.resolver.IDefUnitReference;
import dtool.resolver.api.DefUnitDescriptor;
import dtool.resolver.api.IModuleResolver;

/**
 * Common class for entity references.
 */
public abstract class Reference extends Resolvable {
	
	public static void resolveSearchInReferedMembersScope(CommonDefUnitSearch search, IDefUnitReference reference) {
		if(reference == null) {
			return;
		}
		
		IModuleResolver moduleResolver = search.getModuleResolver();
		Collection<INamedElement> defunits = reference.findTargetDefElements(moduleResolver, true);
		if(defunits == null || defunits.isEmpty())
			return;
		// if several defUnits found, search in first only
		INamedElement resolvedType = defunits.iterator().next();
		resolvedType.resolveSearchInMembersScope(search);
	}
	
	public abstract boolean canMatch(DefUnitDescriptor defunit);
	
}