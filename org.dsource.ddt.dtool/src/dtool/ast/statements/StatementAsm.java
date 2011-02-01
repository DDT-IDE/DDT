package dtool.ast.statements;

import descent.internal.compiler.parser.AsmStatement;
import dtool.ast.IASTNeoVisitor;

public class StatementAsm extends Statement {

	public StatementAsm(AsmStatement node) {
		convertNode(node);
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
		}
		visitor.endVisit(this);
	}

}
