package dtool.ast.statements;

import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.ast.expressions.Expression;

public class StatementFor extends Statement {
	
	public final IStatement init;
	public final Expression condition;
	public final Expression increment;
	public final IStatement body;
	
	public StatementFor(IStatement init, Expression condition, Expression increment, IStatement body) {
		this.init = parentizeI(init);
		this.condition = parentize(condition);
		this.increment = parentize(increment);
		this.body = parentizeI(body);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.STATEMENT_FOR;
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, init);
			TreeVisitor.acceptChildren(visitor, condition);
			TreeVisitor.acceptChildren(visitor, increment);
			TreeVisitor.acceptChildren(visitor, body);
		}
		visitor.endVisit(this);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append("for(");
		cp.append(init);
		cp.append(condition);
		cp.append(";");
		cp.append(increment);
		cp.append(") ");
		cp.append(body);
	}
	
}