package dtool.ast.definitions;

import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.ASTNeoNode;
import dtool.ast.IASTVisitor;
import dtool.ast.statements.IStatement;

public class DefinitionPostBlit extends ASTNeoNode {
	
	public final IStatement fbody;
	
	public DefinitionPostBlit(IStatement fbody) {
		this.fbody = parentizeI(fbody);
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, fbody);
		}
		visitor.endVisit(this);
	}
	
}