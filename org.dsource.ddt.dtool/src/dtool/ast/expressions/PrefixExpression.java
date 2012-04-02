package dtool.ast.expressions;

import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.SourceRange;

public class PrefixExpression extends Expression {
	
	public interface Type {
		int ADDRESS = 1;
		int PRE_INCREMENT = 2;
		int PRE_DECREMENT = 3;
		int POINTER = 4;
		int NEGATIVE = 5;
		int POSITIVE = 6;
		int NOT = 7;
		int INVERT = 8;
	}
	public final int kind;
	public final Resolvable exp;

	public PrefixExpression(Resolvable exp, int kind, SourceRange sourceRange) {
		initSourceRange(sourceRange);
		this.exp = exp; parentize(this.exp);
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
