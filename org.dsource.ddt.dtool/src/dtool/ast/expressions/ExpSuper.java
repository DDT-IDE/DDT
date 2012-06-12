package dtool.ast.expressions;

import dtool.ast.IASTNeoVisitor;
import dtool.ast.SourceRange;

public class ExpSuper extends Expression {
	
	public ExpSuper(SourceRange sourceRange) {
		initSourceRange(sourceRange);
	}
	
	@Override
	public void accept0(IASTNeoVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);	
	}
	
}