package dtool.ast.expressions;

import descent.internal.compiler.parser.ThisExp;
import dtool.ast.IASTNeoVisitor;

public class ExpThis extends Expression {

	public ExpThis(ThisExp elem) {
		convertNode(elem);
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);
	}

}
