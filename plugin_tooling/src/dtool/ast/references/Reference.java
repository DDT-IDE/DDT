/*******************************************************************************
 * Copyright (c) 2011, 2014 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package dtool.ast.references;

import java.util.ArrayList;
import java.util.Collection;

import dtool.ast.definitions.INamedElement;
import dtool.ast.expressions.Resolvable;
import dtool.engine.modules.IModuleResolver;
import dtool.resolver.IResolvable;

/**
 * Common class for entity references.
 */
public abstract class Reference extends Resolvable implements IResolvable {
	
	@Override
	public abstract Collection<INamedElement> findTargetDefElements(IModuleResolver mr, boolean findFirstOnly);
	
	@Override
	public Collection<INamedElement> resolveTypeOfUnderlyingValue(IModuleResolver mr) {
		Collection<INamedElement> resolvedElements = findTargetDefElements(mr, false);
		
		ArrayList<INamedElement> resolvedTypeForValueContext = new ArrayList<>();
		for (INamedElement defElement : resolvedElements) {
			INamedElement resolveTypeForValueContext = defElement.resolveTypeForValueContext(mr);
			if(resolvedTypeForValueContext != null) {
				resolvedTypeForValueContext.add(resolveTypeForValueContext);
			}
		}
		return resolvedTypeForValueContext; 
	}
	
	protected static Collection<INamedElement> resolveToInvalidValue() {
		return null; 
	}
	
}