package dtool.ast.statements;

import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.ast.expressions.Expression;

public class StatementIf extends Statement {
	
	public final Expression condition;
	public final IStatement thenBody;
	public final IStatement elseBody;
	
	public StatementIf(Expression condition, IStatement thenBody, IStatement elseBody) {
		this.condition = parentize(condition);
		this.thenBody = parentizeI(thenBody);
		this.elseBody = parentizeI(elseBody);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.STATEMENT_IF;
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, condition);
			TreeVisitor.acceptChildren(visitor, thenBody);
			TreeVisitor.acceptChildren(visitor, elseBody);
		}
		visitor.endVisit(this);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append("if ");
		cp.append("(", condition, ") ");
		cp.append(thenBody, " ");
		cp.append("else ", elseBody);
	}
	
}