package mmrnmhrm.core.search;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;

import melnorme.utilbox.misc.ArrayUtil;
import melnorme.utilbox.misc.StringUtil;

import org.eclipse.dltk.compiler.CharOperation;
import org.eclipse.dltk.core.ISearchPatternProcessor;
import org.eclipse.dltk.core.search.SearchPatternProcessor;

public class DeeSearchPatterProcessor extends SearchPatternProcessor {
	
	public static final DeeSearchPatterProcessor instance = new DeeSearchPatterProcessor();
	
	private static final String TYPE_DELIMITER = ".";
	private static final String METHOD_DELIMITER = ".";
	
	@Override
	public String getDelimiterReplacementString() {
		return TYPE_DELIMITER;
	}
	
	public static String substringUntil(String string, String delimiter) {
		final int pos = string.lastIndexOf(delimiter);
		if (pos != -1) {
			return string.substring(0, pos);
		}
		return null;
	}
	
	public static String substringFrom(String string, String delimiter) {
		final int pos = string.lastIndexOf(delimiter);
		if (pos != -1) {
			return string.substring(pos + delimiter.length());
		}
		return null;
	}
	
	// Method pattern operations
	@Override
	public char[] extractDeclaringTypeQualification(String pattern) {
		String type = substringUntil(pattern, METHOD_DELIMITER);
		if (type != null) {
			return extractTypeQualification(type);
		}
		return null;
	}
	
	@Override
	public char[] extractDeclaringTypeSimpleName(String pattern) {
		String type = substringUntil(pattern, METHOD_DELIMITER);
		if (type != null) {
			return parseType(type).getSimpleName().toCharArray();
		}
		return null;
	}
	
	@Override
	public char[] extractSelector(String pattern) {
		String selector = substringFrom(pattern, METHOD_DELIMITER);
		if(selector == null)
			return pattern.toCharArray();
		return selector.toCharArray();
	}
	
	// Type pattern operations
	@Override
	public ITypePattern parseType(String patternString) {
		final int pos = patternString.lastIndexOf(TYPE_DELIMITER);
		if (pos != -1) {
			return new TypePatten(patternString.substring(0, pos).replace(
					TYPE_DELIMITER, TYPE_SEPARATOR_STR),
					patternString.substring(pos + TYPE_DELIMITER.length()));
		} else {
			return new TypePatten(null, patternString);
		}
	}
	
	// Field pattern operations
	
}
