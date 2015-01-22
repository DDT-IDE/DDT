/*******************************************************************************
 * Copyright (c) 2015, 2015 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package melnorme.lang.tooling.engine.resolver;


import melnorme.lang.tooling.engine.PickedElement;
import melnorme.lang.tooling.engine.scoping.CommonScopeLookup;
import melnorme.lang.tooling.symbols.IConcreteNamedElement;
import melnorme.lang.tooling.symbols.INamedElement;

public abstract class CommonVarSemantics extends ConcreteElementSemantics {
	
	public CommonVarSemantics(IConcreteNamedElement concreteElement, PickedElement<?> pickedElement) {
		super(concreteElement, pickedElement);
	}
	
	@Override
	public final void resolveSearchInMembersScope(CommonScopeLookup search) {
		/* FIXME: should be ITypeNamedElement */
		INamedElement effectiveType = resolveTypeForValueContext();
		search.evaluateInMembersScope(resolveConcreteElement(effectiveType));
	}
	
}