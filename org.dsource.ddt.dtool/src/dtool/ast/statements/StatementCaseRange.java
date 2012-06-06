package dtool.ast.statements;

import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.SourceRange;
import dtool.ast.expressions.Resolvable;

public class StatementCaseRange extends Statement {

	public final Resolvable expFirst;
	public final Resolvable expLast;
	public final IStatement st;
	
	public StatementCaseRange(Resolvable expFirst, Resolvable expLast, IStatement st, SourceRange sourceRange) {
		initSourceRange(sourceRange);
		this.expFirst = expFirst; parentize(this.expFirst);
		this.expLast = expLast; parentize(this.expLast);
		this.st = st; parentizeI(this.st);
	}
	
	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, expFirst);
			TreeVisitor.acceptChildren(visitor, expLast);
			TreeVisitor.acceptChildren(visitor, st);
		}
		visitor.endVisit(this);
	}

}
