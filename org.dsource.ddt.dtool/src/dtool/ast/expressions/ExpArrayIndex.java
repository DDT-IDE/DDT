package dtool.ast.expressions;

import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.SourceRange;

public class ExpArrayIndex extends Expression {
	
	public final Resolvable array;
	public final Resolvable[] args;
	
	public ExpArrayIndex(Resolvable array, Resolvable[] args, SourceRange sourceRange) {
		initSourceRange(sourceRange);
		this.array = array;
		this.args = args;
	}
	
	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, array);
			TreeVisitor.acceptChildren(visitor, args);
		}
		visitor.endVisit(this);	 
	}
	
}
