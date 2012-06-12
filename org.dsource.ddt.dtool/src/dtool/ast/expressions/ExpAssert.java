package dtool.ast.expressions;

import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.SourceRange;

public class ExpAssert extends Expression {
	
	public final Resolvable exp;
	public final Resolvable msg;
	
	public ExpAssert(Resolvable exp, Resolvable msg, SourceRange sourceRange) {
		initSourceRange(sourceRange);
		this.exp = parentize(exp);
		this.msg = parentize(msg);
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