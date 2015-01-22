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

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import melnorme.lang.tooling.context.ISemanticContext;
import melnorme.lang.tooling.engine.ElementSemantics;
import melnorme.lang.tooling.engine.ErrorElement;
import melnorme.lang.tooling.engine.PickedElement;
import melnorme.lang.tooling.symbols.IConcreteNamedElement;
import melnorme.lang.tooling.symbols.INamedElement;

public abstract class ReferenceSemantics extends ElementSemantics<ReferenceResult> {
	
	private final IReference reference;
	
	public ReferenceSemantics(IReference reference, PickedElement<?> pickedElement) {
		super(pickedElement);
		assertTrue(pickedElement.element == reference);
		this.reference = reference;
	}
	
	protected IResolvable getResolvable() {
		return reference;
	}
	
	/** @return non-null. */
	public final ReferenceResult resolveTargetElement() {
		return getElementResolution();
	}
	
	/** @return non-null. */
	public final INamedElement resolveTargetElement_() {
		return getElementResolution().result;
	}
	
	@Override
	protected final ReferenceResult createLoopResolution() {
		// TODO: test this path
		return new ReferenceResult(ErrorElement.newLoopError(reference, null));
	}
	
	@Override
	protected final ReferenceResult createResolution() {
		INamedElement result = doResolveTargetElement();
		
		if(result == null) {
			result = ErrorElement.newNotFoundError(reference);
		}
		
		return new ReferenceResult(result);
	}
	
	/** Finds the named element matching this {@link IResolvable}. 
	 * If no results are found, return null. */
	protected abstract INamedElement doResolveTargetElement();
	
	
	public abstract static class TypeReferenceSemantics extends ReferenceSemantics {
		
		public TypeReferenceSemantics(IReference reference, PickedElement<?> pickedElement) {
			super(reference, pickedElement);
		}
		
	}
	
	/* -----------------  ----------------- */
	
	public static INamedElement resolveReference(ISemanticContext context, IReference reference) {
		if(reference == null) {
			return null;
		}
		return reference.getSemantics(context).resolveTargetElement().result;
	}
	
	/* -----------------  ----------------- */
	
	public static IConcreteNamedElement resolveConcreteElement(IReference reference, ISemanticContext parentContext) {
		if(reference == null) {
			return null;
		}
		INamedElement refTarget = reference.getSemantics(parentContext).resolveTargetElement_();
		
		return refTarget.getSemantics(parentContext).resolveConcreteElement().result;
	}
	
}