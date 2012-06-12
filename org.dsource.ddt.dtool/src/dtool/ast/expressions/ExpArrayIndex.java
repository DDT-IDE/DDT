package dtool.ast.expressions;

import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.SourceRange;
import dtool.util.ArrayView;

public class ExpArrayIndex extends Expression {
	
	public final Resolvable array;
	public final ArrayView<Resolvable> args;
	
	public ExpArrayIndex(Resolvable array, ArrayView<Resolvable> args, SourceRange sourceRange) {
		initSourceRange(sourceRange);
		this.array = parentize(array);
		this.args = parentize(args);
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