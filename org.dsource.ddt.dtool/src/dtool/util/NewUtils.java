package dtool.util;

import java.util.ArrayList;
import java.util.Collection;

public class NewUtils {
	
	public static final String[] EMPTY_STRING_ARRAY = new String[0];
	
	/** Shortcut for creating a new {@link ArrayList} */
	public static <T> ArrayList<T> createArrayList(Collection<T> coll) {
		return new ArrayList<T>(coll);
	}
	
}