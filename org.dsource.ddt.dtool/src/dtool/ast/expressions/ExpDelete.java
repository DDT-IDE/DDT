package dtool.ast.expressions;

import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.SourceRange;

public class ExpDelete extends Expression {
	
	public final Resolvable exp;
	
	public ExpDelete(Resolvable exp, SourceRange sourceRange) {
		initSourceRange(sourceRange);
		this.exp = parentize(exp);
	}
	
	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, exp);
		}
		visitor.endVisit(this);	
	}
	
}