package dtool.ast.expressions;

import dtool.ast.IASTVisitor;

public class InitializerVoid extends Initializer {
	
	public InitializerVoid() {
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);
	}
	
}