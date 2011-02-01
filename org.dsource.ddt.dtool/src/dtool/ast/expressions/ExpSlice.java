package dtool.ast.expressions;

import melnorme.utilbox.tree.TreeVisitor;
import descent.internal.compiler.parser.SliceExp;
import dtool.ast.IASTNeoVisitor;
import dtool.descentadapter.DescentASTConverter.ASTConversionContext;
import dtool.refmodel.IDefUnitReferenceNode;

public class ExpSlice extends Expression {

	public IDefUnitReferenceNode slicee;
	public Resolvable from;
	public Resolvable to;
	
	public ExpSlice(SliceExp elem, ASTConversionContext convContext) {
		convertNode(elem);
		slicee = Expression.convert(elem.e1, convContext);
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

}
