package dtool.ast.statements;

import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.SourceRange;

public class StatementVolatile extends Statement {
	
	public final IStatement st;
	
	public StatementVolatile(IStatement st, SourceRange sourceRange) {
		initSourceRange(sourceRange);
		this.st = parentizeI(st);
	}
	
	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, st);
		}
		visitor.endVisit(this);
	}
	
}