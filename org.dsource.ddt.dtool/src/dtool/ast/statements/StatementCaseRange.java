package dtool.ast.statements;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.ast.expressions.Expression;

public class StatementCaseRange extends Statement {
	
	public final Expression expFirst;
	public final Expression expLast;
	public final IStatement body;
	
	public StatementCaseRange(Expression expFirst, Expression expLast, IStatement body) {
		this.expFirst = parentize(assertNotNull(expFirst));
		this.expLast = parentize(expLast);
		this.body = parentizeI(body);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.STATEMENT_CASE_RANGE;
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, expFirst);
			TreeVisitor.acceptChildren(visitor, expLast);
			TreeVisitor.acceptChildren(visitor, body);
		}
		visitor.endVisit(this);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append("case ", expFirst, " : .. ");
		cp.append("case ", expLast, " : ");
		cp.append(body);
	}
	
}