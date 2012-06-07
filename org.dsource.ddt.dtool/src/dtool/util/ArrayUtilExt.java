package dtool.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import melnorme.utilbox.misc.ArrayUtil;

/**
 * Stuff that could be added to melnorme:
 */
public class ArrayUtilExt extends ArrayUtil {
	
	/** This implements a toArray like {@link Collection#toArray(Object[])} */
	public static <T> T[] toArray(Object[] source, T[] a) {
		int size = source.length;
		if (a.length < size) {
			// Make a new array of a's runtime type, but my contents:
			return Arrays.copyOf(source, size, (Class<? extends T[]>) a.getClass());
		}
		System.arraycopy(source, 0, a, 0, size);
		if (a.length > size) {
			a[size] = null;
		}
		return a;
	}
	
	/** This implements a contains like {@link Collection#contains(Object)} */
	public static boolean contains_v2(Object[] source, Object o) {
		if (o == null) {
			for(Object elem : source) {
				if(elem == null)
					return true;
			}
		} else {
			for(Object elem : source) {
				if(o.equals(elem))
					return true;
			}
		}
		return false;
	}
	
	/** This implements a contains like {@link Collection#containsAll(Collection)} */
	public static boolean containsAll(Collection<?> source, Collection<?> c) {
		Iterator<?> citer = c.iterator();
		while (citer.hasNext())
			if (!source.contains(citer.next()))
				return false;
		return true;
	}
	
}