package dtool.ast.declarations;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import descent.internal.compiler.parser.CompoundStatement;
import descent.internal.compiler.parser.Dsymbol;
import descent.internal.compiler.parser.Statement;
import dtool.ast.ASTNeoNode;
import dtool.descentadapter.DescentASTConverter;
import dtool.descentadapter.DescentASTConverter.ASTConversionContext;
import dtool.util.ArrayView;

/**
 * A helper class for AST nodes, 
 * to hold a group of declarations or statements.
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
	
	public static NodeList createNodeList(Statement body, ASTConversionContext convContext) {
		if(body == null)
			return null;
		if(body instanceof CompoundStatement) {
			CompoundStatement cst = (CompoundStatement) body;
			return new NodeList(DescentASTConverter.convertMany(cst.sourceStatements, convContext), true);
		} else {
			return new NodeList(DescentASTConverter.convertMany(Collections.singleton(body), convContext), false);
		}
	}
	
	public static NodeList createNodeList(Collection<Dsymbol> decl, ASTConversionContext convContext) {
		if(decl == null)
			return null;
		return new NodeList(DescentASTConverter.convertMany(decl, convContext), false);
	}
	
	public static ArrayView<ASTNeoNode> getNodes(NodeList nodeList) {
		if(nodeList == null)
			return null;
		return nodeList.nodes;
	}
	
}
