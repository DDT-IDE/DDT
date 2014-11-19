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

import melnorme.lang.tooling.bundles.IModuleResolver;
import melnorme.lang.tooling.bundles.ISemanticResolution;
import melnorme.lang.tooling.symbols.IConcreteNamedElement;
import melnorme.lang.tooling.symbols.INamedElement;
import dtool.resolver.CommonDefUnitSearch;

/**
 * Does nothing.
 */
public class NullElementSemantics extends AbstractNamedElementSemantics {
	
	@Override
	public IConcreteNamedElement resolveConcreteElement(ISemanticResolution sr) {
		return null; // /*FIXME: BUG here*/
	}
	
	@Override
	public INamedElement resolveTypeForValueContext(IModuleResolver mr) {
		return null;
	}
	
	@Override
	public void resolveSearchInMembersScope(CommonDefUnitSearch search) {
	}
	
}