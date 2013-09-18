package dtool.resolver;

import java.util.Iterator;

import dtool.ast.ASTNode;

/**
 * Interface for a container node that contains nodes that are part of the same
 * scope as the container. 
 * (version/debug declarations, attribute declarations, etc.)
 */
public interface INonScopedContainer {
	
	/** @return an iterator for the members of this {@link INonScopedContainer}. Non-null. 
	 * Used mainly for resolving. */
	Iterator<? extends ASTNode> getMembersIterator();
	
}
