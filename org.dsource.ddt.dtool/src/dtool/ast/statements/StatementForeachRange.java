package dtool.ast.statements;

import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.SourceRange;
import dtool.ast.definitions.IFunctionParameter;
import dtool.ast.expressions.Resolvable;

public class StatementForeachRange extends Statement {

	public final boolean reverse;
	public final IFunctionParameter param;
	public final Resolvable lwr;
	public final Resolvable upr;
	public final IStatement body;

	public StatementForeachRange(IFunctionParameter param, Resolvable lwr, Resolvable upr, IStatement body, boolean reverse, SourceRange sourceRange) {
		initSourceRange(sourceRange);
		this.param = param; parentize(this.param);
		this.lwr = lwr; parentize(this.lwr);
		this.upr = upr; parentize(this.upr);
		this.body = body; parentize(this.body);
		this.reverse = reverse;
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, param);
			TreeVisitor.acceptChildren(visitor, lwr);
			TreeVisitor.acceptChildren(visitor, upr);
			TreeVisitor.acceptChildren(visitor, body);
		}
		visitor.endVisit(this);
	}

}

