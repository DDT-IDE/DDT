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
package melnorme.lang.tooling.engine.resolver;

import static melnorme.utilbox.misc.CollectionUtil.getFirstElementOrNull;
import melnorme.lang.tooling.bundles.ISemanticContext;
import melnorme.lang.tooling.engine.scoping.CommonScopeLookup;
import melnorme.lang.tooling.symbols.IConcreteNamedElement;
import melnorme.lang.tooling.symbols.INamedElement;
import dtool.ast.expressions.Resolvable;

public abstract class VarSemantics extends ConcreteElementSemantics {
	
	public VarSemantics(IConcreteNamedElement element) {
		super(element);
	}
	
	@Override
	public void resolveSearchInMembersScope(CommonScopeLookup search) {
		INamedElement effectiveType = resolveTypeForValueContext(search.modResolver);
		if(effectiveType != null) {
			effectiveType.resolveSearchInMembersScope(search);
		}
	}
	
	@Override
	public INamedElement resolveTypeForValueContext(ISemanticContext mr) {
		Resolvable declaredType = getTypeReference();
		if(declaredType != null) {
			// TODO: handle finding multiple elements
			return getFirstElementOrNull(declaredType.findTargetDefElements(mr));
		}
		return null;
	}
	
	protected abstract Resolvable getTypeReference();
	
}