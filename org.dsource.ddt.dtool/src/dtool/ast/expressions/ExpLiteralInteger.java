package dtool.ast.expressions;

import java.math.BigInteger;

import dtool.ast.IASTNeoVisitor;
import dtool.ast.SourceRange;

public class ExpLiteralInteger extends Expression {
	
	public final BigInteger num;
	
	public ExpLiteralInteger(BigInteger value, SourceRange sourceRange) {
		initSourceRange(sourceRange);
		this.num = value;
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);	 
	}

	@Override
	public String toStringAsElement() {
		if(num == null)
			return "__<SPECIAL>__";
		return num.toString();
	}

}
