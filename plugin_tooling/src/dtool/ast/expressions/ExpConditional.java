package dtool.ast.expressions;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;

public class ExpConditional extends Expression {
	
	public final Resolvable condExp;
	public final Resolvable thenExp;
	public final Resolvable elseExp;
	
	public ExpConditional(Resolvable condExp, Resolvable thenExp, Resolvable elseExp) {
		this.condExp = parentize(assertNotNull(condExp));
		this.thenExp = parentize(thenExp);
		this.elseExp = parentize(elseExp); 
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.EXP_CONDITIONAL;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
		acceptVisitor(visitor, condExp);
		acceptVisitor(visitor, thenExp);
		acceptVisitor(visitor, elseExp);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append(condExp, "?");
		cp.append(thenExp);
		cp.append(":");
		cp.append(elseExp);
	}
	
}