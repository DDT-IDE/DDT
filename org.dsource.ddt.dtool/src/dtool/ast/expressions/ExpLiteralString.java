package dtool.ast.expressions;

import dtool.ast.ASTCodePrinter;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.SourceRange;
import dtool.parser.Token;

public class ExpLiteralString extends Expression {
	
	public final String stringValue;
	public final Token stringToken; // TODO
	
	@Deprecated
	public ExpLiteralString(String s, SourceRange sourceRange) {
		initSourceRange(sourceRange);
		this.stringToken = null;
		this.stringValue = s;
	}
	
	public ExpLiteralString(Token strinToken, SourceRange sourceRange) {
		initSourceRange(sourceRange);
		this.stringToken = strinToken;
		this.stringValue = null;
	}
	
	@Override
	public void accept0(IASTNeoVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);	 
	}
	
	@Deprecated
	@Override
	public String toStringAsElement() {
		return "\"" + this.stringValue.toString() + "\"";
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append(stringToken);
	}
	
}