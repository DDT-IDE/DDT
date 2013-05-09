package dtool.ast.statements;

import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.ast.expressions.Expression;
import dtool.util.ArrayView;

public class StatementForeach extends Statement {
	
	public final boolean isForeachReverse;
	public final ArrayView<ForeachVariableDef> varParams;
	public final Expression iterable;
	public final IStatement body;
	
	public StatementForeach(boolean isForeachReverse, ArrayView<ForeachVariableDef> varParams, Expression iterable,
			IStatement body) {
		this.varParams = parentizeI(varParams);
		this.iterable = parentize(iterable);
		this.body = parentizeI(body);
		this.isForeachReverse = isForeachReverse;
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.STATEMENT_FOREACH;
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, varParams);
			TreeVisitor.acceptChildren(visitor, iterable);
			TreeVisitor.acceptChildren(visitor, body);
		}
		visitor.endVisit(this);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append(isForeachReverse ? "foreach_reverse(" : "foreach(");
		cp.appendList(varParams, ",");
		cp.append(";");
		cp.append(iterable);
		cp.append(") ");
		cp.append(body);
	}
	
}