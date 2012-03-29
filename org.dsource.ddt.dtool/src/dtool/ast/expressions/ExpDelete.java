package dtool.ast.expressions;

import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.SourceRange;

public class ExpDelete extends Expression {

	public Resolvable exp;
	
	public ExpDelete(Resolvable exp, SourceRange sourceRange) {
		initSourceRange(sourceRange);
		this.exp = exp;
		if (this.exp != null)
			this.exp.setParent(this);
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
