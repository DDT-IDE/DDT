package dtool.ast.declarations;

import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.ASTNeoNode;
import dtool.ast.IASTVisitor;
import dtool.ast.statements.BlockStatement;

public class DeclarationUnitTest extends ASTNeoNode {
	
	public final BlockStatement body;
	
	public DeclarationUnitTest(BlockStatement body) {
		this.body = parentize(body);
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, body);
		}
		visitor.endVisit(this);
	}
	
}