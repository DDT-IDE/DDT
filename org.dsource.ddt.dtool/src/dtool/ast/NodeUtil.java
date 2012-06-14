package dtool.ast;

import melnorme.utilbox.tree.IElement;
import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.Module;

public class NodeUtil {

	/** Gets the module of the given ASTNode. */
	public static Module getParentModule(ASTNeoNode elem) {
		// Search for module elem
		while((elem instanceof Module) == false) {
			if(elem == null)
				return null;
			elem = elem.getParent();
		}
		
		return ((Module)elem);
	}
	
	/** @return the outermost DefUnit starting from given node (non-inclusive), or null if not found. */
	public static DefUnit getOuterDefUnit(ASTNeoNode node) {
		IElement elem = node.getParent();
		while(elem != null) {
			if (elem instanceof DefUnit)
				return (DefUnit) elem;
			elem = elem.getParent();
		}
		return null;
	}
	
}