package dtool.ast.statements;

import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.ast.expressions.Expression;

public class ForeachRangeExpression extends Expression {
	
	public final Expression lower;
	public final Expression upper;
	
	public ForeachRangeExpression(Expression lower, Expression upper) {
		this.lower = parentize(lower);
		this.upper = parentize(upper);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.FOREACH_RANGE_EXPRESSION;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
		acceptVisitor(visitor, lower);
		acceptVisitor(visitor, upper);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append(lower);
		cp.append(" .. ");
		cp.append(upper);
	}
	
}