package dtool.ast.statements;

import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.SourceRange;
import dtool.ast.expressions.Resolvable;

public class StatementWhile extends Statement {

	public final Resolvable condition;
	public final IStatement body;

	public StatementWhile(Resolvable condition, IStatement body, SourceRange sourceRange) {
		initSourceRange(sourceRange);
		this.condition = condition; parentize(this.condition);
		this.body = body; parentizeI(this.body);
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, condition);
			TreeVisitor.acceptChildren(visitor, body);
		}
		visitor.endVisit(this);
	}

}
