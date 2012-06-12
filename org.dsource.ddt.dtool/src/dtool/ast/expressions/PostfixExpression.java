package dtool.ast.expressions;

import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.SourceRange;

public class PostfixExpression extends Expression {
	
	public interface Type {
		int POST_INCREMENT = 9;
		int POST_DECREMENT = 10;
	}
	
	public final int kind;
	public final Resolvable exp;
	
	public PostfixExpression(Resolvable exp, int kind, SourceRange sourceRange) {
		initSourceRange(sourceRange);
		this.exp = parentize(exp);
		this.kind = kind;
	}
	
	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, exp);
		}
		visitor.endVisit(this);
	}
	
}