package dtool.ast.declarations;

import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNode;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.ast.statements.IStatement;

public class MissingDeclaration extends ASTNode implements IDeclaration 
, IStatement //TODO: remove this eventually
{
	
	public MissingDeclaration() {
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.MISSING_DECLARATION;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
	}
	
}