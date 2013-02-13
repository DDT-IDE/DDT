package dtool.ast.expressions;

import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.IASTVisitor;
import dtool.ast.SourceRange;
import dtool.util.ArrayView;

public class ExpArrayLiteral extends Expression {
	
	public final ArrayView<Resolvable> args;
	
	public ExpArrayLiteral(ArrayView<Resolvable> args, SourceRange sourceRange) {
		initSourceRange(sourceRange);
		this.args = parentize(args);
	}
	
	public ArrayView<Resolvable> getArgs() {
		return args;
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, args);
		}
		visitor.endVisit(this);	 
	}
	
}