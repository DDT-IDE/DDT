package dtool.ast.expressions;

import java.util.Collection;

import dtool.ast.ASTNeoNode;
import dtool.ast.definitions.DefUnit;
import dtool.refmodel.IDefUnitReferenceNode;

public abstract class Resolvable extends ASTNeoNode 
	implements IDefUnitReferenceNode {

	@Override
	public abstract Collection<DefUnit> findTargetDefUnits(boolean findFirstOnly);
	
	public DefUnit findTargetDefUnit() {
		Collection<DefUnit> defunits = findTargetDefUnits(true);
		if(defunits == null || defunits.isEmpty())
			return null;
		return defunits.iterator().next();
	}
	
}