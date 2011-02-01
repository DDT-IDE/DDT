package dtool.ast.declarations;

import melnorme.utilbox.tree.TreeVisitor;
import descent.internal.compiler.parser.AliasThis;
import dtool.ast.ASTNeoNode;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.references.RefIdentifier;

public class DeclarationAliasThis extends ASTNeoNode {
	
	public final RefIdentifier targetDef;
	
	
	public DeclarationAliasThis(AliasThis elem) {
		convertNode(elem);
		this.targetDef = new RefIdentifier(new String(elem.ident.ident));
	}
	
	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, targetDef);
		}
		visitor.endVisit(this);
	}
	
}
