package dtool.ast.expressions;

import descent.internal.compiler.parser.SuperExp;
import dtool.ast.IASTNeoVisitor;

public class ExpSuper extends Expression {

	public ExpSuper(SuperExp elem) {
		convertNode(elem);
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);	
	}

}
