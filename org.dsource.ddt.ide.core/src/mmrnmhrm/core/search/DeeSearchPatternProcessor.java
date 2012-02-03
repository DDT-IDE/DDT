package mmrnmhrm.core.search;

import org.eclipse.dltk.core.search.SearchPatternProcessor;

public class DeeSearchPatternProcessor extends SearchPatternProcessor {
	
	public static final DeeSearchPatternProcessor instance = new DeeSearchPatternProcessor();
	
	private static final String TYPE_DELIMITER = ".";
	private static final char METHOD_DELIMITER = '.';
	
	@Override
	public String getDelimiterReplacementString() {
		return TYPE_DELIMITER;
	}
	
	public static String substringUntil(String string, char delimiter) {
		final int pos = string.lastIndexOf(delimiter);
		if (pos != -1) {
			return string.substring(0, pos);
		}
		return null;
	}
	
	public static String substringFrom(String string, char delimiter) {
		final int pos = string.lastIndexOf(delimiter);
		if (pos != -1) {
			return string.substring(pos + 1);
		}
		return null;
	}
	
	// Method pattern operations
	@Override
	public char[] extractDeclaringTypeQualification(String pattern) {
		String type = substringUntil(pattern, METHOD_DELIMITER);
		if (type != null) {
			return parseType(type).qualification();
		}
		return null;
	}
	
	@Override
	public char[] extractDeclaringTypeSimpleName(String pattern) {
		String type = substringUntil(pattern, METHOD_DELIMITER);
		if (type != null) {
			return parseType(type).simpleName();
		}
		return null;
	}
	
	@Override
	public char[] extractSelector(String pattern) {
		String selector = substringFrom(pattern, METHOD_DELIMITER);
		if(selector == null) {
			return pattern.toCharArray();
		}
		return selector.toCharArray();
	}
	
	// Type pattern operations
	@Override
	public ITypePattern parseType(String patternString) {
		final int pos = patternString.lastIndexOf(TYPE_DELIMITER);
		if (pos != -1) {
			// In the internal representation of qualications for patterns, the type delimiter is '$',
			// so was to not confuse with the wildcard '.'
			String qualification = patternString.substring(0, pos).replace(TYPE_DELIMITER, TYPE_SEPARATOR_STR);
			String simpleName = patternString.substring(pos + TYPE_DELIMITER.length());
			return new TypePatten(qualification,simpleName);
		} else {
			return new TypePatten(null, patternString);
		}
	}
	
	// Field pattern operations
	
}
