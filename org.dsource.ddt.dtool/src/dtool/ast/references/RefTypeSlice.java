package dtool.ast.references;

import java.util.Collection;

import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.DefUnitDescriptor;
import dtool.ast.IASTVisitor;
import dtool.ast.definitions.DefUnit;
import dtool.ast.expressions.Resolvable;
import dtool.refmodel.pluginadapters.IModuleResolver;

public class RefTypeSlice extends Reference {
	
	public final Resolvable slicee;
	public final Resolvable from;
	public final Resolvable to;
	
	public RefTypeSlice(Resolvable slicee, Resolvable from, Resolvable to) {
		this.slicee = parentizeI(slicee);
		this.from = parentize(from);
		this.to = parentize(to);
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, slicee);
			TreeVisitor.acceptChildren(visitor, from);
			TreeVisitor.acceptChildren(visitor, to);
		}
		visitor.endVisit(this);
	}
	
	
	@Override
	public Collection<DefUnit> findTargetDefUnits(IModuleResolver moduleResolver, boolean findFirstOnly) {
		// TODO:
		return null;
	}
	
	
	@Override
	public String toStringAsElement() {
		return slicee.toStringAsElement() +"["+from.toStringAsElement() +".."+ to.toStringAsElement()+"]";
	}
	
	
	@Override
	public boolean canMatch(DefUnitDescriptor defunit) {
		return false;
	}
	
}