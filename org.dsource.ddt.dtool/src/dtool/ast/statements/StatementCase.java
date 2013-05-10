package dtool.ast.statements;

import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.ast.expressions.Expression;
import dtool.util.ArrayView;

public class StatementCase extends Statement {
	
	public final ArrayView<Expression> caseValues;
	public final IStatement body;
	
	public StatementCase(ArrayView<Expression> caseValues, IStatement body) {
		this.caseValues = parentize(caseValues);
		this.body = parentizeI(body);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.STATEMENT_CASE;
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, caseValues);
			TreeVisitor.acceptChildren(visitor, body);
		}
		visitor.endVisit(this);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append("case ");
		cp.appendList(caseValues, ", ");
		cp.append(" : ");
		cp.append(body);
	}
	
}