package dtool.ast.expressions;

import descent.internal.compiler.parser.NullExp;
import dtool.ast.IASTNeoVisitor;

public class ExpLiteralNull extends Expression {

	public ExpLiteralNull(NullExp elem) {
		convertNode(elem);
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);
	}
	
	@Override
	public String toStringAsElement() {
		return "null";
	}

}
