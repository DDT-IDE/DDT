package dtool.ast.statements;

import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.SourceRange;
import dtool.ast.expressions.Resolvable;

public class StatementCase extends Statement {
	
	public final Resolvable exp;
	public final IStatement st;
	
	public StatementCase(Resolvable exp, IStatement st, SourceRange sourceRange) {
		initSourceRange(sourceRange);
		this.exp = parentize(exp);
		this.st = parentizeI(st);
	}
	
	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, exp);
			TreeVisitor.acceptChildren(visitor, st);
		}
		visitor.endVisit(this);
	}
	
}