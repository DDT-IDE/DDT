package dtool.ast.expressions;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.util.Collection;

import dtool.ast.ASTNeoNode;
import dtool.ast.definitions.DefUnit;
import dtool.ast.references.CommonRefQualified.IQualifierNode;
import dtool.ast.references.Reference;
import dtool.refmodel.IDefUnitReference;
import dtool.refmodel.pluginadapters.IModuleResolver;

/**
 * A {@link Resolvable} is either an {@link Reference} or {@link Expression}
 */
public abstract class Resolvable extends ASTNeoNode implements IDefUnitReference, IQualifierNode {
	
	public Resolvable() {
		assertTrue(this instanceof Reference || this instanceof Expression);
	}
	
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