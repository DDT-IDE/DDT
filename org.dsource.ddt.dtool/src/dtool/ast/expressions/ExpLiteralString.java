package dtool.ast.expressions;

import descent.internal.compiler.parser.StringExp;
import dtool.ast.IASTNeoVisitor;

public class ExpLiteralString extends Expression {
	
	char[][] strings;
	public final char[] charArray;
	public String string;
	
	public ExpLiteralString(StringExp elem) {
		convertNode(elem);
		this.charArray = elem.string;
		
		// TODO: AST CONV: deal with elem.allStringExps
//		this.strings = new char[elem.strings.size()][];
//		for (int i = 0; i < elem.strings.size(); i++) {
//			this.strings[i] = elem.strings.get(i).string;
//		}
	}
	
	@Override
	public void accept0(IASTNeoVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);	 
	}
	
	private Object getString() {
		if(string == null) {
			string = new String(charArray);
		}
		return string;
	}
	
	@Override
	public String toStringAsElement() {
		return "\"" + getString().toString() + "\"";
	}
	
}
