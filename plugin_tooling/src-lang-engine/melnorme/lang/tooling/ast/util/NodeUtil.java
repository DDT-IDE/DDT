package melnorme.lang.tooling.ast.util;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import melnorme.lang.tooling.ast.ISemanticElement;
import melnorme.lang.tooling.symbols.INamedElement;

public class NodeUtil {
	
	/** @return the innermost {@link INamedElement} containing given node (non-inclusive), or null if not found. */
	public static INamedElement getOuterNamedElement(ISemanticElement node) {
		node = node.getParent();
		while(true) {
			if (node instanceof INamedElement) {
				return (INamedElement) node;
			}
			if(node == null) {
				return null;
			}
			node = node.getParent();
		}
	}
	
	public static INamedElement getParentDefUnit(ISemanticElement node) {
		return getOuterNamedElement(node);
	}
	
	public static boolean isContainedIn(ISemanticElement node, ISemanticElement container) {
		while(node != null) {
			if(node == container) {
				return true;
			}
			node = node.getParent();
		}
		return false;
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T getMatchingParent(ISemanticElement node, Class<T> klass) {
		assertNotNull(klass);
		
		if(node == null) {
			return null;
		}
		
		if(klass.isInstance(node)) {
			return (T) node;
		}
		return getMatchingParent(node.getParent(), klass);
	}
	
}