package dtool.resolver;

import java.util.Iterator;

import dtool.ast.ASTNode;

/**
 * Interface for a node that potentially contains named elements visible 
 * in the same scope/namespace as the container. 
 */
public interface INonScopedContainer {
	
	/** @return an iterator for the members of this {@link INonScopedContainer}. Non-null. 
	 * Used mainly for resolving. */
	Iterator<? extends ASTNode> getMembersIterator();
	
}
