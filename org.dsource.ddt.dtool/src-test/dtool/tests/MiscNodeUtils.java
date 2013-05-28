package dtool.tests;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import descent.internal.compiler.parser.ast.IASTNode;
import dtool.ast.ASTNode;
import dtool.ast.NodeList;
import dtool.ast.definitions.DefUnit;

public class MiscNodeUtils {

	public static DefUnit getDefUniFromScope(IASTNode[] children, String defUnitName) {
		for (IASTNode node : children) {
			if(node instanceof DefUnit) {
				DefUnit defUnit = (DefUnit) node;
				if(defUnit.getName().equals(defUnitName)) {
					return defUnit;
				}
			}
		}
		return null;
	}
	
	public static ASTNode getNodeFromTreePath(ASTNode node, int... treePath) {
		return getNodeFromTreePath(node, true, treePath);
	}
	public static ASTNode getNodeFromTreePath(ASTNode node, boolean ignoreNodeList, int... treePath) {
		for (int pathIx = 0; pathIx < treePath.length; pathIx++) {
			int childIx = treePath[pathIx];
			
			ASTNode[] children = node.getChildren();
			if(ignoreNodeList && children.length == 1 && children[0] instanceof NodeList) {
				children = ((NodeList<?>)children[0]).getChildren();
			}
			
			assertTrue(children.length > childIx);
			node = children[childIx];
		}
		return node;
	}
	
	public static ASTNode getLeftMostChild(ASTNode node) {
		ASTNode[] children = node.getChildren();
		if(children.length == 0) {
			return node;
		} else {
			return getLeftMostChild(children[0]);
		}
	}
	
}