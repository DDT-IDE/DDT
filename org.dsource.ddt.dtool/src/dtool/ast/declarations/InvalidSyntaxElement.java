package dtool.ast.declarations;

import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNode;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.ast.statements.IStatement;
import dtool.parser.IToken;

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
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
		}
		visitor.endVisit(this);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.appendToken(badToken);
	}
	
}