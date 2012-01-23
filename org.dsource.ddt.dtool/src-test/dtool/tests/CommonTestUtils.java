package dtool.tests;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import melnorme.utilbox.core.Assert;
import melnorme.utilbox.core.CoreUtil;
import melnorme.utilbox.core.Function;
import melnorme.utilbox.misc.ArrayUtil;
import melnorme.utilbox.misc.CollectionUtil;
import melnorme.utilbox.misc.StringUtil;

/**
 * A base class for common, miscellaneous test utils. 
 * Some of this stuff may later be refactored into the common utils code (ie, into melnorme/utilbox)
 */
public class CommonTestUtils {
	
	public static boolean TRUE() {
		return true;
	}
	
	public static <T, U extends T> void assertEquals(T obj1, U obj2) {
		Assert.equals(obj1, obj2);
	}
	
	public static <T, U extends T> void assertAreEqual(T obj1, U obj2) {
		assertTrue(CoreUtil.areEqual(obj1, obj2));
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T assertCast(Object object, Class<T> klass) {
		assertTrue(object == null || klass.isInstance(object));
		return (T) object;
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T assertInstance(Object object, Class<T> klass) {
		assertTrue(klass.isInstance(object));
		return (T) object;
	}
	
	/** Assert that the given arrays are equal according to Arrays.equals().
	 *  (equal content-wise, order relevant) */
	public static void assertEqualArrays(Object[] arr1, Object[] arr2) {
		assertTrue(Arrays.equals(arr1, arr2));
	}
	
	public static <T> void assertContains(T[] array, T obj) {
		assertTrue(ArrayUtil.contains(array, obj));
	}
	
	public static void assertEqualSet(Set<?> result, Set<?> expected) {
		boolean equals = result.equals(expected);
		if(equals) {
			return;
		}
		HashSet<?> resultExtra = removeAllCopy(result, expected);
		HashSet<?> expectedExtra = removeAllCopy(expected, result);
		assertTrue(equals,
				"Obtained result set not equal to expected set. \n" +
				"--- Extra elements in result set ("+resultExtra.size()+") : --- \n" +
				StringUtil.collToString(resultExtra, "\n") + "\n" +
				"--- Extra elements in expected set ("+expectedExtra.size()+") : --- \n" +
				StringUtil.collToString(expectedExtra, "\n") + "\n" +
				"== -- =="
		);
	}
	
	/* -------------------------- */
	
	public static <T> T[] array(T... elems) {
		return elems;
	}
	
	public static int[] array(int... elems) {
		return elems;
	}
	
	public static Object[] array() {
		return new Object[0];  // Create empty array
	}
	
	public static String[] arrayString() {
		return new String[0]; // Create empty array
	}
	
	public static <T> List<T> list(T... elems) {
		return Arrays.asList(elems);
	}
	
	public static <T> HashSet<T> hashSet(T... elems) {
		return new HashSet<T>(Arrays.asList(elems));
	}
	
	public static <T> Set<T> unmodifiable(Set<T> set) {
		return Collections.unmodifiableSet(set);
	}
	
	public static <T> List<T> unmodifiable(List<T> set) {
		return Collections.unmodifiableList(set);
	}
	
	public static <T> Collection<T> unmodifiable(Collection<T> set) {
		return Collections.unmodifiableCollection(set);
	}
	
	/** ---- **/
	
	
	public static <T> HashSet<T> retainAllCopy(Set<T> set, Collection<?> retainColl) {
		HashSet<T> setCopy = CollectionUtil.createHashSet(set);
		setCopy.retainAll(retainColl);
		return setCopy;
	}

	public static <T> HashSet<T> removeAllCopy(Set<T> set, Collection<?> removeColl) {
		HashSet<T> setCopy = CollectionUtil.createHashSet(set);
		setCopy.removeAll(removeColl);
		return setCopy;
	}
	
	public static <T> String[] strmap(Collection<T> coll, Function<? super T, String> evalFunction) {
		return ArrayUtil.map(coll, evalFunction, String.class);
	}
	
	public static <T, RE, C extends Collection<RE>> C mapOut(Collection<T> coll, Function<? super T, ? extends RE> evalFunction, C outColl) {
		for(T elem : coll) {
			outColl.add(evalFunction.evaluate(elem));
		}
		return outColl;
	}
	
	public static <T extends Exception> void throwIf(boolean condition, String message) throws RuntimeException {
		if(condition) {
			throw new RuntimeException(message);
		}
	}
	
}
