package dtool.ast.expressions;

import dtool.ast.IASTNeoVisitor;
import dtool.ast.SourceRange;

public class ExpLiteralString extends Expression {
	
	char[][] strings;
	public final char[] charArray;
	public final String string;
	
	public ExpLiteralString(String s, SourceRange sourceRange) {
		initSourceRange(sourceRange);
		this.string = s;
		charArray = s.toCharArray();
	}
	
	@Override
	public void accept0(IASTNeoVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);	 
	}
	
	@Override
	public String toStringAsElement() {
		return "\"" + this.string.toString() + "\"";
	}
	
}