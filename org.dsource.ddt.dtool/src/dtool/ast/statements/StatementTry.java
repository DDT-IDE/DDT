package dtool.ast.statements;


import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.util.ArrayView;

public class StatementTry extends Statement {
	
	public final IStatement body;
	public final ArrayView<CatchClause> catches;
	public final IStatement finallyBody;
	
	public StatementTry(IStatement body, ArrayView<CatchClause> catches, IStatement finallyBody) {
		this.body = parentizeI(body);
		this.catches = parentize(catches);
		this.finallyBody = parentizeI(finallyBody);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.STATEMENT_TRY;
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, body);
			TreeVisitor.acceptChildren(visitor, catches);
			TreeVisitor.acceptChildren(visitor, finallyBody);
		}
		visitor.endVisit(this);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append("try ");
		cp.append(body);
		cp.appendList(catches, "\n");
		cp.append("finally ", finallyBody);
	}
	
}