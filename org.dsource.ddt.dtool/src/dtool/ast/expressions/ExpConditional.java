package dtool.ast.expressions;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import melnorme.utilbox.tree.TreeVisitor;
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
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, condExp);
			TreeVisitor.acceptChildren(visitor, thenExp);
			TreeVisitor.acceptChildren(visitor, elseExp);
		}
		visitor.endVisit(this);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append(condExp, "?");
		cp.append(thenExp);
		cp.append(":");
		cp.append(elseExp);
	}
	
}