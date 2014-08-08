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

import melnorme.utilbox.misc.CollectionUtil;
import dtool.ast.definitions.INamedElement;
import dtool.ast.references.Reference;
import dtool.engine.modules.IModuleResolver;

public class DefElementCommon {
	
	public static INamedElement returnError_ElementIsNotAValue(INamedElement defElement) {
		return new NotAValueErrorElement(defElement);
	}
	
	public static INamedElement resolveTypeForValueContext(IModuleResolver mr, Reference reference) {
		if(reference == null) {
			return null;
		}
		return reference.findTargetDefElement(mr);
	}
	
	public static INamedElement resolveTypeForValueContext_AliasBUG(IModuleResolver mr, Reference alias) {
		/*BUG here*/
		return resolveTypeForValueContext(mr, alias);
	}
	
	public static INamedElement resolveTypeForValueContext_Alias(IModuleResolver mr, Reference alias) {
		Reference aliasTarget = alias;
		if(aliasTarget != null) {
			return CollectionUtil.getFirstElementOrNull(aliasTarget.resolveTypeOfUnderlyingValue(mr));
		}
		return null;
	}
	
}