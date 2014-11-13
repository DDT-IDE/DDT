package dtool.ast.util;

import melnorme.lang.tooling.ast_actual.ASTNode;
import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.Module;

// TODO: need to review and refactor out this code.
public class NodeUtil {

	/** Gets the module of the given ASTNode. */
	public static Module getParentModule(ASTNode elem) {
		// Search for module elem
		while((elem instanceof Module) == false) {
			if(elem == null)
				return null;
			elem = elem.getParent();
		}
		
		return ((Module) elem);
	}
	
	/** @return the outermost DefUnit starting from given node (non-inclusive), or null if not found. */
	public static DefUnit getOuterDefUnit(ASTNode node) {
		node = node.getParent();
		while(true) {
			if (node instanceof DefUnit) {
				return (DefUnit) node;
			}
			if(node == null) {
				return null;
			}
			node = node.getParent();
		}
	}
	
}