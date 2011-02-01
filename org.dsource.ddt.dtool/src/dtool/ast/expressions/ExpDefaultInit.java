package dtool.ast.expressions;

import descent.internal.compiler.parser.FileInitExp;
import descent.internal.compiler.parser.LineInitExp;
import descent.internal.compiler.parser.TOK;
import dtool.ast.IASTNeoVisitor;

public class ExpDefaultInit extends Expression {
	
	protected final TOK subop;
	
	public ExpDefaultInit(FileInitExp elem) {
		convertNode(elem);
		this.subop = TOK.TOKfile; 
	}
	
	public ExpDefaultInit(LineInitExp elem) {
		convertNode(elem);
		this.subop = TOK.TOKline;
	}
	
	@Override
	public void accept0(IASTNeoVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);	 
	}
	
}
