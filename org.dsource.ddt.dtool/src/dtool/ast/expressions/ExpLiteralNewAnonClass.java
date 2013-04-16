package dtool.ast.expressions;

import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.ASTNeoNode;
import dtool.ast.IASTVisitor;
import dtool.ast.definitions.BaseClass;
import dtool.util.ArrayView;

public class ExpLiteralNewAnonClass extends Expression {
	
	public final ArrayView<Resolvable> allocargs;
	public final ArrayView<Resolvable> args;
	public final ArrayView<BaseClass> baseClasses;
	public final ArrayView<ASTNeoNode> members; 
	
	public ExpLiteralNewAnonClass(ArrayView<Resolvable> allocargs, ArrayView<Resolvable> args,
			ArrayView<BaseClass> baseClasses, ArrayView<ASTNeoNode> members) {
		this.allocargs = parentizeI(allocargs);
		this.args = parentizeI(args);
		this.baseClasses = parentizeI(baseClasses);
		this.members = parentizeI(members);
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
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