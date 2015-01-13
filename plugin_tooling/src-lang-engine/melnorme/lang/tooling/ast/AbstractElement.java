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


public abstract class AbstractElement extends CommonLanguageElement {
	
	protected final ILanguageElement ownerElement; // can be null
	protected final ILanguageElement lexicalParent; // can be null
	
	public AbstractElement(ILanguageElement ownerElement, ILanguageElement lexicalParent) {
		this.ownerElement = ownerElement;
		this.lexicalParent = lexicalParent;
	}
	
	@Override
	public ILanguageElement getLexicalParent() {
		return lexicalParent;
	}
	
	@Override
	public ILanguageElement getOwnerElement() {
		return ownerElement;
	}
	
}