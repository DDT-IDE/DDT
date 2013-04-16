package dtool.ast.statements;

import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.IASTVisitor;
import dtool.ast.expressions.Resolvable;

public class StatementWith extends Statement {
	
	public final Resolvable exp;
	public final IStatement body;
	
	public StatementWith(Resolvable exp, IStatement body) {
		this.exp = parentize(exp);
		this.body = parentizeI(body);
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, exp);
			TreeVisitor.acceptChildren(visitor, body);
		}
		visitor.endVisit(this);
	}
	
}