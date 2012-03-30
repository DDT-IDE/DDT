package dtool.ast.expressions;

import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.ASTNeoNode;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.SourceRange;
import dtool.ast.definitions.ArrayView;
import dtool.ast.definitions.BaseClass;

public class ExpLiteralNewAnonClass extends Expression {
	
	public final ArrayView<Resolvable> allocargs;
	public final ArrayView<Resolvable> args;
	public final ArrayView<BaseClass> baseClasses;
	public final ArrayView<ASTNeoNode> members; 


	public ExpLiteralNewAnonClass(Resolvable[] allocargs, Resolvable[] args, BaseClass[] baseClasses, ASTNeoNode[] members, SourceRange sourceRange) {
		initSourceRange(sourceRange);
		this.allocargs = new ArrayView<Resolvable>(allocargs); parentize(this.allocargs);
		this.args = new ArrayView<Resolvable>(args); parentize(this.args);
		this.baseClasses = new ArrayView<BaseClass>(baseClasses); parentize(this.baseClasses);
		this.members = new ArrayView<ASTNeoNode>(members); parentize(this.members);
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, allocargs);
			TreeVisitor.acceptChildren(visitor, args);
			TreeVisitor.acceptChildren(visitor, baseClasses);
			TreeVisitor.acceptChildren(visitor, members);
		}
		visitor.endVisit(this);
	}

}
