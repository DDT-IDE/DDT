package dtool.ast.declarations;

import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.IASTVisitor;
import dtool.ast.NodeList;
import dtool.ast.definitions.Symbol;

public class DeclarationDebugVersion extends AbstractConditionalDeclaration {
	
	public final Symbol ident;
	public final boolean isDebug;
	
	public DeclarationDebugVersion(boolean isDebug, Symbol id, NodeList thenDecls, NodeList elseDecls) {
		super(thenDecls, elseDecls);
		this.isDebug = isDebug;
		this.ident = parentize(id);
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
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