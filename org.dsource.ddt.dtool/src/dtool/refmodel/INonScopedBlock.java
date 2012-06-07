package dtool.refmodel;

import java.util.Iterator;

import dtool.ast.ASTNeoNode;

/**
 * Interface for a container node that contains nodes that are part of the same
 * scope as the container. 
 * (version/debug declarations, attribute declarations, etc.)
 */
public interface INonScopedBlock { // FIXME: some nodes implement this and they should not.
	
	/** Gets the members of this NonScopedBlock.
	 * Note: It is not guaranteed that the returned nodes direct parent is 
	 * this NonScopedBlock (due to DeclarationStaticIfIsType). */
	Iterator<? extends ASTNeoNode> getMembersIterator();
}
