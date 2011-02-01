package dtool.ast.expressions;

import descent.internal.compiler.parser.RealExp;
import dtool.ast.IASTNeoVisitor;

public class ExpLiteralReal extends Expression {

	public ExpLiteralReal(RealExp elem) {
		convertNode(elem);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);	 
	}
	
	@Override
	public String toStringAsElement() {
		return "<REAL>";
	}

}
