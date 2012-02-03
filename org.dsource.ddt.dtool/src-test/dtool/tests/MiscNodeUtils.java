package dtool.tests;

import descent.internal.compiler.parser.ast.IASTNode;
import dtool.ast.definitions.DefUnit;

public class MiscNodeUtils {

	public static DefUnit getDefUniFromScope(IASTNode[] children, String defUnitName) {
		for (IASTNode node : children) {
			if(node instanceof DefUnit) {
				DefUnit defUnit = (DefUnit) node;
				if(defUnit.getName().equals(defUnitName)) {
					return defUnit;
				}
			}
		}
		return null;
	}
	
}
