package dtool.ast.expressions;

import melnorme.lang.tooling.ast.IASTVisitor;
import melnorme.lang.tooling.ast.util.ASTCodePrinter;
import melnorme.lang.tooling.ast_actual.ASTNodeTypes;

/**
 * The dollar operator, only valid inside an indexing.
 */
public class ExpArrayLength extends Expression {
	
	public ExpArrayLength() {
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.EXP_ARRAY_LENGTH;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append("$");
	}
	
}