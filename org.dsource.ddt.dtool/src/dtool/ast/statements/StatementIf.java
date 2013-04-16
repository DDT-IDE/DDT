package dtool.ast.statements;

import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.IASTVisitor;
import dtool.ast.expressions.Resolvable;

public class StatementIf extends Statement {
	
	public final Resolvable pred;
	public final IStatement thenBody;
	public final IStatement elseBody;
	
	public StatementIf(Resolvable pred, IStatement thenBody, IStatement elseBody) {
		this.pred = parentize(pred);
		this.thenBody = parentizeI(thenBody);
		this.elseBody = parentizeI(elseBody);
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, pred);
			TreeVisitor.acceptChildren(visitor, thenBody);
			TreeVisitor.acceptChildren(visitor, elseBody);
		}
		visitor.endVisit(this);
	}
	
}