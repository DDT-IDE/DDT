package dtool.ast.expressions;

import java.util.Collection;

import dtool.ast.ASTNeoNode;
import dtool.ast.definitions.DefUnit;
import dtool.refmodel.IDefUnitReferenceNode;
import dtool.refmodel.ReferenceResolver;
import dtool.refmodel.pluginadapters.IModuleResolver;

public abstract class Resolvable extends ASTNeoNode implements IDefUnitReferenceNode {
	
	// eventual BUG here whenever this function is used
	@Deprecated
	public final Collection<DefUnit> findTargetDefUnits(boolean findFirstOnly) {
		return findTargetDefUnits(ReferenceResolver.getModResolver(), findFirstOnly);
	}
	
	// eventual BUG here whenever this function is used
	@Deprecated
	public DefUnit findTargetDefUnit() {
		return findTargetDefUnit(ReferenceResolver.getModResolver());
	}
	
	@Override
	public abstract Collection<DefUnit> findTargetDefUnits(IModuleResolver moduleResolver, boolean findFirstOnly);
	
	public DefUnit findTargetDefUnit(IModuleResolver moduleResolver) {
		Collection<DefUnit> defunits = findTargetDefUnits(moduleResolver, true);
		if(defunits == null || defunits.isEmpty())
			return null;
		return defunits.iterator().next();
	}
	
}