package dtool.ast.expressions;

import dtool.ast.IASTVisitor;
import dtool.ast.SourceRange;

public class ExpDollar extends Expression {
	
	public ExpDollar(SourceRange sourceRange) {
		initSourceRange(sourceRange);
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);
	}
	
}