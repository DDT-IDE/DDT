package dtool.ast.statements;

import dtool.ast.IASTVisitor;

public class StatementGotoDefault extends Statement {
	
	public StatementGotoDefault() {
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
		}
		visitor.endVisit(this);
	}
	
}