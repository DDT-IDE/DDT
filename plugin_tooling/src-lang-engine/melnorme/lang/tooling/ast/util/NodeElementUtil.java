/*******************************************************************************
 * Copyright (c) 2014, 2014 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package melnorme.lang.tooling.ast.util;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import melnorme.lang.tooling.ast.ISemanticElement;
import melnorme.lang.tooling.symbols.INamedElement;
import melnorme.utilbox.core.CoreUtil;

public class NodeElementUtil {
	
	public static boolean isContainedIn(ISemanticElement node, ISemanticElement container) {
		while(node != null) {
			if(node == container) {
				return true;
			}
			node = node.getParent();
		}
		return false;
	}
	
	/** @return the innermost node in the parent chain of given node (inclusive), that is
	 * an instance of given klass. Null if not found. */
	public static <T extends ISemanticElement> T getMatchingParent(ISemanticElement node, Class<T> klass) {
		assertNotNull(klass);
		
		
		while(true) {
			
			if(node == null) {
				return null;
			}
			
			if(klass.isInstance(node)) {
				return CoreUtil.<T>blindCast(node);
			}
			node = node.getParent();
		}
	}
	
	public static INamedElement getOuterNamedElement(ISemanticElement node) {
		return getNamedElementParent(node.getParent());
	}
	
	/** An optimized version of {@link #getMatchingParent(ISemanticElement, Class)}) for klass=INamedElement */
	public static INamedElement getNamedElementParent(ISemanticElement node) {
		while(true) {
			if(node == null) {
				return null;
			}
			if (node instanceof INamedElement) {
				return (INamedElement) node;
			}
			node = node.getParent();
		}
	}
	
}