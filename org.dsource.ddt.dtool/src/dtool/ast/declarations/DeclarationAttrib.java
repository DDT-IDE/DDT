package dtool.ast.declarations;

import java.util.Iterator;

import melnorme.utilbox.misc.IteratorUtil;
import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.ASTNeoNode;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.SourceRange;
import dtool.refmodel.INonScopedBlock;

public abstract class DeclarationAttrib extends ASTNeoNode implements INonScopedBlock {
	
	public final NodeList body;
	
	public DeclarationAttrib(NodeList body, SourceRange sourceRange) {
		initSourceRange(sourceRange);
		this.body = body; parentize(this.body);
	}
	
	protected void acceptBodyChildren(IASTNeoVisitor visitor) {
		if(body != null) {
			TreeVisitor.acceptChildren(visitor, body.nodes);
		}
	}
	
	@Override
	public Iterator<ASTNeoNode> getMembersIterator() {
		if(body == null)
			return IteratorUtil.getEMPTY_ITERATOR();
		return body.getNodeIterator();
	}
	
}
