package dtool.ast.expressions;

import java.util.Collection;

import dtool.ast.ASTNeoNode;
import dtool.ast.definitions.DefUnit;
import dtool.refmodel.IDefUnitReferenceNode;
import dtool.refmodel.pluginadapters.IModuleResolver;

public abstract class Resolvable extends ASTNeoNode implements IDefUnitReferenceNode {
	
	@Override
	public abstract Collection<DefUnit> findTargetDefUnits(IModuleResolver moduleResolver, boolean findFirstOnly);
	
	public DefUnit findTargetDefUnit(IModuleResolver moduleResolver) {
		Collection<DefUnit> defunits = findTargetDefUnits(moduleResolver, true);
		if(defunits == null || defunits.isEmpty())
			return null;
		return defunits.iterator().next();
	}
	
	/** Returns a simple representation of this node, element-like and for for a line. 
	 * TODO: need to fix this API */
	@Override
	public String toStringAsElement() {
		return toStringAsCode();
	}
	
}