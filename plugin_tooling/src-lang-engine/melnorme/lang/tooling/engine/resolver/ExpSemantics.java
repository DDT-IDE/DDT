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
import melnorme.lang.tooling.engine.ErrorElement.Invalid_TypeErrorElement;
import melnorme.lang.tooling.engine.PickedElement;

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
		return new TypeReferenceResult(ErrorElement.newLoopError(resolvable, null));
	}
	
	@Override
	protected final TypeReferenceResult createResolution() {
		TypeReferenceResult result = doCreateExpResolution();
		
		if(result == null) {
			return new TypeReferenceResult(new Invalid_TypeErrorElement("#InvalidExp", resolvable, null, null));
		}
		
		return result;
	}
	
	public abstract TypeReferenceResult doCreateExpResolution();
	
	
	/* -----------------  ----------------- */
	
	public TypeReferenceResult resolveTypeReference(IReference reference) {
		return ResolvableUtil.resolveTypeReference2(reference, context);
	}
	
}