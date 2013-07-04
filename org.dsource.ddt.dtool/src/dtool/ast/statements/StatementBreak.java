package dtool.ast.statements;

import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.ast.definitions.Symbol;

public class StatementBreak extends Statement {
	
	public Symbol id;
	
	public StatementBreak(Symbol id) {
		this.id = parentize(id);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.STATEMENT_BREAK;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
		acceptVisitor(visitor, id);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append("break ");
		cp.append(id);
		cp.append(";");
	}
	
}