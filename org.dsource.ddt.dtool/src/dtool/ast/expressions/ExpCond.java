package dtool.ast.expressions;

import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.SourceRange;

public class ExpCond extends Expression {
	
	public final Resolvable predExp;
	public final Resolvable trueExp;
	public final Resolvable falseExp;
	
	public ExpCond(Resolvable predExp, Resolvable trueExp, Resolvable falseExp, SourceRange sourceRange) {
		initSourceRange(sourceRange);
		this.predExp = parentize(predExp);
		this.trueExp = parentize(trueExp);
		this.falseExp = parentize(falseExp);
	}
	
	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, predExp);
			TreeVisitor.acceptChildren(visitor, trueExp);
			TreeVisitor.acceptChildren(visitor, falseExp);
		}
		visitor.endVisit(this);
	}
	
}