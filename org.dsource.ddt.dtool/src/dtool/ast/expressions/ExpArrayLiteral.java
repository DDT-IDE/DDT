package dtool.ast.expressions;

import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.SourceRange;

public class ExpArrayLiteral extends Expression {
	
	public final Resolvable[] args;
	
	public ExpArrayLiteral(Resolvable[] args, SourceRange sourceRange) {
		initSourceRange(sourceRange);
		this.args = args;
	}
	
	public Resolvable[] getArgs() {
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
