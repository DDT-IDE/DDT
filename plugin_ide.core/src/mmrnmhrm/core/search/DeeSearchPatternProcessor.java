package mmrnmhrm.core.search;

import org.eclipse.dltk.core.search.SearchPatternProcessor;

public class DeeSearchPatternProcessor extends SearchPatternProcessor {
	
	public static final DeeSearchPatternProcessor instance = new DeeSearchPatternProcessor();
	
	private static final char NAME_DELIMITER = '.';
	private static final String NAME_DELIMITER_STR = String.valueOf(NAME_DELIMITER);
	private static final char METHOD_PARAM_DELIMITER = '(';
	
	@Override
	public String getDelimiterReplacementString() {
		return NAME_DELIMITER_STR;
	}
	
	public static String substringUntil(String string, char delimiter) {
		return substringUntil(string, delimiter, false);
	}
	public static String substringUntil(String string, char delimiter, boolean endAlsoDelimits) {
		final int pos = string.lastIndexOf(delimiter);
		if (pos != -1) {
			return string.substring(0, pos);
		}
		return endAlsoDelimits ? string : null;
	}
	
	public static String substringFromLast(String string, char delimiter) {
		return substringFromLast(string, delimiter, false);
	}
	public static String substringFromLast(String string, char delimiter, boolean startAlsoDelimits) {
		final int pos = string.lastIndexOf(delimiter);
		if (pos != -1) {
			return string.substring(pos + 1);
		}
		return startAlsoDelimits ? string : null;
	}
	
	// Method pattern operations
	@Override
	public char[] extractDeclaringTypeQualification(String pattern) {
		String type = substringUntil(pattern, NAME_DELIMITER);
		if (type != null) {
			return parseType(type).qualification();
		}
		return null;
	}
	
	@Override
	public char[] extractDeclaringTypeSimpleName(String pattern) {
		String type = substringUntil(pattern, NAME_DELIMITER);
		if (type != null) {
			return parseType(type).simpleName();
		}
		return null;
	}
	
	@Override
	public char[] extractSelector(String pattern) {
		String fqNamepattern = substringUntil(pattern, METHOD_PARAM_DELIMITER, true);
		String selector = substringFromLast(fqNamepattern, NAME_DELIMITER, true);
		return selector.toCharArray();
	}
	
	// Type pattern operations
	@Override
	public ITypePattern parseType(String patternString) {
		final int pos = patternString.lastIndexOf(NAME_DELIMITER);
		if (pos != -1) {
			// In the internal representation of qualications for patterns, the type delimiter is '$',
			// so was to not confuse with the wildcard '.'
			String qualification = patternString.substring(0, pos).replace(NAME_DELIMITER, TYPE_SEPARATOR);
			String simpleName = patternString.substring(pos + 1);
			return new TypePatten(qualification,simpleName);
		} else {
			return new TypePatten(null, patternString);
		}
	}
	
	// Field pattern operations
	
}
