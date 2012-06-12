package dtool.ast;

import java.util.Iterator;

import dtool.util.ArrayView;

/**
 * A helper class for AST nodes, 
 * used for holding a list of declarations or statements.
 */
public class NodeList  {
	
	public final ArrayView<ASTNeoNode> nodes;
	public final boolean hasCurlies; // Accurate detection not implement yet
	
	public NodeList(ArrayView<ASTNeoNode> nodes, boolean hasCurlies) {
		this.nodes = nodes;
		this.hasCurlies = hasCurlies;
	}
	
	public Iterator<ASTNeoNode> getNodeIterator() {
		return nodes.iterator();
	}
	
	public static NodeList parentizeNodeList(NodeList nodeList, ASTNeoNode parent) {
		parent.parentize(getNodes(nodeList));
		return nodeList;
	}
	
	public static ArrayView<ASTNeoNode> getNodes(NodeList nodeList) {
		if(nodeList == null)
			return null;
		return nodeList.nodes;
	}
	
}