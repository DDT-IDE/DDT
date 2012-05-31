package dtool.ast.expressions;

import java.math.BigInteger;

import descent.internal.compiler.parser.IntegerExp;
import descent.internal.compiler.parser.TY;
import descent.internal.compiler.parser.TypeBasic;
import dtool.ast.ASTNeoNode;
import dtool.ast.IASTNeoVisitor;
import dtool.descentadapter.DefinitionConverter;

public class ExpLiteralInteger extends Expression {

	public static ASTNeoNode convertIntegerExp(IntegerExp node) {
		if(((TypeBasic) node.type).ty == TY.Tbool)
			return new ExpLiteralBool(node);
		else
			return new ExpLiteralInteger(node);
			
	}
	
	BigInteger num;

	public ExpLiteralInteger(IntegerExp elem) {
		initSourceRange(DefinitionConverter.sourceRange(elem, false));
		if(elem.value != null) {
			num = elem.value.bigIntegerValue();
		} else {
			// TODO special tokens __LINE__ , etc.
		}
	}
	
	public ExpLiteralInteger(BigInteger value) {
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
