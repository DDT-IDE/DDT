package mmrnmhrm.lang.core.search;

import java.util.regex.Pattern;

import org.eclipse.dltk.compiler.CharOperation;
import org.eclipse.dltk.core.search.SearchPattern;

public class CommonLangPatternMatcher {
	
	/* DTLK: copied coded 3.0 */
	
	/* match levels */
	public static final int IMPOSSIBLE_MATCH = 0;
	public static final int INACCURATE_MATCH = 1;
	public static final int POSSIBLE_MATCH = 2;
	public static final int ACCURATE_MATCH = 3;
	public static final int ERASURE_MATCH = 4;
	// Possible rule match flavors
	// see bug https://bugs.eclipse.org/bugs/show_bug.cgi?id=79866
	protected static final int POSSIBLE_FULL_MATCH = POSSIBLE_MATCH
			| (SearchPattern.R_FULL_MATCH << 16);
	protected static final int POSSIBLE_PREFIX_MATCH = POSSIBLE_MATCH
			| (SearchPattern.R_PREFIX_MATCH << 16);
	protected static final int POSSIBLE_PATTERN_MATCH = POSSIBLE_MATCH
			| (SearchPattern.R_PATTERN_MATCH << 16);
	protected static final int POSSIBLE_REGEXP_MATCH = POSSIBLE_MATCH
			| (SearchPattern.R_REGEXP_MATCH << 16);
	protected static final int POSSIBLE_CAMELCASE_MATCH = POSSIBLE_MATCH
			| (SearchPattern.R_CAMELCASE_MATCH << 16);
	
	
	@SuppressWarnings("restriction")
	public static final int MATCH_MODE_MASK = 
	org.eclipse.dltk.internal.core.search.matching.DLTKSearchPattern.MATCH_MODE_MASK;

	
	
	// store pattern info
	protected final int matchMode;
	protected final boolean isCaseSensitive;
	protected final boolean isCamelCase;
	
	protected Pattern compiledPattern;

	public CommonLangPatternMatcher(SearchPattern pattern) {
		int matchRule = pattern.getMatchRule();
		this.isCaseSensitive = (matchRule & SearchPattern.R_CASE_SENSITIVE) != 0;
		this.isCamelCase = (matchRule & SearchPattern.R_CAMELCASE_MATCH) != 0;
		this.matchMode = matchRule & MATCH_MODE_MASK;
	}
	
	/**
	 * Returns whether the given name matches the given pattern.
	 */
	public boolean matchesName(char[] pattern, char[] name) {
		return matchNameValue(pattern, name) != IMPOSSIBLE_MATCH;
	}
	
	/**
	 * Return how the given name matches the given pattern.
	 * 
	 * @see "https://bugs.eclipse.org/bugs/show_bug.cgi?id=79866"
	 * 
	 * @param pattern
	 * @param name
	 * @return Possible values are:
	 *         <ul>
	 *         <li>{@link #POSSIBLE_FULL_MATCH}: Given name is equals to pattern
	 *         </li>
	 *         <li>{@link #POSSIBLE_PREFIX_MATCH}: Given name prefix equals to
	 *         pattern</li>
	 *         <li>{@link #POSSIBLE_CAMELCASE_MATCH}: Given name matches pattern
	 *         as Camel Case</li>
	 *         <li>{@link #POSSIBLE_PATTERN_MATCH}: Given name matches pattern
	 *         as Pattern (ie. using '*' and '?' characters)</li>
	 *         </ul>
	 */
	public int matchNameValue(char[] pattern, char[] name) {
		if (pattern == null)
			return ACCURATE_MATCH; // null is as if it was "*"
		if (name == null)
			return IMPOSSIBLE_MATCH; // cannot match null name
		if (name.length == 0) { // empty name
			if (pattern.length == 0) { // can only matches empty pattern
				return ACCURATE_MATCH;
			}
			return IMPOSSIBLE_MATCH;
		} else if (pattern.length == 0) {
			return IMPOSSIBLE_MATCH; // need to have both name and pattern
			// length==0 to be accurate
		}
		boolean matchFirstChar = !this.isCaseSensitive || pattern[0] == name[0];
		boolean sameLength = pattern.length == name.length;
		boolean canBePrefix = name.length >= pattern.length;
		if (this.isCamelCase && matchFirstChar
				&& CharOperation.camelCaseMatch(pattern, name)) {
			return POSSIBLE_CAMELCASE_MATCH;
		}
		switch (this.matchMode) {
		case SearchPattern.R_EXACT_MATCH:
			if (!this.isCamelCase) {
				if (sameLength
						&& matchFirstChar
						&& CharOperation.equals(pattern, name,
								this.isCaseSensitive)) {
					return POSSIBLE_FULL_MATCH;
				}
				break;
			}
			// fall through next case to match as prefix if camel case
			// failed
		case SearchPattern.R_PREFIX_MATCH:
			if (canBePrefix
					&& matchFirstChar
					&& CharOperation.prefixEquals(pattern, name,
							this.isCaseSensitive)) {
				return POSSIBLE_PREFIX_MATCH;
			}
			break;
		case SearchPattern.R_PATTERN_MATCH:
			if (!this.isCaseSensitive) {
				pattern = CharOperation.toLowerCase(pattern);
			}
			if (CharOperation.match(pattern, name, this.isCaseSensitive)) {
				return POSSIBLE_MATCH;
			}
			break;
		case SearchPattern.R_REGEXP_MATCH:
			if (compiledPattern == null) {
				compiledPattern = Pattern.compile(new String(pattern),
						this.isCaseSensitive ? 0 : Pattern.CASE_INSENSITIVE);
			}
			if (compiledPattern.matcher(new String(name)).matches()) {
				return POSSIBLE_REGEXP_MATCH;
			}
			break;
		}
		return IMPOSSIBLE_MATCH;
	}
}
