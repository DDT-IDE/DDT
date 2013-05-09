package dtool.ast.statements;

import melnorme.utilbox.tree.TreeVisitor;
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
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, lower);
			TreeVisitor.acceptChildren(visitor, upper);
		}
		visitor.endVisit(this);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append(lower);
		cp.append(" .. ");
		cp.append(upper);
	}
	
}