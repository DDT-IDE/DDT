package dtool.ast.declarations;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import descent.internal.compiler.parser.CompoundStatement;
import descent.internal.compiler.parser.Dsymbol;
import descent.internal.compiler.parser.Statement;
import dtool.ast.ASTNeoNode;
import dtool.descentadapter.DescentASTConverter;
import dtool.descentadapter.DescentASTConverter.ASTConversionContext;

/**
 * A helper class for AST nodes, 
 * to hold a group of declarations or statements.
 */
public class NodeList  {
	
	public final ASTNeoNode[] nodes;
	public final boolean hasCurlies; // Accurate detection not implement yet

	private NodeList(ASTNeoNode[] nodes, boolean hasCurlies) {
		this.nodes = nodes;
		this.hasCurlies = hasCurlies;
	}

	public static NodeList createNodeList(Statement body, ASTConversionContext convContext) {
		if(body == null)
			return null;
		if(body instanceof CompoundStatement) {
			CompoundStatement cst = (CompoundStatement) body;
			ASTNeoNode[] neoNodes = DescentASTConverter.convertMany(cst.sourceStatements, convContext);
			return new NodeList(neoNodes, true);
		} else {
			ASTNeoNode[] neoNodes = new ASTNeoNode[] { DescentASTConverter.convertElem(body, convContext) };
			return new NodeList(neoNodes, false);
		}
	}

	public static NodeList createNodeList(Collection<Dsymbol> decl, ASTConversionContext convContext) {
		if(decl == null)
			return null;
		ASTNeoNode[] neoNodes = DescentASTConverter.convertMany(decl, convContext);
		return new NodeList(neoNodes, false);
	}
	
	public static ASTNeoNode[] getNodes(NodeList nodeList) {
		if(nodeList == null)
			return null;
		return nodeList.nodes;
	}


	public Iterator<ASTNeoNode> getNodeIterator() {
		return Arrays.asList(nodes).iterator();
	}

}
