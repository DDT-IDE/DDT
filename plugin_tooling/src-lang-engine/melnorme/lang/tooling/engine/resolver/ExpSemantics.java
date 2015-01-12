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

import java.util.Collection;

import melnorme.lang.tooling.engine.PickedElement;
import melnorme.lang.tooling.symbols.INamedElement;

public abstract class ExpSemantics extends ResolvableSemantics {
	
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