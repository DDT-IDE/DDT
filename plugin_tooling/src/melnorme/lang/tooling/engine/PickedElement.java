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
package melnorme.lang.tooling.engine;

import melnorme.lang.tooling.ast.ISemanticElement;
import melnorme.lang.tooling.bundles.ISemanticContext;

/**
 * A {@link ISemanticElement} with an attached semantic context.
 * The attached context is where the given element will store semantic resolution info,
 * as such the element must have been created from the corresponding context.
 */
public class PickedElement<E extends ISemanticElement> {
	
	public static <E extends ISemanticElement> PickedElement<E> create(E element, ISemanticContext context) {
		return new PickedElement<E>(element, context);
	}
	
	public final E element;
	public final ISemanticContext context;
	
	public PickedElement(E element, ISemanticContext context) {
		this.element = element;
		this.context = context;
	}
	
}