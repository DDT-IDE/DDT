package dtool.ast.declarations;

import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.SourceRange;
import dtool.ast.definitions.Symbol;

public class DeclarationConditionalDV extends DeclarationConditional {
	
	public final Symbol ident;
	public final boolean isDebug;
	
	public DeclarationConditionalDV(boolean isDebug, Symbol id, NodeList thenDecls, NodeList elseDecls, SourceRange sourceRange) {
		super(thenDecls, elseDecls, sourceRange );
		this.isDebug = isDebug;
		this.ident = id; parentize(this.ident);
	}
	
	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, ident);
			TreeVisitor.acceptChildren(visitor, NodeList.getNodes(thendecls));
			TreeVisitor.acceptChildren(visitor, NodeList.getNodes(elsedecls));
		}
		visitor.endVisit(this);
	}
	
	@Override
	public String toStringAsElement() {
		if(ident!= null) {
			return "["+ (isDebug?"debug":"version") + "("+ident.toStringAsElement()+")]";
		} else {
			return "["+ (isDebug?"debug":"version")+"()]";
		}
	}
}
