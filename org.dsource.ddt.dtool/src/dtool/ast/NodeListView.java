package dtool.ast;

import java.util.Iterator;

import melnorme.utilbox.misc.IteratorUtil;
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
	
	public static <T> Iterator<T> getIteratorSafe(Iterable<T> nodeList) {
		return nodeList == null ? IteratorUtil.<T>getEMPTY_ITERATOR() : nodeList.iterator();
	}
	
}
