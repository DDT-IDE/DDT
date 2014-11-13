package dtool.ast.declarations;

import melnorme.lang.tooling.ast.IASTVisitor;
import melnorme.lang.tooling.ast.util.ASTCodePrinter;
import melnorme.lang.tooling.ast_actual.ASTNode;
import melnorme.lang.tooling.ast_actual.ASTNodeTypes;
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