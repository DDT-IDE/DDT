package dtool.ast.statements;

import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.ast.SourceRange;

public class EmptyBodyStatement extends BodyStatement implements IStatement {
	
	public EmptyBodyStatement(SourceRange sourceRange) {
		initSourceRange(sourceRange);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.STATEMENT_EMTPY_BODY;
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