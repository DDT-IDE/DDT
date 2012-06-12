package dtool.ast.expressions;

import dtool.ast.IASTNeoVisitor;
import dtool.ast.SourceRange;

public class ExpLiteralBool extends Expression {
	
	public final boolean value;
	
	public ExpLiteralBool(boolean value, SourceRange sourceRange) {
		initSourceRange(sourceRange);
		this.value = value;
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