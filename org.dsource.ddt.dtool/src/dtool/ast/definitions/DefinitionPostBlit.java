package dtool.ast.definitions;

import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.ASTNeoNode;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.SourceRange;
import dtool.ast.statements.IStatement;

public class DefinitionPostBlit extends ASTNeoNode {
	
	public final IStatement fbody;
	
	public DefinitionPostBlit(IStatement fbody, SourceRange sourceRange) {
		initSourceRange(sourceRange);
		this.fbody = fbody; parentize(this.fbody);
	}
	
	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, fbody);
		}
		visitor.endVisit(this);
	}
	
}
