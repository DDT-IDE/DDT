package dtool.ast.expressions;

import dtool.ast.ASTCodePrinter;
import dtool.ast.IASTVisitor;

public class ExpDefaultInit extends Expression {
	
	public enum DefaultInit {
		FILE,
		LINE
	}
	public final DefaultInit defInit;
	
	public ExpDefaultInit(DefaultInit defInit) {
		this.defInit = defInit;
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);	 
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append("default");
	}
	
}