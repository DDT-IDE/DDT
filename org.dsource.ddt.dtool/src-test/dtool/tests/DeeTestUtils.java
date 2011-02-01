package dtool.tests;

import melnorme.utilbox.core.Function;
import dtool.ast.ASTNeoNode;


public class DeeTestUtils extends CommonTestUtils {
	
	public static final boolean UNSUPPORTED_FUNCTIONALITY_MARKER = false;
	
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
	
	public DeeTestUtils() {
		super();
	}
	
}