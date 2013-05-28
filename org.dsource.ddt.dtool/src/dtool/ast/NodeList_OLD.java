package dtool.ast;

import static dtool.util.NewUtils.assertNotNull_;

import java.util.Iterator;

import melnorme.utilbox.misc.IteratorUtil;
import melnorme.utilbox.tree.TreeVisitor;
import dtool.util.ArrayView;

@Deprecated
public class NodeList_OLD<E extends ASTNode> extends ASTNode {
	
	public final ArrayView<E> nodes;
	
	public NodeList_OLD(ArrayView<E> nodes) {
		this.nodes = parentize(assertNotNull_(nodes));
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, nodes);
		}
		visitor.endVisit(this);
	}
	
	public static Iterator<? extends ASTNode> getMembersIterator(NodeList_OLD<? extends ASTNode> body) {
		return body == null ? IteratorUtil.<ASTNode>getEMPTY_ITERATOR() : body.nodes.iterator();
	}
	
}