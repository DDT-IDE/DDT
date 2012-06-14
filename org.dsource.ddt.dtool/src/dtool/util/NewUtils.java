package dtool.util;

import java.util.ArrayList;
import java.util.Collection;

public class NewUtils {
	
	/** Shortcut for creating a new {@link ArrayList} */
	public static <T> ArrayList<T> createArrayList(Collection<T> coll) {
		return new ArrayList<T>(coll);
	}
	
}