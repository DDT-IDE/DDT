package dtool.ast.expressions;

import dtool.ast.IASTNeoVisitor;
import dtool.ast.SourceRange;

public class ExpDefaultInit extends Expression {
	
	public enum DefaultInit {
		FILE,
		LINE
	}
	protected final DefaultInit defInit;
	
	public ExpDefaultInit(DefaultInit defInit, SourceRange sourceRange) {
		initSourceRange(sourceRange);
		this.defInit = defInit;
	}
	
	@Override
	public void accept0(IASTNeoVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);	 
	}
	
}
