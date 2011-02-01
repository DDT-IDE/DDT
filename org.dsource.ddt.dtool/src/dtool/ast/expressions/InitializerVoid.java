package dtool.ast.expressions;

import descent.internal.compiler.parser.VoidInitializer;
import dtool.ast.IASTNeoVisitor;

public class InitializerVoid extends Initializer {

	public InitializerVoid(VoidInitializer elem) {
		convertNode(elem);
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);

	}

}
