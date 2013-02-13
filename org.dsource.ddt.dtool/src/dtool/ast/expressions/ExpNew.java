package dtool.ast.expressions;

import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.IASTVisitor;
import dtool.ast.SourceRange;
import dtool.ast.references.Reference;
import dtool.util.ArrayView;

public class ExpNew extends Expression {
	
	public final ArrayView<Resolvable> allocargs;
	public final Reference newtype;
	public final ArrayView<Resolvable> args;
	
	public ExpNew(ArrayView<Resolvable> atorArgs, Reference type, ArrayView<Resolvable> args, SourceRange sourceRange) {
		initSourceRange(sourceRange);
		this.allocargs = parentize(atorArgs);
		this.newtype = parentize(type);
		this.args = parentize(args);
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, allocargs);
			TreeVisitor.acceptChildren(visitor, newtype);
			TreeVisitor.acceptChildren(visitor, args);
		}
		visitor.endVisit(this);
	}
	
}