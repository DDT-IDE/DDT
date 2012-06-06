package dtool.ast.statements;

import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.SourceRange;
import dtool.ast.expressions.Resolvable;

public class StatementIf extends Statement {

	public Resolvable pred;
	public IStatement thenbody;
	public IStatement elsebody;

	public StatementIf(Resolvable pred, IStatement thenBody, IStatement elseBody, SourceRange sourceRange) {
		initSourceRange(sourceRange);
		this.pred = pred; parentize(this.pred);
		this.thenbody = thenBody; parentizeI(this.thenbody);
		this.elsebody = elseBody; parentizeI(this.elsebody);
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, pred);
			TreeVisitor.acceptChildren(visitor, thenbody);
			TreeVisitor.acceptChildren(visitor, elsebody);
		}
		visitor.endVisit(this);
	}

}
