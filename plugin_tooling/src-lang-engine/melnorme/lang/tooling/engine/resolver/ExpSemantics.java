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

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import melnorme.lang.tooling.engine.ElementSemantics;
import melnorme.lang.tooling.engine.ErrorElement;
import melnorme.lang.tooling.engine.PickedElement;
import melnorme.lang.tooling.symbols.IConcreteNamedElement;
import melnorme.lang.tooling.symbols.INamedElement;
import melnorme.lang.tooling.symbols.ITypeNamedElement;

public abstract class ExpSemantics extends ElementSemantics<TypeReferenceResult> {
	
	protected final IResolvable resolvable;
	
	public ExpSemantics(IResolvable resolvable, PickedElement<?> pickedElement) {
		super(pickedElement);
		assertTrue(pickedElement.element == resolvable);
		this.resolvable = resolvable;
	}
	
	/** @return non-null. */
	public final TypeReferenceResult resolveTypeOfUnderlyingValue() {
		return getElementResolution();
	}
	
	@Override
	protected final TypeReferenceResult createLoopResolution() {
		// TODO: test this path
		return concreteTypeResult(ErrorElement.newLoopError(resolvable, null));
	}
	
	@Override
	protected final TypeReferenceResult createResolution() {
		TypeReferenceResult result = doCreateExpResolution();
		
		if(result == null) {
			return concreteTypeResult(new ErrorElement("#InvalidExp", null, resolvable, null));
		}
		
		return result;
	}
	
	public static TypeReferenceResult concreteTypeResult(ITypeNamedElement typeElement) {
		return new TypeReferenceResult(typeElement, typeElement);
	}
	
	public abstract TypeReferenceResult doCreateExpResolution();
	
	
	/* -----------------  ----------------- */
	
	/**
	 * Resolve a reference that should point to a var element 
	 * (or a similar element that has an expression value).
	 */
	public TypeReferenceResult resolveTypeOfExpressionReference(IReference reference) {
		if(reference == null) {
			return null;
		}
		INamedElement expElement = reference.getSemantics(context).resolveTargetElement().result;
		
		INamedElement originalType = expElement.resolveTypeForValueContext(context);
		return concreteTypeResult(reference, originalType);
	}
	
	/**
	 * Resolve a reference that should point to a type element.
	 */
	public TypeReferenceResult resolveTypeReference(IReference reference) {
		if(reference == null) {
			return null;
		}
		INamedElement originalType = reference.getSemantics(context).resolveTargetElement().result;
		return concreteTypeResult(reference, originalType);
	}
	
	protected TypeReferenceResult concreteTypeResult(IReference reference, INamedElement originalType) {
		if(originalType == null) {
			return concreteTypeResult(ErrorElement.newNotFoundError(reference));
		}
		
		IConcreteNamedElement concreteResult = originalType.resolveConcreteElement(context);
		
		ITypeNamedElement concreteType;
		if(concreteResult instanceof ITypeNamedElement) {
			concreteType = (ITypeNamedElement) concreteResult;
		} else {
			concreteType = new NamedElementSemantics.NotAValueErrorElement(reference, concreteResult);
		}
		return new TypeReferenceResult(originalType, concreteType);
	}
	
}