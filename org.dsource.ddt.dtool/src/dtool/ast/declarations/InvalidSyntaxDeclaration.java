package dtool.ast.declarations;

import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNeoNode;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.statements.IStatement;
import dtool.parser.Token;

public class InvalidSyntaxDeclaration extends ASTNeoNode implements IStatement {
	
	public final Token badToken;
	
	public InvalidSyntaxDeclaration(Token badToken) {
		super(badToken.getSourceRange());
		this.badToken = badToken;
	}
	
	@Override
	public void accept0(IASTNeoVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append(badToken);
	}
	
}