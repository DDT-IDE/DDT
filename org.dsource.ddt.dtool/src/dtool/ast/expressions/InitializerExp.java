package dtool.ast.expressions;

import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.SourceRange;

public class InitializerExp extends Initializer {
	
	public final Resolvable exp;
	
	public InitializerExp(Resolvable exp, SourceRange sourceRange) {
		initSourceRange(sourceRange);
		this.exp = exp; parentize(this.exp);
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
