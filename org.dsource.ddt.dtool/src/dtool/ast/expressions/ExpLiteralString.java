package dtool.ast.expressions;

import dtool.ast.ASTCodePrinter;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.SourceRange;
import dtool.parser.Token;

public class ExpLiteralString extends Expression {
	
	public final Token[] stringTokens;
	
	public ExpLiteralString(Token stringToken, SourceRange sourceRange) {
		this(new Token[] { stringToken }, sourceRange);
	}
	
	public ExpLiteralString(Token[] stringToken, SourceRange sourceRange) {
		initSourceRange(sourceRange);
		this.stringTokens = stringToken;
	}
	
	@Override
	public void accept0(IASTNeoVisitor visitor) {
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