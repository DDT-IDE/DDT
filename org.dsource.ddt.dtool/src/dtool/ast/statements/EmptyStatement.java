package dtool.ast.statements;

import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;

public class EmptyStatement extends Statement implements IFunctionBody, IStatement {
	
	public EmptyStatement() {
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.EMPTY_STATEMENT;
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append(";");
	}
	
}