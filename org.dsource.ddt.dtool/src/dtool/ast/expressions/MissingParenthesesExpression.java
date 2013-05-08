package dtool.ast.expressions;

import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;

/** 
 * This class represents a syntax error where an expression delimited by parentheses was expected.
 * It is used instead of null so that it can provide the source range of where the parentheses were expected. 
 */
public class MissingParenthesesExpression extends Expression {
	
	public MissingParenthesesExpression() {
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
	
	public static void appendParenthesesExp(ASTCodePrinter cp, Expression expression) {
		if(expression instanceof MissingParenthesesExpression) {
			cp.append(expression);
		} else {
			cp.append("(", expression, ") ");
		}
	}
	
}