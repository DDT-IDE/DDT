package dtool.ast.expressions;

import descent.internal.compiler.parser.IntegerExp;
import dtool.ast.IASTNeoVisitor;

public class ExpLiteralBool extends Expression {
	
	public final boolean value;

	public ExpLiteralBool(IntegerExp node) {
		convertNode(node);
		this.value = node.value.intValue() != 0;
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);	
	}
	
	@Override
	public String toStringAsElement() {
		return String.valueOf(value);
	}

}
