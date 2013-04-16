package dtool.ast.statements;

import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.IASTVisitor;
import dtool.ast.expressions.Resolvable;

public class StatementGotoCase extends Statement {
	
	public Resolvable exp;
	
	public StatementGotoCase(Resolvable exp) {
		this.exp = parentize(exp);
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, exp);
		}
		visitor.endVisit(this);
	}
	
}