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
package dtool.engine.common;

import melnorme.utilbox.misc.StringUtil;
import dtool.ast.ASTNode;
import dtool.ast.definitions.INamedElement;
import dtool.engine.modules.ElementName;

public class ScopeSemantics {

	public static INamedElement findElement(ASTNode moduleNode, String elementNameLocator) {
		String segmentName = StringUtil.substringUntilMatch(elementNameLocator, ElementName.NAME_SEP); 
		String restOfName = StringUtil.segmentAfterMatch(elementNameLocator, ElementName.NAME_SEP);
		
		// FIXME: should iterate over visible INamedElements, use search mechanism
		for (ASTNode childNode : moduleNode.getChildren()) {
			if(childNode instanceof INamedElement) {
				INamedElement namedElement = (INamedElement) childNode;
				if(namedElement.getExtendedName().equals(segmentName)) {
					if(restOfName != null) {
						return findElement(childNode, restOfName);
					} else {
						return namedElement;
					}
				}
			}
		}
		
		return null;
	}
	
}