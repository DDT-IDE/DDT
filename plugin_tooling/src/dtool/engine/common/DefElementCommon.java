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

import melnorme.lang.tooling.ast_actual.ILangNamedElement;
import melnorme.utilbox.misc.CollectionUtil;
import dtool.ast.references.Reference;
import dtool.engine.modules.IModuleResolver;

public class DefElementCommon {
	
	public static ILangNamedElement returnError_ElementIsNotAValue(ILangNamedElement defElement) {
		return new NotAValueErrorElement(defElement);
	}
	
	public static ILangNamedElement resolveTypeForValueContext(IModuleResolver mr, Reference reference) {
		if(reference == null) {
			return null;
		}
		return reference.findTargetDefElement(mr);
	}
	
	public static ILangNamedElement resolveTypeForValueContext_Alias(IModuleResolver mr, Reference alias) {
		Reference aliasTarget = alias;
		if(aliasTarget != null) {
			return CollectionUtil.getFirstElementOrNull(aliasTarget.resolveTypeOfUnderlyingValue(mr));
		}
		return null;
	}
	
}