package dtool.ast.expressions;

import melnorme.utilbox.tree.TreeVisitor;
import descent.internal.compiler.parser.SliceExp;
import dtool.ast.IASTNeoVisitor;
import dtool.descentadapter.DescentASTConverter.ASTConversionContext;
import dtool.descentadapter.ExpressionConverter;
import dtool.refmodel.IDefUnitReferenceNode;

public class ExpSlice extends Expression {

	public IDefUnitReferenceNode slicee;
	public Resolvable from;
	public Resolvable to;
	
	public ExpSlice(SliceExp elem, ASTConversionContext convContext) {
		convertNode(elem);
		slicee = ExpressionConverter.convert(elem.e1, convContext);
		from = ExpressionConverter.convert(elem.lwr, convContext);
		to = ExpressionConverter.convert(elem.upr, convContext);
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
