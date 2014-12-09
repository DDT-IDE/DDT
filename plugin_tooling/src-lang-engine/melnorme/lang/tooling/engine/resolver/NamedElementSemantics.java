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


import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import melnorme.lang.tooling.engine.ElementSemantics;
import melnorme.lang.tooling.engine.ErrorElement;
import melnorme.lang.tooling.engine.PickedElement;
import melnorme.lang.tooling.engine.scoping.CommonScopeLookup;
import melnorme.lang.tooling.symbols.IConcreteNamedElement;
import melnorme.lang.tooling.symbols.INamedElement;

public abstract class NamedElementSemantics extends ElementSemantics<ConcreteElementResult> {
	
	protected final INamedElement element; 
	
	public NamedElementSemantics(INamedElement element, PickedElement<?> pickedElement) {
		super(pickedElement);
		assertTrue(pickedElement.element == element);		
		this.element = assertNotNull(element);
	}
	
	public ConcreteElementResult resolveConcreteElement() {
		return getElementResolution();
	}
	
	@Override
	protected final ConcreteElementResult createResolution() {
		return new ConcreteElementResult(doResolveConcreteElement());
	}
	
	@Override
	protected ConcreteElementResult createLoopResolution() {
		return new ConcreteElementResult(ErrorElement.newLoopError(element, null));
	}
	
	protected abstract IConcreteNamedElement doResolveConcreteElement();
	
	public abstract void resolveSearchInMembersScope(CommonScopeLookup search);
	
	/* FIXME: review this API */
	public abstract INamedElement resolveTypeForValueContext();
	
}