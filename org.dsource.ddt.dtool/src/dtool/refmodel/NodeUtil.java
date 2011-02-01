package dtool.refmodel;

import melnorme.utilbox.tree.IElement;
import descent.internal.compiler.parser.ast.IASTNode;
import dtool.ast.ASTNeoNode;
import dtool.ast.definitions.BaseClass;
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

	/** Finds the first outer scope of the given element 
	 * (navigating through the element's parents). */
	public static IScopeNode getOuterScope(IASTNode elem) {
		return getScopeNode(elem.getParent());
	}
	
	/** Finds the first IScopeNode in the given elem chain of parents, 
	 * including elem itself. This corresponds to the innermost lexical
	 * scope available from elem. */
	public static IScopeNode getScopeNode(IElement elem) {

		while(elem != null) {
			if (elem instanceof IScopeNode)
				return (IScopeNode) elem;
			
			if (elem instanceof BaseClass) {
				// Skip aggregate defunit scope (this is important) 
				elem = elem.getParent().getParent();
				continue;
			}
			
			elem = elem.getParent();
		}
		return null;
	}

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
