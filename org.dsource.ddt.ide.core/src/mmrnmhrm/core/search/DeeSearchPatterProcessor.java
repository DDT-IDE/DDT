package mmrnmhrm.core.search;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;

import melnorme.utilbox.misc.ArrayUtil;
import melnorme.utilbox.misc.StringUtil;

import org.eclipse.dltk.compiler.CharOperation;
import org.eclipse.dltk.core.ISearchPatternProcessor;

public class DeeSearchPatterProcessor implements ISearchPatternProcessor {
	
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
			return extractTypeChars(type).toCharArray();
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
	public String extractTypeChars(String pattern) {
		String simpleName = substringFrom(pattern, TYPE_DELIMITER);
		if (simpleName != null) {
			return simpleName;
		}
		return pattern;
	}
	
	@Override
	public char[] extractTypeQualification(String pattern) {
		char[] rawTypeQualification = extractRawTypeQualification(pattern);
		if(rawTypeQualification == null)
			return null;
		// BM: Have no ideia why this '$' substitution is made
		return CharOperation.replace(rawTypeQualification, TYPE_DELIMITER.toCharArray(),
				new char[] { '$' });
	}
	private char[] extractRawTypeQualification(String pattern) {
		String typeQual = substringUntil(pattern, TYPE_DELIMITER);
		if (typeQual != null) {
			return typeQual.toCharArray();
		}
		return null;
	}
	
	// Field pattern operations
	
}
