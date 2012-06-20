package dtool.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import melnorme.utilbox.misc.ChainedIterator;
import melnorme.utilbox.misc.IteratorUtil;

public class NewUtils {
	
	public static final String[] EMPTY_STRING_ARRAY = new String[0];
	
	/** Shortcut for creating a new {@link ArrayList} */
	public static <T> ArrayList<T> createArrayList(Collection<T> coll) {
		return new ArrayList<T>(coll);
	}
	
	public static <T> Iterator<? extends T> getChainedIterator(Iterable<? extends T> iter1, Iterable<? extends T> iter2) {
		if(iter1 == null && iter2 == null)
			return IteratorUtil.getEMPTY_ITERATOR();
		if(iter1 == null)
			return iter2.iterator();
		if(iter2 == null)
			return iter1.iterator();
		
		return new ChainedIterator<T>(iter1.iterator(), iter2.iterator());
	}
	
}