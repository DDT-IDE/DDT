package dtool.resolver;

import java.util.Iterator;

import dtool.ast.ASTNode;

/**
 * Interface for a container node that contains nodes that are part of the same
 * scope as the container. 
 * (version/debug declarations, attribute declarations, etc.)
 */
public interface INonScopedBlock { // FIXME: some nodes implement this and they should not.
	
	/** @return an iterator for the members of this {@link INonScopedBlock}. Non-null. */
	Iterator<? extends ASTNode> getMembersIterator();
	
}
