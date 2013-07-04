package dtool.ast.statements;

import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.ast.definitions.Symbol;

public class StatementContinue extends Statement {
	
	public final Symbol id;
	
	public StatementContinue(Symbol id) {
		this.id = parentize(id);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.STATEMENT_CONTINUE;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
		acceptVisitor(visitor, id);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append("continue ");
		cp.append(id);
		cp.append(";");
	}
	
}