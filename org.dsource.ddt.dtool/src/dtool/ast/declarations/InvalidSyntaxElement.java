package dtool.ast.declarations;

import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNode;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.ast.statements.IStatement;
import dtool.parser.Token;

public class InvalidSyntaxElement extends ASTNode implements IStatement {
	
	public final Token badToken;
	
	public InvalidSyntaxElement(Token badToken) {
		this.badToken = badToken;
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.INVALID_SYNTAX;
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
		}
		visitor.endVisit(this);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append(badToken);
	}
	
}