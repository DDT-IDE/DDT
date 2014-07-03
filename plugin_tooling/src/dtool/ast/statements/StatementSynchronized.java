package dtool.ast.statements;

import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.ast.expressions.Expression;
import dtool.ast.expressions.MissingParenthesesExpression;

public class StatementSynchronized extends Statement {
	
	public final Expression exp;
	public final IStatement body;
	
	public StatementSynchronized(Expression exp, IStatement body) {
		this.exp = parentize(exp);
		this.body = parentizeI(body);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.STATEMENT_SYNCHRONIZED;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
		acceptVisitor(visitor, exp);
		acceptVisitor(visitor, body);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append("synchronized ");
		MissingParenthesesExpression.appendParenthesesExp(cp, exp);
		cp.append(body);
	}
	
}