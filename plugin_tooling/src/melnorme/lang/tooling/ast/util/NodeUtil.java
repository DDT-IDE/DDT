package melnorme.lang.tooling.ast.util;

import melnorme.lang.tooling.ast_actual.ASTNode;
import dtool.engine.common.IDeeNamedElement;

public class NodeUtil {

//	/** Gets the module of the given ASTNode. */
//	public static Module getParentModule(ASTNode elem) {
//		// Search for module elem
//		while((elem instanceof Module) == false) {
//			if(elem == null)
//				return null;
//			elem = elem.getParent();
//		}
//		
//		return ((Module)elem);
//	}
	
	/** @return the innermost {@link IDeeNamedElement} containing given node (non-inclusive), or null if not found. */
	public static IDeeNamedElement getOuterDefUnit(ASTNode node) {
		node = node.getParent();
		while(true) {
			if (node instanceof IDeeNamedElement) {
				return (IDeeNamedElement) node;
			}
			if(node == null) {
				return null;
			}
			node = node.getParent();
		}
	}
	
	public static IDeeNamedElement getParentDefUnit(ASTNode node) {
		return getOuterDefUnit(node);
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
	
}