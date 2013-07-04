package dtool.ast.statements;


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
	public void visitChildren(IASTVisitor visitor) {
		acceptVisitor(visitor, body);
		acceptVisitor(visitor, catches);
		acceptVisitor(visitor, finallyBody);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append("try ");
		cp.append(body);
		cp.appendList(catches, "\n");
		cp.append("finally ", finallyBody);
	}
	
}