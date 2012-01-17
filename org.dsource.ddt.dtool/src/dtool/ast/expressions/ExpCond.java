package dtool.ast.expressions;

import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.SourceRange;

public class ExpCond extends Expression {
	
	public Resolvable predExp;
	public Resolvable trueExp;
	public Resolvable falseExp;
	
	public ExpCond(Resolvable predExp, Resolvable trueExp, Resolvable falseExp, SourceRange sourceRange) {
		initSourceRange(sourceRange);
		this.predExp = predExp; 
		this.trueExp = trueExp;
		this.falseExp = falseExp; 
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
