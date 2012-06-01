package dtool.ast.expressions;

import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.SourceRange;
import dtool.ast.definitions.ArrayView;

public class ExpArrayLiteral extends Expression {
	
	public final ArrayView<Resolvable> args;
	
	public ExpArrayLiteral(Resolvable[] args, SourceRange sourceRange) {
		initSourceRange(sourceRange);
		this.args = ArrayView.create(args); parentize(this.args);
	}
	
	public ArrayView<Resolvable> getArgs() {
		return args;
	}
	
	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, args);
		}
		visitor.endVisit(this);	 
	}

}
