package dtool.ast;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import dtool.resolver.CommonDefUnitSearch;
import dtool.resolver.IScopeNode;
import dtool.resolver.ReferenceResolver;
import dtool.util.ArrayView;

public abstract class NodeList<E extends ASTNode> extends ASTNode implements IScopeNode {
	
	public final ArrayView<E> nodes;
	
	protected NodeList(ArrayView<E> nodes) {
		this.nodes = parentize(assertNotNull(nodes));
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
		acceptVisitor(visitor, nodes);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.appendList(nodes, "\n", true);
	}
	
	@Override
	public void resolveSearchInScope(CommonDefUnitSearch search) {
		ReferenceResolver.findInNodeList(search, nodes, false);
	}
	
}