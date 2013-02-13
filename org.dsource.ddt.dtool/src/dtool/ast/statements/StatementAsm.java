package dtool.ast.statements;

import dtool.ast.IASTVisitor;
import dtool.ast.SourceRange;

public class StatementAsm extends Statement {
	
	public StatementAsm(SourceRange sourceRange) {
		initSourceRange(sourceRange);
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
		}
		visitor.endVisit(this);
	}
	
}