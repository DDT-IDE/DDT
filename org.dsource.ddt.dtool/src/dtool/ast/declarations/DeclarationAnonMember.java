package dtool.ast.declarations;

import java.util.Arrays;
import java.util.Iterator;

import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.ASTNeoNode;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.SourceRange;
import dtool.refmodel.INonScopedBlock;

public class DeclarationAnonMember extends ASTNeoNode implements INonScopedBlock {

	public NodeList body;

	public DeclarationAnonMember(NodeList body, SourceRange sourceRange) {
		initSourceRange(sourceRange);
		this.body = body; parentize(this.body);
	}
	
	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, body.nodes);
		}
		visitor.endVisit(this);
	}
	
	@Override
	public Iterator<ASTNeoNode> getMembersIterator() {
		return Arrays.asList(body.nodes).iterator();
	}

}
