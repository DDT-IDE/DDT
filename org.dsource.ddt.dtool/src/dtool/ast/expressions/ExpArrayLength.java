package dtool.ast.expressions;

import dtool.ast.ASTCodePrinter;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.SourceRange;

/**
 * The dollar operator, only valid inside an indexing.
 */
public class ExpArrayLength extends Expression {
	
	public ExpArrayLength(SourceRange sourceRange) {
		initSourceRange(sourceRange);
	}
	
	@Override
	public void accept0(IASTNeoVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append("$");
	}
	
}