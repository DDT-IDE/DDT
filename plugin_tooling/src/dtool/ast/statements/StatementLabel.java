package dtool.ast.statements;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.ast.definitions.Symbol;

public class StatementLabel extends Statement {
	
	public final Symbol label;
	
	public StatementLabel(Symbol label) {
		this.label = parentize(assertNotNull(label));
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.STATEMENT_LABEL;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
		acceptVisitor(visitor, label);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append(label, ": ");
	}
	
}