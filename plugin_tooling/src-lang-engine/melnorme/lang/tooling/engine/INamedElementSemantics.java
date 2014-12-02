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

import melnorme.lang.tooling.context.ISemanticContext;
import melnorme.lang.tooling.engine.scoping.CommonScopeLookup;
import melnorme.lang.tooling.symbols.IConcreteNamedElement;
import melnorme.lang.tooling.symbols.INamedElement;


public interface INamedElementSemantics extends IElementSemantics {
	
	ElementResolution<? extends IConcreteNamedElement> resolveConcreteElement(ISemanticContext sr);
	
	void resolveSearchInMembersScope(CommonScopeLookup search);
	
	/* FIXME: review this API */
	INamedElement resolveTypeForValueContext(ISemanticContext mr);
	
}