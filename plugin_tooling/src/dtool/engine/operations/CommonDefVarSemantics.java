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
package dtool.engine.operations;

import java.util.Collection;

import melnorme.utilbox.misc.CollectionUtil;
import dtool.ast.definitions.DefinitionVariable;
import dtool.ast.definitions.INamedElement;
import dtool.ast.expressions.IInitializer;
import dtool.ast.references.Reference;
import dtool.engine.modules.IModuleResolver;
import dtool.resolver.CommonDefUnitSearch;
import dtool.resolver.IResolvable;

public abstract class CommonDefVarSemantics {
	
	protected final IVarDefinitionLike varDef;
	
	public CommonDefVarSemantics(IVarDefinitionLike varDef) {
		this.varDef = varDef;
	}
	
	public void resolveSearchInMembersScope(CommonDefUnitSearch search) {
		DefinitionVariable.resolveSearchInReferredContainer(search, getEffectiveType());
	}
	
	protected IResolvable getEffectiveType() {
		return varDef.getEffectiveType();
	}
	
	public INamedElement resolveEffectiveType(IModuleResolver mr) {
		return resolveEffectiveType(mr, getEffectiveType());
	}
	
	public static IResolvable getEffectiveType(Reference typeRef, IInitializer initializer) {
		if(typeRef != null) 
			return typeRef;
		if(initializer instanceof IResolvable)
			return (IResolvable) initializer;
		return null;
	}
	
	public static INamedElement resolveEffectiveType(IModuleResolver mr, IResolvable effectiveType) {
		if(effectiveType == null) 
			return null;
		Collection<INamedElement> target = effectiveType.findTargetDefElements(mr, true);
		INamedElement firstElement = CollectionUtil.getFirstElementOrNull(target);
		
		if(firstElement == null) {
			return firstElement;
		}
		return firstElement;
	}
	
}