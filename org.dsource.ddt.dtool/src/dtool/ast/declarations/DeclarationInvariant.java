package dtool.ast.declarations;

import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.ASTNeoNode;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.SourceRange;
import dtool.ast.statements.BlockStatement;

public class DeclarationInvariant extends ASTNeoNode {

	public final BlockStatement body;
	
	public DeclarationInvariant(BlockStatement body, SourceRange sourceRange) {
		initSourceRange(sourceRange);
		this.body = body; parentize(body);
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, body);
		}
		visitor.endVisit(this);
	}

}
