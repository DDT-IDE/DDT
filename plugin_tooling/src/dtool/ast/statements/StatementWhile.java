package dtool.ast.statements;

import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.ast.expressions.Expression;
import dtool.ast.expressions.MissingParenthesesExpression;

public class StatementWhile extends Statement {
	
	public final Expression condition;
	public final IStatement body;
	
	public StatementWhile(Expression condition, IStatement body) {
		this.condition = parentize(condition);
		this.body = parentizeI(body);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.STATEMENT_WHILE;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
		acceptVisitor(visitor, condition);
		acceptVisitor(visitor, body);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append("while ");
		MissingParenthesesExpression.appendParenthesesExp(cp, condition);
		cp.append(body);
	}
	
}