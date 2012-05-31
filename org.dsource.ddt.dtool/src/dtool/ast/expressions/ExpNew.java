package dtool.ast.expressions;

import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.SourceRange;
import dtool.ast.definitions.ArrayView;
import dtool.ast.references.Reference;

public class ExpNew extends Expression {
	
	public final ArrayView<Resolvable> allocargs;
	public final Reference newtype;
	public final ArrayView<Resolvable> args;
	
	public ExpNew(Resolvable[] atorArgs, Reference type, Resolvable[] args, SourceRange sourceRange) {
		initSourceRange(sourceRange);
		this.allocargs = ArrayView.create(atorArgs); parentize(this.allocargs);
		this.newtype = type; parentize(this.newtype);
		this.args = ArrayView.create(args); parentize(this.args);
	}
	
	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, allocargs);
			TreeVisitor.acceptChildren(visitor, newtype);
			TreeVisitor.acceptChildren(visitor, args);
		}
		visitor.endVisit(this);
	}
	
}
