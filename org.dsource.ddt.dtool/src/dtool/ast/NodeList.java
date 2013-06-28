package dtool.ast;

import static dtool.util.NewUtils.assertNotNull_;

import java.util.Iterator;
import java.util.List;

import melnorme.utilbox.misc.IteratorUtil;
import melnorme.utilbox.tree.TreeVisitor;
import dtool.resolver.IScope;
import dtool.resolver.IScopeNode;
import dtool.resolver.api.IModuleResolver;
import dtool.util.ArrayView;

public abstract class NodeList<E extends ASTNode> extends ASTNode implements IScopeNode {
	
	public final ArrayView<E> nodes;
	
	protected NodeList(ArrayView<E> nodes) {
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
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.appendList(nodes, "\n", true);
	}
	
	@Override
	public Iterator<? extends ASTNode> getMembersIterator(IModuleResolver moduleResolver) {
		return nodes.iterator();
	}
	
	@Override
	public List<IScope> getSuperScopes(IModuleResolver moduleResolver) {
		return null;
	}
	
	@Override
	public boolean hasSequentialLookup() {
		return false;
	}
	
	public static Iterator<? extends ASTNode> getBodyMembersIterator(NodeList<? extends ASTNode> body) {
		return body == null ? IteratorUtil.<ASTNode>getEMPTY_ITERATOR() : body.nodes.iterator();
	}
	
}