package dtool.ast.expressions;

import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.SourceRange;

public class ExpAssert extends Expression {
	
	public final Resolvable exp;
	public final Resolvable msg;
	
	public ExpAssert(Expression exp, Expression msg, SourceRange sourceRange) {
		initSourceRange(sourceRange);
		this.exp = exp;
		this.msg = msg;
	}
	
	public ExpAssert(Resolvable exp, Resolvable msg) {
		this.exp = exp;
		this.msg = msg;
		
		if (this.exp != null)
			this.exp.setParent(this);
		
		if (this.msg != null)
			this.msg.setParent(this);
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
