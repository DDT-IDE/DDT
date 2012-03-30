package dtool.ast.expressions;

import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.SourceRange;
import dtool.ast.references.Reference;

public class ExpTypeid extends Expression {
	
	Reference typeArgument;
	Expression expressionArgument;
	
	public ExpTypeid(Reference typeArgument, SourceRange sourceRange) {
		initSourceRange(sourceRange);
		this.typeArgument = typeArgument; parentize(this.typeArgument);
		this.expressionArgument = null;
		
	}

	public ExpTypeid(Expression expressionArgument, SourceRange sourceRange) {
		initSourceRange(sourceRange);
		this.expressionArgument = expressionArgument; parentize(this.expressionArgument);
		this.typeArgument = null;
	}

	public Resolvable getArgument() {
		return typeArgument != null ? typeArgument : expressionArgument;
	}
	
	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, getArgument());
		}
		visitor.endVisit(this);
	}
	
}
