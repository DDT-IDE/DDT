/*******************************************************************************
 * Copyright (c) 2013, 2014 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package melnorme.lang.tooling.engine.scoping;

import melnorme.lang.tooling.symbols.INamedElement;
import melnorme.utilbox.collections.ArrayList2;
import melnorme.utilbox.collections.Indexable;

public class NamedElementsScope implements IScopeElement {
	
	protected final Indexable<? extends INamedElement> members;
	
	public NamedElementsScope(INamedElement... member) {
		this.members = new ArrayList2<>(member); 
	}
	
	public NamedElementsScope(ArrayList2<? extends INamedElement> members) {
		this.members = members; 
	}
	
	@Override
	public Iterable<? extends INamedElement> getScopeNodeList() {
		return members;
	}
	
	@Override
	public boolean allowsForwardReferences() {
		return true;
	}
	
}