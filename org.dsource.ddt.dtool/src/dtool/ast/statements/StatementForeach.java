package dtool.ast.statements;

import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.SourceRange;
import dtool.ast.definitions.IFunctionParameter;
import dtool.ast.expressions.Resolvable;
import dtool.util.ArrayView;

public class StatementForeach extends Statement {
	
	public final boolean reverse;
	public final ArrayView<IFunctionParameter> params;
	public final Resolvable iterable;
	public final IStatement body;
	
	public StatementForeach(ArrayView<IFunctionParameter> params, Resolvable iterable, IStatement body,
			boolean reverse, SourceRange sourceRange) {
		initSourceRange(sourceRange);
		this.params = params; parentizeI(this.params);
		this.iterable = iterable; parentize(this.iterable);
		this.body = body; parentizeI(this.body);
		this.reverse = reverse;
	}
	
	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, params);
			TreeVisitor.acceptChildren(visitor, iterable);
			TreeVisitor.acceptChildren(visitor, body);
		}
		visitor.endVisit(this);
	}
	
}