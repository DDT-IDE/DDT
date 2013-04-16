package dtool.ast.statements;

import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.IASTVisitor;
import dtool.ast.expressions.Resolvable;

public class StatementWhile extends Statement {
	
	public final Resolvable condition;
	public final IStatement body;
	
	public StatementWhile(Resolvable condition, IStatement body) {
		this.condition = parentize(condition);
		this.body = parentizeI(body);
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, condition);
			TreeVisitor.acceptChildren(visitor, body);
		}
		visitor.endVisit(this);
	}
	
}