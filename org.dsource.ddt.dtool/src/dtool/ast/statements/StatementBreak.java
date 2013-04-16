package dtool.ast.statements;

import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.IASTVisitor;
import dtool.ast.definitions.Symbol;

public class StatementBreak extends Statement {
	
	public Symbol id;
	
	public StatementBreak(Symbol id) {
		this.id = parentize(id);
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, id);
		}
		visitor.endVisit(this);
	}
	
}