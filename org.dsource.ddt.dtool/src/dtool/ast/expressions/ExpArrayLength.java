package dtool.ast.expressions;

import descent.internal.compiler.parser.ArrayLengthExp;
import dtool.ast.IASTNeoVisitor;

public class ExpArrayLength extends Expression {
	
	public ExpArrayLength(ArrayLengthExp element) {
		convertNode(element);
	}
	
	@Override
	public void accept0(IASTNeoVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);
	}
	
}
