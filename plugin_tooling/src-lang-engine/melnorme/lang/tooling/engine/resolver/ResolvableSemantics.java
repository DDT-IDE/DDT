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
import static melnorme.utilbox.core.CoreUtil.nullToEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import melnorme.lang.tooling.context.ISemanticContext;
import melnorme.lang.tooling.engine.ElementSemantics;
import melnorme.lang.tooling.engine.ErrorElement;
import melnorme.lang.tooling.engine.PickedElement;
import melnorme.lang.tooling.symbols.INamedElement;
import melnorme.utilbox.collections.ArrayList2;

public abstract class ResolvableSemantics extends ElementSemantics<ResolvableResult> {
	
	private final IResolvable resolvable;
	
	public ResolvableSemantics(IResolvable resolvable, PickedElement<?> pickedElement) {
		super(pickedElement);
		assertTrue(pickedElement.element == resolvable);
		this.resolvable = resolvable;
	}
	
	protected IResolvable getResolvable() {
		return resolvable;
	}
	
	public final ResolvableResult resolveTargetElement() {
		return getElementResolution();
	}
	
	@Override
	protected ResolvableResult createLoopResolution() {
		// TODO: test this path
		return new ResolvableResult(ErrorElement.newLoopError(resolvable, null));
	}
	
	@Override
	protected ResolvableResult createResolution() {
		INamedElement result = doResolveTargetElement();
		
		if(result == null) {
			result = ErrorElement.newNotFoundError(resolvable);
		}
		
		return new ResolvableResult(result);
	}
	
	/** Finds the named element matching this {@link IResolvable}. 
	 * If no results are found, return null. */
	protected abstract INamedElement doResolveTargetElement();
	
	
	/* FIXME: return a single element. */
	public Collection<INamedElement> resolveTypeOfUnderlyingValue() {
		INamedElement target = this.resolveTargetElement().result;
		
		INamedElement resolvedType = null;
		if(target != null) {
			resolvedType = target.resolveTypeForValueContext(context);
		}
		
		return resultToColl(resolvedType);
	}

	protected static Collection<INamedElement> resultToColl(INamedElement resolvedType) {
		if(resolvedType != null) {
			return new ArrayList2<>(resolvedType);
		} else {
			return Collections.EMPTY_LIST;
		}
	}
	
	
	@Deprecated
	public static Collection<INamedElement> resolveTypeOfUnderlyingValue(ISemanticContext mr, 
		Collection<INamedElement> resolvedElements) {
		ArrayList<INamedElement> resolvedTypeForValueContext = new ArrayList<>();
		for (INamedElement defElement : nullToEmpty(resolvedElements)) {
			INamedElement resolveTypeForValueContext = defElement.resolveTypeForValueContext(mr);
			if(resolvedTypeForValueContext != null) {
				resolvedTypeForValueContext.add(resolveTypeForValueContext);
			}
		}
		return resolvedTypeForValueContext;
	}
	
	
	protected Collection<INamedElement> resolveToInvalidValue() {
		return null; // TODO
	}
	
	public abstract static class TypeReferenceSemantics extends ResolvableSemantics {
		
		public TypeReferenceSemantics(IResolvable resolvable, PickedElement<?> pickedElement) {
			super(resolvable, pickedElement);
		}
		
		@Override
		public Collection<INamedElement> resolveTypeOfUnderlyingValue() {
			return resolveToInvalidValue();
		}
		
	}
	
	public abstract static class ExpSemantics extends ResolvableSemantics {
		
		public ExpSemantics(IResolvable resolvable, PickedElement<?> pickedElement) {
			super(resolvable, pickedElement);
		}
		
		@Override
		public abstract INamedElement doResolveTargetElement();
		
		@Override
		public Collection<INamedElement> resolveTypeOfUnderlyingValue() {
			return resultToColl(doResolveTargetElement()); // TODO need to review this
		}
		
	}
	
}