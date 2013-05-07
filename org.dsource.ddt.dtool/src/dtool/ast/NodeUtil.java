package dtool.ast;

import melnorme.utilbox.tree.IElement;
import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.Module;
import dtool.parser.Token;
import dtool.util.ArrayView;

public class NodeUtil {

	/** Gets the module of the given ASTNode. */
	public static Module getParentModule(ASTNode elem) {
		// Search for module elem
		while((elem instanceof Module) == false) {
			if(elem == null)
				return null;
			elem = elem.getParent();
		}
		
		return ((Module)elem);
	}
	
	/** @return the outermost DefUnit starting from given node (non-inclusive), or null if not found. */
	public static DefUnit getOuterDefUnit(ASTNode node) {
		IElement elem = node.getParent();
		while(elem != null) {
			if (elem instanceof DefUnit)
				return (DefUnit) elem;
			elem = elem.getParent();
		}
		return null;
	}
	
	public static boolean isContainedIn(ASTNode node, ASTNode container) {
		while(node != null) {
			if(node == container) {
				return true;
			}
			node = node.getParent();
		}
		return false;
	}
	
	public static String[] tokenArrayToStringArray(ArrayView<Token> tokenArray) {
		String[] stringArray = new String[tokenArray.size()];
		for (int i = 0; i < stringArray.length; i++) {
			stringArray[i] = tokenArray.get(i).source;
		}
		return stringArray;
	}
	
	public static String getSubString(String string, SourceRange sourceRange) {
		return string.subSequence(sourceRange.getStartPos(), sourceRange.getEndPos()).toString();
	}
	
}