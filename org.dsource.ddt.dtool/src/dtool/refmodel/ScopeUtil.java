package dtool.refmodel;

import dtool.ast.IASTNeoNode;
import dtool.ast.definitions.DefinitionClass;

public class ScopeUtil {

	/** Finds the first outer scope of the given element 
	 * (navigating through the element's parents). */
	public static IScopeNode getOuterScope(IASTNeoNode elem) {
		return getScopeNode(elem.getParent());
	}

	/** Finds the first IScopeNode in the given elem chain of parents, 
	 * including elem itself. This corresponds to the innermost lexical
	 * scope available from elem. */
	public static IScopeNode getScopeNode(IASTNeoNode elem) {
	
		while(elem != null) {
			if (elem instanceof IScopeNode)
				return (IScopeNode) elem;
			
			/*BUG here should be:*/
			//if (elem.getParent() instanceof DefinitionAggregate) {
			if (elem.getParent() instanceof DefinitionClass) {
				// Need to skip aggregate defunit scope 
				elem = elem.getParent().getParent();
				continue;
			}
			
			elem = elem.getParent();
		}
		return null;
	}
	
}
