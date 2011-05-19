package dtool.ast.expressions;

import melnorme.utilbox.tree.TreeVisitor;
import descent.internal.compiler.parser.AssertExp;
import dtool.ast.IASTNeoVisitor;
import dtool.descentadapter.DescentASTConverter.ASTConversionContext;
import dtool.descentadapter.ExpressionConverter;

public class ExpAssert extends Expression {
	
	public Resolvable exp;
	public Resolvable msg;

	public ExpAssert(AssertExp elem, ASTConversionContext convContext) {
		convertNode(elem);
		this.exp = ExpressionConverter.convert(elem.e1, convContext);
		this.msg = ExpressionConverter.convert(elem.msg, convContext);
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, exp);
			TreeVisitor.acceptChildren(visitor, msg);
		}
		visitor.endVisit(this);
	}

}
