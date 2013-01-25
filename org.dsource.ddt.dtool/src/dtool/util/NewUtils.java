package dtool.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import melnorme.utilbox.core.Assert;
import melnorme.utilbox.misc.ChainedIterator;
import melnorme.utilbox.misc.IteratorUtil;

public class NewUtils {
	
	public static final String[] EMPTY_STRING_ARRAY = new String[0];
	
	/** Shortcut for creating a new {@link ArrayList} */
	public static <T> ArrayList<T> createArrayList(Collection<T> coll) {
		return new ArrayList<T>(coll);
	}
	
	public static <T> Iterator<? extends T> getChainedIterator(
		Iterable<? extends T> iter1, Iterable<? extends T> iter2) {
		if(iter1 == null && iter2 == null)
			return IteratorUtil.getEMPTY_ITERATOR();
		if(iter1 == null)
			return iter2.iterator();
		if(iter2 == null)
			return iter1.iterator();
		
		return new ChainedIterator<T>(iter1.iterator(), iter2.iterator());
	}
	
	public static <T> T assertNotNull_(T obj) {
		Assert.AssertNamespace.assertNotNull(obj);
		return obj;
	}
	
	public static <T> T[] assertNotContainsNull_(T... arr) {
		for (T elem : arr) {
			Assert.AssertNamespace.assertNotNull(elem);
		}
		return arr;
	}

	public static <T> T lastElement(List<T> list) {
		return list.get(list.size()-1);
	}
	
	/** @return a substring of given string starting from the end of the first occurrence 
	 * of given match, or null if no match is found. */
	public static String fromIndexOf(String match, String string) {
		int lastIx = string.indexOf(match);
		return (lastIx == -1) ? null : string.substring(lastIx + match.length());
	}
	
	public static String emptyToNull(String string) {
		if(string.isEmpty()) {
			return null;
		}
		return string;
	}
	
	public static int updateIfNull(int currentValue, int newValue) {
		return currentValue == -1 ? newValue : currentValue;
	}
	
	public static String replaceRegexFirstOccurrence(String str, String regex, int regexGroup, String replacement) {
		Matcher matcher = Pattern.compile(regex).matcher(str);
		if(matcher.find()) {
			int matchIx = matcher.start();
			int matchEndIx = matchIx + matcher.group(regexGroup).length();
			return str.substring(0, matchIx) + replacement + str.substring(matchEndIx, str.length()); 
		} else {
			return str;
		}
	}
	
}