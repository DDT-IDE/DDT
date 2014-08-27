package dtool.ast.declarations;

import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNode;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.ast.statements.IStatement;
import dtool.parser.common.IToken;

public class InvalidSyntaxElement extends ASTNode implements IDeclaration, IStatement {
	
	public final boolean isStatementContext;
	public final IToken badToken;
	
	public InvalidSyntaxElement(IToken badToken) {
		this(false, badToken);
	}
	public InvalidSyntaxElement(boolean isStatementContext, IToken badToken) {
		this.isStatementContext = isStatementContext;
		this.badToken = badToken;
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.INVALID_SYNTAX;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.appendToken(badToken);
	}
	
}