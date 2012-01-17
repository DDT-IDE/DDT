package dtool.ast.expressions;

import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.SourceRange;
import dtool.ast.references.Reference;

public class ExpCast extends Expression {
	
	public final Resolvable exp;
	public final Reference type;
	
	public ExpCast(Expression exp, Reference type, SourceRange sourceRange) {
		initSourceRange(sourceRange);
		this.exp = exp;
		this.type = type;
	}
	
	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if(children) {
			TreeVisitor.acceptChildren(visitor, exp);
			TreeVisitor.acceptChildren(visitor, type);
		}
		visitor.endVisit(this);
	}
	
}
