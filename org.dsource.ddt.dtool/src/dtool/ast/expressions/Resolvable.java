package dtool.ast.expressions;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.util.Collection;

import dtool.ast.ASTNode;
import dtool.ast.IASTNode;
import dtool.ast.definitions.DefUnit;
import dtool.ast.references.RefQualified;
import dtool.ast.references.Reference;
import dtool.refmodel.IDefUnitReference;
import dtool.refmodel.pluginadapters.IModuleResolver;

/**
 * A {@link Resolvable} is either an {@link Reference} or {@link Expression}
 */
public abstract class Resolvable extends ASTNode implements IDefUnitReference {
	
	/** Marker interface for nodes that can appear as qualifier in {@link RefQualified}. 
	 * Must be a {@link Resolvable}. */
	public interface IQualifierNode extends IDefUnitReference, IASTNode { }
	
	/** Marker interface for nodes that can appear as template references in template instance. 
	 * Must be a {@link Reference}.*/
	public interface ITemplateRefNode extends IASTNode { }
	
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
	
}