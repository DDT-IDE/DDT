package dtool.tests;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import melnorme.utilbox.misc.ArrayUtil;
import dtool.ASTNodeSearcher;
import dtool.ast.ASTNode;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTNode;
import dtool.ast.NodeList;
import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.Module;

/**
 * Miscellaneous quick and dirty node utils. 
 * These might not be well tested nor have good performance, so use in tests only. 
 */
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
			if(ignoreNodeList) {
				node = getChildFlattenNodelist(childIx, children);
			} else {
				assertTrue(children.length > childIx);
				node = children[childIx];
			}
		}
		return node;
	}
	
	public static ASTNode getChildFlattenNodelist(int childIx, ASTNode[] children) {
		
		int flattenedTraversedIndex = 0;
		for (int ix = 0; ix < children.length; ix++) {
			ASTNode node = children[ix];
			if(node instanceof NodeList) {
				NodeList<?> nodeList = (NodeList<?>) node;
				if(childIx >= flattenedTraversedIndex + nodeList.nodes.size()) {
					flattenedTraversedIndex += nodeList.nodes.size();
					continue;
				} else {
					return nodeList.nodes.get(childIx - flattenedTraversedIndex);
				}
			} else {
				if(childIx == flattenedTraversedIndex)
					return node;
				flattenedTraversedIndex++;
			}
		}
		throw assertFail();
	}
	
	public static ASTNode getLeftMostChild(ASTNode node) {
		ASTNode[] children = node.getChildren();
		if(children.length == 0) {
			return node;
		} else {
			return getLeftMostChild(children[0]);
		}
	}
	
	public static DefUnit searchDefUnit(ASTNode node, final String defName, final ASTNodeTypes... typesToDescend) {
		
		ASTNodeSearcher<DefUnit> searcher = new ASTNodeSearcher<DefUnit>() {
			
			@Override
			public boolean doPreVisit(ASTNode node) {
				if(node instanceof DefUnit) {
					DefUnit defUnit = (DefUnit) node;
					if(defUnit.getName().equals(defName)) {
						match = defUnit;
						return continueSearch = false;
					}
				}
				
				if(node instanceof NodeList || node instanceof Module || 
					ArrayUtil.contains(typesToDescend, node.getNodeType())) {
					return true;
				}
				return false;
			}
		};
		node.accept(searcher);
		return searcher.match;
	}
	
}