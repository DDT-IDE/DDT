package dtool.ast.expressions;

import dtool.ast.ASTCodePrinter;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.SourceRange;
import dtool.parser.Token;

public class ExpLiteralString extends Expression {
	
	public final Token stringToken; // TODO: this can be multiple tokens?
	
	public ExpLiteralString(Token stringToken, SourceRange sourceRange) {
		initSourceRange(sourceRange);
		this.stringToken = stringToken;
	}
	
	@Override
	public void accept0(IASTNeoVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);	 
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append(stringToken);
	}
	
}