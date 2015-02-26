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
package melnorme.lang.tooling.ast;



public abstract class NonSourceElement extends CommonLanguageElement {
	
	protected final ILanguageElement ownerElement; // can be null
	
	public NonSourceElement(ILanguageElement ownerElement, CommonLanguageElement lexicalParent) {
		this.ownerElement = ownerElement;
		setParent(lexicalParent);
	}
	
	@Override
	public ILanguageElement getOwnerElement() {
		return ownerElement;
	}
	
	@Override
	protected abstract void doSetElementSemanticReady();
	
}