package dtool.ast.declarations;

import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNode;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.ast.statements.BlockStatement;

public class DeclarationUnitTest extends ASTNode implements IDeclaration {
	
	public final BlockStatement body;
	
	public DeclarationUnitTest(BlockStatement body) {
		this.body = parentize(body);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.DECLARATION_UNITEST;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
		acceptVisitor(visitor, body);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append("unittest ");
		cp.append(body);
	}
	
}