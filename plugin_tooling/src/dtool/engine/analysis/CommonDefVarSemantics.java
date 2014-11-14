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
package dtool.engine.analysis;

import static melnorme.utilbox.misc.CollectionUtil.getFirstElementOrNull;
import melnorme.lang.tooling.bundles.IModuleResolver;
import melnorme.lang.tooling.engine.resolver.IValueNode;
import melnorme.lang.tooling.symbols.INamedElement;
import dtool.ast.expressions.IInitializer;
import dtool.ast.references.Reference;
import dtool.resolver.CommonDefUnitSearch;

public abstract class CommonDefVarSemantics {
	
	protected final IVarDefinitionLike varDef;
	
	public CommonDefVarSemantics(IVarDefinitionLike varDef) {
		this.varDef = varDef;
	}
	
	public void resolveSearchInMembersScope(CommonDefUnitSearch search) {
		INamedElement effectiveType = resolveEffectiveType(search.getModuleResolver());
		if(effectiveType != null) {
			effectiveType.resolveSearchInMembersScope(search);
		}
	}
	
	public INamedElement resolveEffectiveType(IModuleResolver mr) {
		Reference declaredType = varDef.getDeclaredType();
		if(declaredType != null) {
			return getFirstElementOrNull(declaredType.findTargetDefElements(mr, true));
		}
		IInitializer initializer = varDef.getDeclaredInitializer();
		if(initializer instanceof IValueNode) {
			IValueNode initializerR = (IValueNode) initializer;
			return getFirstElementOrNull(initializerR.resolveTypeOfUnderlyingValue(mr));
		}
		
		return null; // TODO: create error element
	}
	
}