package dtool.ast.statements;

import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.ast.expressions.Expression;
import dtool.ast.expressions.MissingParenthesesExpression;

public class StatementSwitch extends Statement {
	
	public final boolean isFinal;
	public final Expression exp;
	public final IStatement body;
	
	public StatementSwitch(boolean isFinal, Expression exp, IStatement body) {
		this.isFinal = isFinal;
		this.exp = parentize(exp);
		this.body = parentizeI(body);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.STATEMENT_SWITCH;
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, exp);
			TreeVisitor.acceptChildren(visitor, body);
		}
		visitor.endVisit(this);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append(isFinal, "final ");
		cp.append("switch ");
		MissingParenthesesExpression.appendParenthesesExp(cp, exp);
		cp.append(body);
	}
	
}