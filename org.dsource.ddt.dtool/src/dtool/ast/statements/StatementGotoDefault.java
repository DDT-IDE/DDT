package dtool.ast.statements;

import dtool.ast.IASTNeoVisitor;
import dtool.ast.SourceRange;

public class StatementGotoDefault extends Statement {

	public StatementGotoDefault(SourceRange sourceRange) {
		initSourceRange(sourceRange);		
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
		}
		visitor.endVisit(this);
	}

}
