package dtool.ast.expressions;

import dtool.ast.IASTNeoVisitor;
import dtool.ast.SourceRange;

public class InitializerVoid extends Initializer {
	
	public InitializerVoid(SourceRange sourceRange) {
		initSourceRange(sourceRange);		
	}
	
	@Override
	public void accept0(IASTNeoVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);

	}

}
