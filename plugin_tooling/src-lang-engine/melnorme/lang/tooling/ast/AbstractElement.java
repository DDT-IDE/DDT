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

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;


public abstract class AbstractElement extends CommonLanguageElement {
	
	protected final ILanguageElement ownerElement; // can be null
	protected final CommonLanguageElement lexicalParent; // can be null
	private boolean isCompleted = false;
	
	public AbstractElement(ILanguageElement ownerElement, CommonLanguageElement lexicalParent, boolean isCompleted) {
		this.ownerElement = ownerElement;
		this.lexicalParent = lexicalParent;
		this.isCompleted = isCompleted;
	}
	
	@Override
	public CommonLanguageElement getLexicalParent() {
		return lexicalParent;
	}
	
	@Override
	public ILanguageElement getOwnerElement() {
		return ownerElement;
	}
	
	public void setCompleted() {
		assertTrue(isCompleted == false);
		doSetCompleted();
		isCompleted = true;
	}
	
	protected abstract void doSetCompleted();
	
	@Override
	public boolean isCompleted() {
		return isCompleted;
	}
	
}