package dtool.tests;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import melnorme.utilbox.core.Function;
import dtool.ast.ASTNeoNode;


public class MiscDeeTestUtils {
	
	public static Function<String, String> fnStringToSubString(final int index) {
		return new Function<String, String>() {
			@Override
			public String evaluate(String obj) {
				return (obj == null || index > obj.length() )? null : obj.substring(index);
			}
		};
	}
	
	public static Function<ASTNeoNode, String> fnDefUnitToStringAsElement(final int prefixLen) {
		return new Function<ASTNeoNode, String>() {
			@Override
			public String evaluate(ASTNeoNode obj) {
				return obj == null ? null : obj.toStringAsElement().substring(prefixLen);
			}
		};
	}

	public static Matcher matchRegexp(String regex, String input, int start) {
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(input);
		m.find(start);
		return m;
	}
	
}