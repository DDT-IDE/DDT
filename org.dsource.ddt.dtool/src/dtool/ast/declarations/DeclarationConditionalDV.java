package dtool.ast.declarations;

import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.NodeList;
import dtool.ast.SourceRange;
import dtool.ast.definitions.Symbol;

public class DeclarationConditionalDV extends DeclarationConditional {
	
	public final Symbol ident;
	public final boolean isDebug;
	
	public DeclarationConditionalDV(boolean isDebug, Symbol id, NodeList thenDecls, NodeList elseDecls,
			SourceRange sourceRange) {
		super(thenDecls, elseDecls, sourceRange );
		this.isDebug = isDebug;
		this.ident = parentize(id);
	}
	
	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, ident);
			TreeVisitor.acceptChildren(visitor, NodeList.getNodes(thenDecls));
			TreeVisitor.acceptChildren(visitor, NodeList.getNodes(elseDecls));
		}
		visitor.endVisit(this);
	}
	
	@Override
	public String toStringAsElement() {
		if(ident!= null) {
			return "["+ (isDebug?"debug":"version") + "("+ident.toStringAsCode()+")]";
		} else {
			return "["+ (isDebug?"debug":"version")+"()]";
		}
	}
	
}