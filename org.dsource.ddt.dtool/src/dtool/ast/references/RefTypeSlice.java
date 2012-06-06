package dtool.ast.references;

import java.util.Collection;

import melnorme.utilbox.tree.TreeVisitor;
import descent.internal.compiler.parser.TypeSlice;
import dtool.ast.ASTNeoNode;
import dtool.ast.DefUnitDescriptor;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.SourceRange;
import dtool.ast.definitions.DefUnit;
import dtool.ast.expressions.Resolvable;
import dtool.descentadapter.DescentASTConverter.ASTConversionContext;
import dtool.descentadapter.ExpressionConverter;
import dtool.refmodel.IDefUnitReferenceNode;

public class RefTypeSlice extends Reference {

	public final IDefUnitReferenceNode slicee;
	public final Resolvable from;
	public final Resolvable to;
	
	public RefTypeSlice(TypeSlice elem, ASTConversionContext convContext) {
		slicee = ReferenceConverter.convertType(elem.next, convContext);
		from = ExpressionConverter.convert(elem.lwr, convContext);
		to = ExpressionConverter.convert(elem.upr, convContext);
	}
	
	public RefTypeSlice(IDefUnitReferenceNode slicee, Resolvable from, Resolvable to, SourceRange sourceRange) {
		initSourceRange(sourceRange);
		this.from = from; parentize(this.from);
		this.to = to; parentize(this.to);
		this.slicee = slicee; parentize((ASTNeoNode) this.slicee);
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
	public boolean canMatch(DefUnitDescriptor defunit) {
		return false;
	}

}
