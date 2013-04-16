package dtool.ast.expressions;

import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.parser.Token;

public class ExpLiteralString extends Expression {
	
	public final Token[] stringTokens;
	
	public ExpLiteralString(Token stringToken) {
		this(new Token[] { stringToken });
	}
	
	public ExpLiteralString(Token[] stringToken) {
		this.stringTokens = stringToken;
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.EXP_LITERAL_STRING;
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);	 
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		for (Token stringToken : stringTokens) {
			cp.append(stringToken);
		}
	}
	
}