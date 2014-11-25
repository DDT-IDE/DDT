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

import melnorme.lang.tooling.context.ISemanticContext;
import melnorme.lang.tooling.engine.ElementResolution;
import melnorme.lang.tooling.engine.INamedElementSemantics;
import melnorme.lang.tooling.engine.scoping.CommonScopeLookup;
import melnorme.lang.tooling.symbols.IConcreteNamedElement;
import melnorme.lang.tooling.symbols.INamedElement;

/**
 * Does nothing.
 */
public class NullNamedElementSemantics extends NullElementSemantics implements INamedElementSemantics {
	
	@Override
	public ElementResolution<IConcreteNamedElement> resolveConcreteElement(ISemanticContext sr) {
		return null; // /*FIXME: BUG here*/
	}
	
	@Override
	public INamedElement resolveTypeForValueContext(ISemanticContext mr) {
		return null;
	}
	
	@Override
	public void resolveSearchInMembersScope(CommonScopeLookup search) {
	}
	
}