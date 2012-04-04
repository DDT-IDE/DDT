package dtool.ast.statements;

import dtool.ast.IASTNeoVisitor;
import dtool.ast.SourceRange;

public class StatementAsm extends Statement {

	public StatementAsm(SourceRange sourceRange) {
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
