package dtool.ast.declarations;

import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.ASTNeoNode;
import dtool.ast.IASTVisitor;
import dtool.ast.references.RefIdentifier;
import dtool.ast.statements.IStatement;

public class DeclarationAliasThis extends ASTNeoNode implements IStatement {
	
	public final RefIdentifier targetDef;
	
	public DeclarationAliasThis(RefIdentifier targetDef) {
		this.targetDef = parentize(targetDef);
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, targetDef);
		}
		visitor.endVisit(this);
	}
	
}
