package dtool.ast.statements;

import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.SourceRange;
import dtool.ast.expressions.Resolvable;

public class StatementWith extends Statement {
	
	public final Resolvable exp;
	public final IStatement body;
	
	public StatementWith(Resolvable exp, IStatement body, SourceRange sourceRange) {
		initSourceRange(sourceRange);
		this.exp = parentize(exp);
		this.body = parentizeI(body);
	}
	
	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, exp);
			TreeVisitor.acceptChildren(visitor, body);
		}
		visitor.endVisit(this);
	}
	
}