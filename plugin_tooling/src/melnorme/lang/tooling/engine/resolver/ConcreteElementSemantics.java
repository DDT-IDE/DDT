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

import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import melnorme.lang.tooling.bundles.ISemanticContext;
import melnorme.lang.tooling.engine.ElementResolution;
import melnorme.lang.tooling.symbols.IConcreteNamedElement;
import melnorme.lang.tooling.symbols.INamedElement;


class ConcreteElementResolution extends ElementResolution<IConcreteNamedElement> {
	
	public ConcreteElementResolution(IConcreteNamedElement concreTarget) {
		super(concreTarget);
	}
	
	public INamedElement getConcreteTarget() {
		return result;
	}
}

public abstract class ConcreteElementSemantics extends NamedElementSemantics<ConcreteElementResolution> {
	
	protected final ConcreteElementResolution elementRes; 
	
	public ConcreteElementSemantics(IConcreteNamedElement concreteElement) {
		super(concreteElement);
		this.elementRes = new ConcreteElementResolution(concreteElement);
	}
	
	@Override
	public final ElementResolution<IConcreteNamedElement> resolveConcreteElement(ISemanticContext sr) {
		return elementRes;
	}
	
	@Override
	protected ConcreteElementResolution getOrCreateElementResolution(ISemanticContext context) {
		return elementRes;
	}
	
	@Override
	protected final ConcreteElementResolution createResolution(ISemanticContext context) {
		throw assertFail();
	}
	
}