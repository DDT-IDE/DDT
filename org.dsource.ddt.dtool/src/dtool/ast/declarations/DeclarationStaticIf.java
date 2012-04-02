package dtool.ast.declarations;

import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.SourceRange;
import dtool.ast.expressions.Resolvable;

public class DeclarationStaticIf extends DeclarationConditional {
	public final Resolvable exp;
	
	public DeclarationStaticIf(Resolvable exp, NodeList thenDecls, NodeList elseDecls, SourceRange sourceRange) {
		super(thenDecls, elseDecls, sourceRange);
		this.exp = exp; parentize(this.exp);
	}
	
	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, exp);
			TreeVisitor.acceptChildren(visitor, NodeList.getNodes(thendecls));
			TreeVisitor.acceptChildren(visitor, NodeList.getNodes(elsedecls));
		}
		visitor.endVisit(this);
	}
	
	@Override
	public String toStringAsElement() {
		return "[static if("+"..."+")]";
	}
	
}
