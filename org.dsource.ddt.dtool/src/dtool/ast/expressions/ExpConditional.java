package dtool.ast.expressions;

import static dtool.util.NewUtils.assertNotNull_;
import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.ASTCodePrinter;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.SourceRange;

public class ExpConditional extends Expression {
	
	public final Resolvable condExp;
	public final Resolvable thenExp;
	public final Resolvable elseExp;
	
	public ExpConditional(Resolvable condExp, Resolvable thenExp, Resolvable elseExp, SourceRange sourceRange) {
		initSourceRange(sourceRange);
		this.condExp = parentize(assertNotNull_(condExp));
		this.thenExp = parentize(thenExp);
		this.elseExp = parentize(elseExp); 
	}
	
	@Override
	public void accept0(IASTNeoVisitor visitor) {
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
		cp.appendNode(condExp, "?");
		cp.append(thenExp);
		cp.append(":");
		cp.append(elseExp);
	}
	
}