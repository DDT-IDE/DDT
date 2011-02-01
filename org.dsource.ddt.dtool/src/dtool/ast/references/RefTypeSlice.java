package dtool.ast.references;

import java.util.Collection;

import melnorme.utilbox.tree.TreeVisitor;
import descent.internal.compiler.parser.TypeSlice;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.definitions.DefUnit;
import dtool.ast.expressions.Expression;
import dtool.ast.expressions.Resolvable;
import dtool.descentadapter.DescentASTConverter.ASTConversionContext;
import dtool.refmodel.IDefUnitReferenceNode;

public class RefTypeSlice extends Reference {

	public IDefUnitReferenceNode slicee;
	public Resolvable from;
	public Resolvable to;
	
	public RefTypeSlice(TypeSlice elem, ASTConversionContext convContext) {
		slicee = ReferenceConverter.convertType(elem.next, convContext);
		from = Expression.convert(elem.lwr, convContext);
		to = Expression.convert(elem.upr, convContext);
	}
	

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, slicee);
			TreeVisitor.acceptChildren(visitor, from);
			TreeVisitor.acceptChildren(visitor, to);
		}
		visitor.endVisit(this);
	}


	@Override
	public Collection<DefUnit> findTargetDefUnits(boolean findFirstOnly) {
		// TODO:
		return null;
	}


	@Override
	public String toStringAsElement() {
		return slicee.toStringAsElement()
		+"["+from.toStringAsElement() +".."+ to.toStringAsElement()+"]";
	}


	@Override
	public boolean canMatch(DefUnit defunit) {
		return false;
	}

}
