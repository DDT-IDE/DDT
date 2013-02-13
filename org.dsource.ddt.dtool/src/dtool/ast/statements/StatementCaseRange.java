package dtool.ast.statements;

import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.IASTVisitor;
import dtool.ast.SourceRange;
import dtool.ast.expressions.Resolvable;

public class StatementCaseRange extends Statement {
	
	public final Resolvable expFirst;
	public final Resolvable expLast;
	public final IStatement st;
	
	public StatementCaseRange(Resolvable expFirst, Resolvable expLast, IStatement st, SourceRange sourceRange) {
		initSourceRange(sourceRange);
		this.expFirst = parentize(expFirst);
		this.expLast = parentize(expLast);
		this.st = parentizeI(st);
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, expFirst);
			TreeVisitor.acceptChildren(visitor, expLast);
			TreeVisitor.acceptChildren(visitor, st);
		}
		visitor.endVisit(this);
	}
	
}