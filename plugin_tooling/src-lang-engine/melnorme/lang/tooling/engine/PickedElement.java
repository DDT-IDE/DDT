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

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import melnorme.lang.tooling.ast.ISemanticElement;
import melnorme.lang.tooling.context.ISemanticContext;

/**
 * A {@link ISemanticElement} with an attached semantic context.
 * The attached context is where the given element will store semantic resolution info,
 * as such the element must have been created from the corresponding context.
 */
public class PickedElement<E extends ISemanticElement> {
	
	public final E element;
	public final ISemanticContext context;
	
	public PickedElement(E element, ISemanticContext context) {
		assertTrue(element.getContextForThisElement(context) == context);
		this.element = element;
		this.context = context;
	}
	
}