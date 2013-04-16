package dtool.ast.statements;

import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.IASTVisitor;
import dtool.ast.definitions.IFunctionParameter;
import dtool.ast.expressions.Resolvable;
import dtool.util.ArrayView;

public class StatementForeach extends Statement {
	
	public final boolean reverse;
	public final ArrayView<IFunctionParameter> params;
	public final Resolvable iterable;
	public final IStatement body;
	
	public StatementForeach(ArrayView<IFunctionParameter> params, Resolvable iterable, IStatement body,
			boolean reverse) {
		this.params = parentizeI(params);
		this.iterable = parentize(iterable);
		this.body = parentizeI(body);
		this.reverse = reverse;
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, params);
			TreeVisitor.acceptChildren(visitor, iterable);
			TreeVisitor.acceptChildren(visitor, body);
		}
		visitor.endVisit(this);
	}
	
}