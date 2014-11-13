package dtool.ast.util;

import melnorme.lang.tooling.ast_actual.ASTNode;
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
	
}