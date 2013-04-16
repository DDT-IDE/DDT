package dtool.ast.statements;

import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.IASTVisitor;

public class StatementDefault extends Statement {
	
	public final IStatement st;
	
	public StatementDefault(IStatement st) {
		this.st = parentizeI(st);
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, st);
		}
		visitor.endVisit(this);
	}
	
}