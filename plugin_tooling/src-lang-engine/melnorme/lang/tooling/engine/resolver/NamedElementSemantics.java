/*******************************************************************************
 * Copyright (c) 2014 Bruno Medeiros and other Contributors.
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

public abstract class NamedElementSemantics extends ElementSemantics<ConcreteElementResult> 
	implements INamedElementSemanticData 
{
	
	protected final INamedElement element; 
	
	public NamedElementSemantics(INamedElement element, PickedElement<?> pickedElement) {
		super(pickedElement);
		assertTrue(pickedElement.element == element);		
		this.element = assertNotNull(element);
	}
	
	@Override
	public ConcreteElementResult resolveConcreteElement() {
		return getElementResolution();
	}
	
	@Override
	protected final ConcreteElementResult createResolution() {
		IConcreteNamedElement concreteElement = doResolveConcreteElement();
		if(concreteElement == null) {
			concreteElement = ErrorElement.newUnsupportedError(element, null);
		}
		return new ConcreteElementResult(concreteElement);
	}
	
	@Override
	protected ConcreteElementResult createLoopResolution() {
		return new ConcreteElementResult(ErrorElement.newLoopError(element, null));
	}
	
	/** @return null for unsupported element. */
	protected abstract IConcreteNamedElement doResolveConcreteElement();
	
	protected IConcreteNamedElement resolveConcreteElement(INamedElement namedElement) {
		if(namedElement == null) {
			return null;
		}
		return namedElement.resolveConcreteElement(context);
	}
	
	@Override
	public abstract void resolveSearchInMembersScope(CommonScopeLookup search);
	
	@Override
	public final INamedElement getTypeForValueContext() {
		INamedElement typeResult = getTypeForValueContext_do();
		if(typeResult != null) {
			return typeResult;
		} else {
			return ErrorElement.newUnsupportedError(element, null);
		}
	}
	
	public abstract INamedElement getTypeForValueContext_do();
	
}