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

import java.util.Collection;

import melnorme.lang.tooling.bundles.ISemanticContext;
import melnorme.lang.tooling.engine.IElementSemantics;
import melnorme.lang.tooling.symbols.INamedElement;

public interface IResolvableSemantics extends IElementSemantics {
	
	ResolvableResult resolveTargetElement(ISemanticContext sr);
	
	/* FIXME: TODO: deprecate these: */
	Collection<INamedElement> findTargetDefElements(ISemanticContext moduleResolver, boolean findOneOnly);
	Collection<INamedElement> resolveTypeOfUnderlyingValue(ISemanticContext mr);
	
}