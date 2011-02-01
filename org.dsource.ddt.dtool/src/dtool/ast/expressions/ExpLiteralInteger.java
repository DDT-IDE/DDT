package dtool.ast.expressions;

import java.math.BigInteger;

import descent.internal.compiler.parser.IntegerExp;
import descent.internal.compiler.parser.TY;
import descent.internal.compiler.parser.TypeBasic;
import dtool.ast.ASTNeoNode;
import dtool.ast.IASTNeoVisitor;

public class ExpLiteralInteger extends Expression {

	public static ASTNeoNode convertIntegerExp(IntegerExp node) {
		if(((TypeBasic) node.type).ty == TY.Tbool)
			return new ExpLiteralBool(node);
		else
			return new ExpLiteralInteger(node);
			
	}
	
	BigInteger num;

	public ExpLiteralInteger(IntegerExp elem) {
		convertNode(elem);
		if(elem.value != null) {
			num = elem.value.bigIntegerValue();
		} else {
			// TODO special tokens __LINE__ , etc.
		}
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
