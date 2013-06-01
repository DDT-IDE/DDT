package dtool.ast;

import dtool.util.ArrayView;

/**
 * Utility class for lists of nodes.
 * Has additional info saying if parsing encountered an endingseparator or not;
 */
public class NodeListView<T extends IASTNeoNode> extends ArrayView<T> {
	
	public final boolean hasEndingSeparator;
	
	public NodeListView(T[] array, boolean hasEndingSeparator) {
		super(array);
		this.hasEndingSeparator = hasEndingSeparator;
	}
	
}
