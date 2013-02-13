package dtool.ast.expressions;

import dtool.ast.IASTVisitor;
import dtool.ast.SourceRange;

public class ExpDefaultInit extends Expression {
	
	public enum DefaultInit {
		FILE,
		LINE
	}
	public final DefaultInit defInit;
	
	public ExpDefaultInit(DefaultInit defInit, SourceRange sourceRange) {
		initSourceRange(sourceRange);
		this.defInit = defInit;
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);	 
	}
	
}