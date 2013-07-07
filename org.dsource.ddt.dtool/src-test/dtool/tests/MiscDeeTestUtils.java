package dtool.tests;

import java.util.regex.Pattern;

import melnorme.utilbox.core.Function;
import dtool.ast.ASTNode;
import dtool.ast.definitions.DefUnit;


public class MiscDeeTestUtils {
	
	public static Function<String, String> fnStringToSubString(final int index) {
		return new Function<String, String>() {
			@Override
			public String evaluate(String obj) {
				return (obj == null || index > obj.length() )? null : obj.substring(index);
			}
		};
	}
	
	public static Function<DefUnit, String> fnDefUnitToStringAsName(final int prefixLen) {
		return new Function<DefUnit, String>() {
			@Override
			public String evaluate(DefUnit obj) {
				return obj == null ? null : obj.getName().substring(prefixLen);
			}
		};
	}
	
	public static Function<DefUnit, String> fnDefUnitToStringAsElement(final int prefixLen) {
		return new Function<DefUnit, String>() {
			@Override
			public String evaluate(DefUnit obj) {
				return obj == null ? null : obj.toStringAsElement().substring(prefixLen);
			}
		};
	}
	
	public static Function<ASTNode, String> fnDefUnitToStringAsCode() {
		return new Function<ASTNode, String>() {
			@Override
			public String evaluate(ASTNode obj) {
				return obj == null ? null : obj.toStringAsCode();
			}
		};
	}
	
	public static final Pattern LINE_SPLITTER = Pattern.compile("\n|(\r\n)|\r");
	
	public static String[] splitLines(String exclusionsFile) {
		return LINE_SPLITTER.split(exclusionsFile);
	}
	
}