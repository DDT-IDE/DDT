package dtool;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import melnorme.utilbox.misc.ArrayUtil;

/**
 * Stuff that could be added to melnorme:
 */
public class ArrayUtilExt extends ArrayUtil {
	
	/** Removes the last given count elements from given array, creating a new array. */
	public static <T> T[] removeLast(T[] array, int count) {
		assertTrue(array.length >= count);
		T[] newArray = ArrayUtil.copyFrom(array, array.length - count);
		return newArray;
	}
	
}