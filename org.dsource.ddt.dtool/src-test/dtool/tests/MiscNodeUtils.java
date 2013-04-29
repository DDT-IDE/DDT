package dtool.tests;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import melnorme.utilbox.core.CoreUtil;
import descent.internal.compiler.parser.ast.IASTNode;
import dtool.ast.ASTNeoNode;
import dtool.ast.NodeList2;
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
	
	public static ASTNeoNode getNodeFromTreePath(ASTNeoNode node, int... treePath) {
		return getNodeFromTreePath(node, true, treePath);
	}
	public static ASTNeoNode getNodeFromTreePath(ASTNeoNode node, boolean ignoreNodeList, int... treePath) {
		for (int pathIx = 0; pathIx < treePath.length; pathIx++) {
			int childIx = treePath[pathIx];
			
			ASTNeoNode[] children = node.getChildren();
			if(ignoreNodeList && children.length == 1 && children[0] instanceof NodeList2) {
				children = CoreUtil.<NodeList2<ASTNeoNode>>blindCast(children[0]).getChildren();
			}
			
			assertTrue(children.length > childIx);
			node = children[childIx];
		}
		return node;
	}
	
}