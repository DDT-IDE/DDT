package dtool.ast.expressions;

import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.ast.SourceRange;

/** 
 * This class represents a syntax error where an expression delimited by paranthesis was expected.
 * It is used instead of null so that it can provide the source range of where the parenthesis was expected. 
 */
public class MissingParenthesesExpression extends Expression {
	
	public MissingParenthesesExpression(SourceRange sourceRange) {
		initSourceRange(sourceRange);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.MISSING_EXPRESSION;
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);	 
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append("/*( MissingExp )*/");
	}
	
}