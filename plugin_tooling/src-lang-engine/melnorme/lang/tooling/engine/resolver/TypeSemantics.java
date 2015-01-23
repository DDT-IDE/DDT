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

import melnorme.lang.tooling.engine.PickedElement;
import melnorme.lang.tooling.engine.scoping.CommonScopeLookup;
import melnorme.lang.tooling.engine.scoping.IScopeElement;
import melnorme.lang.tooling.symbols.IConcreteNamedElement;
import melnorme.lang.tooling.symbols.INamedElement;

public class TypeSemantics extends NonValueConcreteElementSemantics {
	
	protected final IScopeElement membersScope; // Can be null
	
	public TypeSemantics(IConcreteNamedElement typeElement, PickedElement<?> pickedElement, 
			IScopeElement membersScope) {
		super(typeElement, pickedElement);
		this.membersScope = membersScope;
	}
	
	protected final IConcreteNamedElement getTypeElement() {
		return elementRes.result;
	}
	
	@Override
	public void resolveSearchInMembersScope(CommonScopeLookup search) {
		search.evaluateScope(getMembersScope());
	}
	
	public IScopeElement getMembersScope() {
		return membersScope;
	}
	
	/* -----------------  ----------------- */
	
	public boolean isCompatibleWith(INamedElement other) {
		// TODO: need to properly check if it's a subtype, etc.,
		return element == other;
	}
	
}