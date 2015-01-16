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
import melnorme.lang.tooling.ast.ILanguageElement;
import melnorme.lang.tooling.symbols.INamedElement;
import melnorme.utilbox.core.CoreUtil;

public class NodeElementUtil {
	
	public static boolean isContainedIn(ILanguageElement node, ILanguageElement container) {
		while(node != null) {
			if(node == container) {
				return true;
			}
			node = node.getLexicalParent();
		}
		return false;
	}
	
	/** @return the innermost node in the parent chain of given node (inclusive), that is
	 * an instance of given klass. Null if not found. */
	public static <T extends ILanguageElement> T getMatchingParent(ILanguageElement node, Class<T> klass) {
		assertNotNull(klass);
		
		
		while(true) {
			
			if(node == null) {
				return null;
			}
			
			if(klass.isInstance(node)) {
				return CoreUtil.<T>blindCast(node);
			}
			node = node.getLexicalParent();
		}
	}
	
	public static INamedElement getOuterNamedElement(ILanguageElement node) {
		return getNearestNamedElement(node.getLexicalParent());
	}
	
	/** An optimized version of {@link #getMatchingParent(ILanguageElement, Class)}) for klass=INamedElement */
	public static INamedElement getNearestNamedElement(ILanguageElement node) {
		while(true) {
			if(node == null) {
				return null;
			}
			if (node instanceof INamedElement) {
				return (INamedElement) node;
			}
			node = node.getLexicalParent();
		}
	}
	
}