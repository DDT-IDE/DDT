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
import melnorme.lang.tooling.ast.ILanguageElement;
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
	public abstract INamedElement resolveTypeForValueContext();
	
	/* -----------------  ----------------- */
	
	public static class NotAValueErrorElement extends ErrorElement {
		
		public static final String ERROR_IS_NOT_A_VALUE = "#NotAValue";
		
		public final INamedElement invalidElement;
		
		public NotAValueErrorElement(INamedElement invalidElement) {
			this(invalidElement, invalidElement); 
		}
		
		public NotAValueErrorElement(ILanguageElement owner, INamedElement invalidElement) {
			super(ERROR_IS_NOT_A_VALUE, owner, 
				quoteDoc("Element does not have a value: " + invalidElement.getFullyQualifiedName()));
			this.invalidElement = invalidElement;
		}
		
		@Override
		public String getExtendedName() {
			return getName() + ":" + invalidElement.getFullyQualifiedName();
		}
		
		@Override
		public String toString() {
			return getExtendedName();
		}
		
		/* -----------------  ----------------- */
		
		@Override
		public NamedElementSemantics doCreateSemantics(PickedElement<?> pickedElement) {
			return new ErrorElement.ErrorNamedElementSemantics(this, pickedElement) {
				@Override
				public INamedElement resolveTypeForValueContext() {
					// Do nothing.
					return null;
				}
			};
		}
	}
	
}