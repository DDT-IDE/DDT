package dtool.tests;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import melnorme.utilbox.core.Function;
import dtool.ast.ASTNeoNode;
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
	
	public static Function<DefUnit, String> fnDefUnitToStringAsElement(final int prefixLen) {
		return new Function<DefUnit, String>() {
			@Override
			public String evaluate(DefUnit obj) {
				return obj == null ? null : obj.toStringAsElement().substring(prefixLen);
			}
		};
	}
	
	public static Function<ASTNeoNode, String> fnDefUnitToStringAsCode() {
		return new Function<ASTNeoNode, String>() {
			@Override
			public String evaluate(ASTNeoNode obj) {
				return obj == null ? null : obj.toStringAsCode();
			}
		};
	}
	
	public static ASTNeoNode getSingleChild(ASTNeoNode[] array) {
		assertTrue(array != null && array.length == 1);
		return array[0];
	}
	
}