package dtool.ast.expressions;


import java.util.Collection;
import java.util.Collections;

import dtool.ast.definitions.DefUnit;
import dtool.refmodel.IDefUnitReferenceNode;

public abstract class Expression extends Resolvable implements IDefUnitReferenceNode {
	
	// deprecate
	public Collection<DefUnit> getType() {
		return findTargetDefUnits(false);
	}
	
	@Override
	public Collection<DefUnit> findTargetDefUnits(boolean findFirstOnly) {
		return Collections.emptySet();
		/*throw new UnsupportedOperationException(
				"Unsupported peering the type/scope of expression: "+toStringClassName());*/
	}
	
}
